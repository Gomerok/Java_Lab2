<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="my" tagdir="/WEB-INF/tags"%>
<%-- Обработать параметр сортировки --%>
<c:if test="${param.sort!=null}">
	<c:set var="sort" scope="session" value="${param.sort}" />
</c:if>
<%-- Обработать параметр направления сортировки --%>
<c:if test="${param.dir!=null}">
	<c:set var="dir" scope="session" value="${param.dir}" />
</c:if>
<%-- Общая декоративная "шапка" для всех страниц --%>
<div style="background-color: greenyellow; padding: 29px;">
	<img src="imr.jpg" width="75" height="75" border="0" align="left">
	<div
		style="font-family: 'Trebuchet'; font-size: 30px; height: 65px; margin-left: 40px;margin-right: 40px;text-align: center;">
		Доска объявлений <br>Купля/продажа/обмен велосипедами</div>
</div>
<%-- Панель отображается если пользователь аутентифицирован --%>
<c:if test="${sessionScope.authUser!=null}">
	<div style="background-color: silver; padding: 5px">
		<div style="float: right; margin-right: 5px">
			[ <a href="<c:url value="/doLogout.jsp" />">Выйти</a> ]
		</div>
		Вы вошли как:
		<c:out value="${sessionScope.authUser.name}" />
		(
		<c:out value="${sessionScope.authUser.login}" />
		)
	</div>
</c:if>