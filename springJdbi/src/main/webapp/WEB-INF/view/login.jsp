<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%--Context path--%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <title>Login</title>
        <link rel="stylesheet" href="${cp}/wro/all.css"/>
        <script src="${cp}/wro/all.js"></script>
        <script type="text/javascript">
            window.onload=function(){
                document.getElementById("userid").focus();
            }
            function doSubmit(form){
                var comp = form.userid;
                if(!comp.value){
                    comp.focus();
                    alert("Please input your account.");
                    return false;
                }
                comp = form.password;;
                if(!comp.value){
                    comp.focus();
                    alert("Please input your password.");
                    return false;
                }
                return true;
            }
        </script>
    </head>
    <body><a href="${pageContext.request.contextPath}" style="position: absolute;left: 2px;top: 2px" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent">Index</a><center>
        <div align="center" style="color:red;font-weight:bold;text-align: center" id="msgArea">
            <c:if test="${not empty param.cause or not empty param.authfailed or not empty requestScope.errorMessage}">
                <c:if test="${'expired' eq param.cause}">Session expired,Please login again</c:if>
                <c:if test="${'sessionExceed' eq param.cause}">Session expired,Please try later.</c:if>
                <c:if test="${not empty param.authfailed}"><fmt:message key="exception"/>: ${SPRING_SECURITY_LAST_EXCEPTION.message}</c:if>
                <c:if test="${not empty requestScope.errorMessage}">${requestScope.errorMessage}</c:if>
            </c:if>                                        
        </div>
        <form action="<c:url value='j_spring_security_check'/>" method="post" onsubmit="return doSubmit(this)">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" style="display:block">
                <input class="mdl-textfield__input" type="text" id="userid" name="j_username" value="${sessionScope.loginId}" autofocus required/>
                <label class="mdl-textfield__label" for="userid"><fmt:message key="account"/></label>
            </div>
            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" style="display:block">
                <input class="mdl-textfield__input" type="password" id="password" name="j_password" required/>
                <label class="mdl-textfield__label" for="userid"><fmt:message key="password"/></label>
            </div>
            <div style="text-align: center;display: block;width:200px">
                <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="rememberMe">
                    <input type="checkbox" id="rememberMe" name="remember-me" class="mdl-checkbox__input" value="true">
                    <span class="mdl-checkbox__label"><fmt:message key="rememberMe.label"/></span>
                </label>
            </div>
            <div style="text-align: center;display: block">
                <input type="submit" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent"/>
                <button ></button>
                <span style="width:300px">&nbsp;</span>
                <input type="reset" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent"/>
            </div>
        </form><br/>
        <table align="center" class="mdl-data-table mdl-js-data-table mdl-shadow--2dp">
            <tr><th><fmt:message key="account"/></th><th><fmt:message key="password"/></th></tr>
          <c:forEach var="member" items="${members}">
            <tr><td>${member.account}</td><td>${member.password}</td></tr>
          </c:forEach>
        </table>
    </center></body>
</html>
