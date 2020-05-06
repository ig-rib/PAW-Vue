<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <link href="<c:url value='/resources/css/icons.css'/>" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/general.css'/>" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap"
          rel="stylesheet">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script src="<c:url value='/resources/js/form.js'/>"></script>
</head>

<body>
<div class="flex-column">
    <c:set var="tagtId" value="${requestScope.tagId}"/>
    <form:form class="form-container flex-center" action="${tagId}/follow" method="post" modelAttribute="followForm">
        <form:checkbox class="hidden" id="follow-button" path="follows" value="true" onclick="updateForm(this)"/>
        <label for="follow-button">
            <c:choose>
                <c:when test="${followForm.follows}">
                    <div class="tag-snippets-button border-radius flex-center no-text-decoration">
                        <spring:message code="tags.unfollow"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tag-snippets-button border-radius flex-center no-text-decoration">
                        <spring:message code="tags.follow"/>
                    </div>
                </c:otherwise>
            </c:choose>
        </label>
    </form:form>
</div>

</body>
</html>