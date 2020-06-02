package bsu.rfe.java.group9.lab7.Gomerov.varA;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";

    private static final int FRAME_MINIMUM_WIDTH = 600;
    private static final int FRAME_MINIMUM_HEIGHT = 600;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private String date;

    public MainFrame() {

        super(FRAME_TITLE);

        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));

        // Центрирование окна
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);

        // Текстовая область для отображения полученных сообщений
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);

        // Контейнер, обеспечивающий прокрутку текстовой области
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);

        final JLabel labelFrom = new JLabel("От");
        final JLabel labelTo = new JLabel("Получатель");

        // Поля ввода имени пользователя и адреса получателя
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

        // Текстовая область для ввода сообщения
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);

        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));

        final JButton sendButton = new JButton("Отправить");


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                date=getDateTime();
                sendMessage();}
        });

        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout2
                                .createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());

        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom).addComponent(labelTo).addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());

        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

        // Создание и запуск потока-обработчика запросов
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());
                        // Читаем имя отправителя
                        final String senderName = in.readUTF();
                        // Читаем сообщение
                        final String message = in.readUTF();
                        // Закрываем соединение
                        final String Dat = in.readUTF();
                        socket.close();

                        // Выделяем IP-адрес
                        final String address =((InetSocketAddress) socket
                                .getRemoteSocketAddress()).getAddress().getHostAddress();
                        // Выводим сообщение в текстовую область
                        textAreaIncoming.append(Dat+"  "+senderName + " (" + address + "): " + message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в работе сервера",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }




    private void sendMessage() {
        try {
            // Получаем необходимые параметры
            final String senderName = textFieldFrom.getText();
            final String destinationAddress = textFieldTo.getText();
            final String message = textAreaOutgoing.getText();
            final String Datt = date;


            // проверка на правильность IP
            if(!destinationAddress.isEmpty()){

                Pattern p = Pattern.compile("[0-9][0-9]?[0-9]?.{1}[0-9][0-9]?[0-9]?.{1}[0-9][0-9]?[0-9]?.{1}[0-9][0-9]?[0-9]?");
                Matcher m = p.matcher(destinationAddress);
                boolean b = m.matches();


                if(!b) {

                    JOptionPane.showMessageDialog(this, "Некоректно введен IP" , "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    textFieldTo.grabFocus();
                    return;
                }

            }

            // Убеждаемся, что поля не пустые
            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя отправителя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textFieldFrom.grabFocus();
                return;
            }

            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите адрес узла-получателя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textFieldTo.grabFocus();
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textAreaOutgoing.grabFocus();
                return;
            }


            // Создаем сокет для соединения
            final Socket socket = new Socket(destinationAddress, SERVER_PORT);
            // Открываем поток вывода данных
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Записываем в поток данные
            out.writeUTF(senderName);
            out.writeUTF(message);
            out.writeUTF(Datt);
            socket.close();

            // Помещаем сообщения в текстовую область вывода
            textAreaIncoming.append(Datt+"  Я -> (" + destinationAddress + ") : " + message + "\n");
            // Очищаем текстовую область ввода сообщения
            textAreaOutgoing.setText("");

        }

        catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Не удалось отправить сообщение: узел-адресат не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение",
                    "Ошибка",JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
    private String getDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date date = new Date();

        return dateFormat.format(date);

    }
}
