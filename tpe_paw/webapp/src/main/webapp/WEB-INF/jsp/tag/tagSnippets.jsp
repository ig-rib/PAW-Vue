<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="<c:url value='/resources/css/navigationBar.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/tagSnippets.css'/>" type="text/css" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/general.css'/>" type="text/css" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap" rel="stylesheet">
</head>
<body>
    <div class="wrapper">
        <c:import url="/WEB-INF/jsp/navBar/navigationBar.jsp"/>
        <div class="main-content">
            <div class="tag-snippets-title-line">
                <div class="tag-snippets-title">
                    <spring:message code="tagSnippets.title" arguments="${tag.name}"/>
                </div>
                <div class="tag-snippets-button">
                    <c:choose>
                        <c:when test="${!follows}">
                            <a href="<c:url value='/tags/${tag.id}/follow'/>" class="follow-button-container">
                                <div>
                                    <spring:message code="tags.follow"/>
                                </div>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/tags/${tag.id}/unfollow'/>" class="follow-button-container">
                                <div>
                                    <spring:message code="tags.unfollow"/>
                                </div>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="tag-snippets-grid">
                <c:forEach var="snippet" items="${snippets}">
                    <c:set var="snippet" value="${snippet}" scope="request"/>
                    <c:import url="/WEB-INF/jsp/snippet/snippetCard.jsp"/>
                </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>