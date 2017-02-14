<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%--Context path--%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <title>Home page</title>
        <link rel="stylesheet" href="${cp}/wro/all.css"/>
        <script src="${cp}/wro/all.js"></script>
        <script>
            function ajaxGetUser(path){
                $.ajax({
                    type : "POST",
                    url : "${cp}/admin/"+(path?path:"users"),
                    dataType : "json",
                    headers: {"${_csrf.headerName}":"${_csrf.token}"},
                    data: {},
                    cache: false,
                    error:function(jqXHR,  statusText){
                        alert("Exception prone when fetch users' data with "+statusText+":["+jqXHR.status+"]:"+jqXHR.statusText+"\n\t"+jqXHR.responseText);
                    },
                    success:function(data){  
                        if(data.total==0){
                            $("#listuser").html("");
                        }else{
                            var html="<table class=\"mdl-data-table mdl-js-data-table mdl-shadow--2dp\"><thead><tr><th class=\"mdl-data-table__cell--non-numeric\"><fmt:message key="account"/></th><th class=\"mdl-data-table__cell--non-numeric\"><fmt:message key="name"/></th><th class=\"mdl-data-table__cell--non-numeric\"><fmt:message key="birthday"/></th></tr></thead><tbody>";
                            for(var i=0;i<data.total;i++){
                                var user = data.users[i];
                                <sec:authorize access="hasRole('ROLE_ADMIN')" var="isAdmin">
                                html = html + "<tr><td class=\"mdl-data-table__cell--non-numeric\"><a class=\"mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent\" href=\"${cp}/member/edit/"+user.account+"\"><i class=\"material-icons\">&#xE254;</i>"+user.account+"</a></td><td class=\"mdl-data-table__cell--non-numeric\">"+user.name+"</td><td class=\"mdl-data-table__cell--non-numeric\">"+user.birthday+"</td></tr>";
                                </sec:authorize>
                                <c:if test="${not isAdmin}">
                                html = html + "<tr><td class=\"mdl-data-table__cell--non-numeric\">"+user.account+"</td><td class=\"mdl-data-table__cell--non-numeric\">"+user.name+"</td><td class=\"mdl-data-table__cell--non-numeric\">"+user.birthday+"</td></tr>";
                                </c:if>
                            }
                            html += "</tbody></table>";
                            $("#listuser").hide().html(html).show("bounce",{},2000);
                        }
                    }
                });
            }
            $(function() {
                $("center") .show("puff");
            });
        </script>
    </head>
    <body>
        <table border="0" style="width:100%;position: fixed;top: 3px">
            <sec:authorize access="authenticated" var="logined">
                <tr>
                    <td><a href="${cp}/changePassword" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent">
                            <i class="material-icons">&#xE90D;</i><fmt:message key="changePassword"/></a></td>
                    <td align="right">
                        <form action="${cp}/j_spring_security_logout" method="post" style="display: inline">
                            <!--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>-->
                            <sec:csrfInput />
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent">
                                <sec:authentication property="principal.username"/>&nbsp;<fmt:message key="logout"/>
                            <i class="material-icons">&#xE879;</i></button>
                        </form>
                    </td>
                </tr>
            </sec:authorize>
            <c:if test="${not logined}">
                <tr>
                    <td align="right"><a href="${cp}/user/myinfo" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent"><fmt:message key="myinfo"/></a></td>
                </tr>
            </c:if>
        </table>

    <center  style="display: none"><c:set var="world"><fmt:message key="world"/></c:set>
        <h1 class="mdl-shadow--3dp"><fmt:message key="hello"/> ${empty member?world:member.name}</h1>
        <c:if test="${not empty member}">
            <table id="myinfo" class="mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                <thead><tr><th colspan="2" class="mdl-data-table__cell--non-numeric"><fmt:message key="myinfo"/></th></tr></thead>
                <tbody>
                    <tr><td class="mdl-data-table__cell--non-numeric"><fmt:message key="account"/>:</td><td class="mdl-data-table__cell--non-numeric">${member.account}</td></tr>
                    <tr><td class="mdl-data-table__cell--non-numeric"><fmt:message key="name"/>:</td><td class="mdl-data-table__cell--non-numeric">${member.name}</td></tr>
                    <tr><td class="mdl-data-table__cell--non-numeric"><fmt:message key="enabled"/>:</td><td class="mdl-data-table__cell--non-numeric"><c:if test="${'Y' eq member.enabled}"><fmt:message key="true"/></c:if>
                        <c:if test="${'Y' ne member.enabled}"><fmt:message key="false"/></c:if></td></tr>
                    <tr><td class="mdl-data-table__cell--non-numeric"><fmt:message key="birthday"/>:</td><td class="mdl-data-table__cell--non-numeric"><fmt:formatDate value="${member.birthday}" pattern="yyyy/MM/dd"/></td></tr>
                    <tr><td class="mdl-data-table__cell--non-numeric"><fmt:message key="role"/>:</td><td class="mdl-data-table__cell--non-numeric">
                        <c:forEach var="authority" items="${member.authorities}">
                            ${authority.authority}&nbsp;
                        </c:forEach>
                    </td></tr>
                </tbody>
            </table>
        </c:if><br/>
        <c:set var="adminAjaxList"><fmt:message key="adminAjaxList"/></c:set>
        <c:set var="adminUserAjaxList"><fmt:message key="adminUserAjaxList"/></c:set>
        <input type="button" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" onclick="ajaxGetUser()" value="<c:out value="${adminAjaxList}" escapeXml="true"/>"/>
        <input type="button" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" onclick="ajaxGetUser('adminOrUsers')" value="<c:out value="${adminUserAjaxList}" escapeXml="true"/>"/>
        <div id="listuser" style="margin-top: 10px"></div>
    </center>
</body>
</html>
