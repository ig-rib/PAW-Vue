<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title><spring:message code="tag.title" arguments="${tag.name.toUpperCase()}"/></title>
    <link rel="shortcut icon" type="image/x-icon" href="<c:url value='/resources/favicon/favicon.ico'/>"/>
    <link href="<c:url value='/resources/css/navigationBar.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/elementSnippets.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/general.css'/>" type="text/css" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap" rel="stylesheet">
</head>
<body>
    <div class="wrapper">
        <c:import url="/WEB-INF/jsp/navigation/navigationBar.jsp"/>
        <div class="main-content">
            <div class="flex-row flex-center element-snippets-top-container">
                <div class="fw-100 title-container">
                    <spring:message code="tagSnippets.title" arguments="${tag.name.toUpperCase()}"/>
                </div>
                <c:if test="${currentUser != null}">
                    <c:choose>
                        <c:when test="${!follows}">
                            <a href="<c:url value='/tags/${tag.id}/follow'/>" class="tag-snippets-button border-radius flex-center no-text-decoration">
                                <spring:message code="tags.follow"/>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/tags/${tag.id}/unfollow'/>" class="tag-snippets-button border-radius flex-center no-text-decoration">
                                <spring:message code="tags.unfollow"/>
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${currentUser.username == 'admin'}">
                        <a href="<c:url value='/tags/${tag.id}/delete'/>" class="flex-center no-text-decoration">
                            <div class="delete-element-icon material-icons border-radius">delete</div>
                        </a>
                    </c:if>
                </c:if>
            </div>
            <c:set var="snippetList" value="${snippets}" scope="request"/>
            <c:import url="/WEB-INF/jsp/snippet/snippetFeed.jsp"/>
        </div>
    </div>
</body>
</html>
