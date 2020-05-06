<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="languages.title"/></title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value='/resources/favicon/favicon.ico'/>"/>
    <link href="<c:url value='/resources/css/general.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/navigationBar.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/elementsList.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/element.css'/>" type="text/css" rel="stylesheet"/>
</head>
<body>
<div class="wrapper">
    <c:import url="/WEB-INF/jsp/navigation/navigationBar.jsp"/>
    <div class="main-content">
        <div class="flex-center">
            <div class="fw-100 title-container">
                <spring:message code="menu.languages"/>
            </div>
        </div>
        <div>
            <spring:message code="search.languages.hint" var="search_hint"/>
            <c:set var="hint" value="${search_hint}" scope="request"/>
            <c:import url="/WEB-INF/jsp/navigation/itemNavigationBar.jsp"/>
        </div>
        <c:choose>
            <c:when test="${languages.size() > 0}">
                <div class="all-elements-grid main-grid">
                    <c:forEach var="language" items="${languages}">
                        <c:set var="element" value="${language}" scope="request"/>
                        <c:set var="context" value="languages" scope="request"/>
                        <c:set var="cssClass" value="element-container-main" scope="request"/>
                        <c:import url="/WEB-INF/jsp/tagAndLanguages/element.jsp"/>
                    </c:forEach>
                </div>
                <div class="flex-row flex-wrap flex-center flex-grow">
                    <c:import url="/WEB-INF/jsp/navigation/navigationPage.jsp"/>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-elements flex-center fw-100">
                    <spring:message code="feed.no-languages"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>