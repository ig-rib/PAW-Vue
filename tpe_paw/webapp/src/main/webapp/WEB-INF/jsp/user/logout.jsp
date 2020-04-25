<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %><html>

<head>
    <head>
        <title><spring:message code="logout.title"/></title>
        <link href="<c:url value='/resources/css/errorPages.css'/>" rel="stylesheet"/>
        <link href="<c:url value='/resources/css/general.css'/>" rel="stylesheet"/>
        <link href="<c:url value='/resources/css/registration.css'/>" rel="stylesheet"/>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<body>
<div class="flex-column flex-center TODO-delete">

    <div class="flex-row flex-center register-text-container register-text register-welcome-text fw-300">
        <i class="material-icons app-icon register-app-icon-margin">code</i>
        <spring:message code="logout.goodbye"/>
    </div>

    <div class="flex-center register-form register-border register-shadow">
        <div class="flex-column register-form-data">
            <a class="flex-center register-border register-field-size register-button register-buttons-margin fw-500 register-text" href="<c:url value='/'/>">
                <spring:message code="logout.homeRedirect"/>
            </a>

            <a class="flex-center register-border register-field-size register-button register-buttons-margin fw-500 register-text" href="<c:url value='/login'/>">
                <spring:message code="logout.loginRedirect"/>
            </a>

            <a class="flex-center register-border register-field-size register-button register-buttons-margin fw-500 register-text" href="<c:url value='/signup'/>">
                <spring:message code="logout.signupRedirect"/>
            </a>
        </div>
    </div>

</div>

</body>
</html>