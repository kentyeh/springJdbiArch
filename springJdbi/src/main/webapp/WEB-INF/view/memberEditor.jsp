<%-- 
    Author     : Kent Yeh
--%>

<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%--Context path--%>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
    <head>
        <title>${member.name}</title>
        <link rel="stylesheet" href="${cp}/wro/all.css"/>
        <script src="${cp}/wro/all.js"></script>
        <script type="text/javascript">
            var idx = 0;
            function addAuthority(){
                var html= '<tr><td>'+
                        '<input type="hidden" name="authorities['+idx+'].account" value="${member.account}"/>'+
                        '<input type="text" name="authorities['+(idx++)+'].authority" width="30" maxlength="50"/>'+
                        '</td><td><button type="button" class="mdl-button mdl-js-button mdl-button--fab mdl-button--colored" onclick="$(this).closest(\'tr\').remove();">'+
                        '<i class="material-icons">&#xE14C;</i></button></td></tr>';
                $("tfoot").append($(html));
            }
        </script>
    </head>
    <body>
        <table border="0" style="width:100%">
            <sec:authorize access="authenticated" var="logined">
                <tr>
                    <td><a href="${cp}/user/myinfo" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent"><fmt:message key="myinfo"/></a></td>
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
        <form action="${cp}/member/update" method="POST"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <table style="margin-left: auto;margin-right: auto">
                <thead>
                    <c:if test="${not empty errorMsg}"><tr><th colspan="2" style="color: red"><fmt:message key='exception'/>:${errorMsg}</th></tr></c:if>
                    <tr><th colspan="2" style="text-align: center;font-size:14pt;" class="mdl-shadow--2dp"><input type="hidden" name="account" value="${member.account}">${member.account}
                        <input type="hidden" name="password" value="********"></th></tr></thead>
                <tbody>
                    <tr><td colspan="2" style="text-align: center"><input class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" type="submit" value="<fmt:message key="updateUserData"><fmt:param value="${member.name}"/></fmt:message>"/></td></tr>
                    <tr><td colspan="2" class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                            <input class="mdl-textfield__input" type="text" id="username" name="name" value="${member.name}" maxlength="16" autofocus required/>
                        <label class="mdl-textfield__label" for="username"><fmt:message key="name"/></label>
                      </td></tr>
                    <tr><td colspan="2">
                            <fmt:message key="enabled"/>:
                      <label class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="enabledY">
                          <input class="mdl-radio__button" type="radio" name="enabled" value="Y" id="enabledY" ${"Y" eq member.enabled?"checked":""}/>
                          <span class="mdl-radio__label"><fmt:message key="true"/></span>
                      </label>
                      <label class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for="enabledN">
                          <input class="mdl-radio__button" type="radio" name="enabled" value="N" id="enabledN" ${"Y" ne member.enabled?"checked":""}/>
                          <span class="mdl-radio__label"><fmt:message key="false"/></span>
                      </label>  
                      </td></tr>
                    <tr><td colspan="2" class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                            <input class="mdl-textfield__input" type="date" width="12" max="10" name="birthday" value="<fmt:formatDate value="${member.birthday}"/>"/>
                        <label class="mdl-textfield__label" for="birthday"><fmt:message key="birthday"/></label>
                      </td></tr>
                </tbody>
                <tfoot>
                    <tr><td><fmt:message key="role"/></td><td style="text-align: center"><button  type="button" class="mdl-button mdl-js-button mdl-button--fab mdl-button--colored" onclick="addAuthority()"/><i class="material-icons">&#xE145;</i></button></td></tr>
                            <c:forEach var="authority" items="${member.authorities}" varStatus="status">
                        <tr><td>
                                <input type="hidden" name="authorities[${status.index}].account" value="${member.account}"/>
                                <input type="text" name="authorities[${status.index}].authority" value="${authority.authority}" width="30" maxlength="50"/>
                            </td><td>
                                <button  type="button" class="mdl-button mdl-js-button mdl-button--fab mdl-button--colored" onclick="$(this).closest('tr').remove();"/><i class="material-icons">&#xE14C;</i></button>
                            </td></tr>
                            <c:set var="idx" value="${status.count}"/>
                        </c:forEach>
                    <script type="text/javascript">idx=${idx};</script>
                </tfoot>
            </table>
        </form>
    </body>
</html>
