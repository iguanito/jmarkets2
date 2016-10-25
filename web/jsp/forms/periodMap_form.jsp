<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="savePeriodMap">

    <H1><bean:message key="period.config"/></H1>

    <!--<hr size="1" class="strikeline">-->

    <div class="content">

        <table border="0" width="100%">
            <tr><td colspan="2" height="5">&nbsp;</td></tr>
            <tr><td colspan="2"><html:errors/></td></tr>
        </table>

        <table border="0" rules="none" width="70%">
            <tr>
                <td align="center" width="10%"><bean:message key="period.list"/></td>
                <td align="center" width="10%"><bean:message key="period.numSecurities"/></td>
                <td align="center" width="10%"><bean:message key="period.numStates"/></td>
                <td align="center" width="10%"><bean:message key="period.numDivisions"/></td>
                <td align="center" width="10%"><bean:message key="period.periodLength"/></td>
                <td align="center" width="10%"><bean:message key="period.openDelay"/></td>
                <td align="center" width="10%"><bean:message key="period.trigger"/></td>
                <td align="center" width="10%"><bean:message key="period.suggestedPrice"/></td>
                <td align="center" width="10%"><bean:message key="period.closebook"/></td>
            </tr>

            <c:forEach var="period" varStatus="status" items="${periodConfigForm.period}">
                <tr>
                    <td align="center" width="10%"><c:out value="${status.index}"/></td>
                    <td align="center" width="10%"><html:text name="period" property="numSecurities" indexed="true" size="3" maxlength="3"/></td>
                    <td align="center" width="10%"><html:text name="period" property="numStates" indexed="true" size="3" maxlength="3"/></td>
                    <td align="center" width="10%"><html:text name="period" property="numDivisions" indexed="true" size="3" maxlength="3"/></td>
                    <td align="center" width="10%"><html:text name="period" property="periodLength" indexed="true" size="10" maxlength="30"/></td>
                    <td align="center" width="10%"><html:text name="period" property="openDelay" indexed="true" size="10" maxlength="30"/></td>
                    <td align="center" width="10%"><html:checkbox name="period" property="applyTrigger" indexed="true"/></td>
                    <td align="center" width="10%"><html:checkbox name="period" property="showSuggestedClearingPrice" indexed="true"/></td>
                    <td align="center" width="10%"><html:checkbox name="period" property="closebook" indexed="true"/></td>
                </tr>
            </c:forEach>
        </table>

        <br>

        <table border="0" rules="none" width=40%">
            <th>&nbsp;</th>
            <th><bean:message key="period.useGui"/></th>
            <th><bean:message key="period.useEffCashValidation"/></th>
            <c:forEach var="period" varStatus="status" items="${periodConfigForm.period}">
                <tr>
                    <td align="center" width="25%">Period <c:out value="${status.index}"/>:</td>
                    <td align="center" width="37%"><html-el:checkbox property="period[${status.index}].useGui"/></td>                    
                    <td align="center" width="37%"><html-el:checkbox property="period[${status.index}].useEffCashValidation"/></td>
                </tr>
            </c:forEach>
        </table>
        <br>
        
        <table border="0" width="33%">
            <tr>
                <td align="center" width="37%"><b><bean:message key="group.id"/></b></td>
                <td align="center" width="63%"><b><bean:message key="group.name"/></b></td>
            </tr>

            <c:forEach var="groupNames" varStatus="status" items="${periodConfigForm.groupNames}">
                <tr>
                    <td align="center" width="37%"><c:out value="${status.index}"/></td>
                    <td align="center" width="63%"><html:text name="groupNames" property="name" indexed="true" size="20" maxlength="20"/></td>
                </tr>
            </c:forEach>
        </table>

        <br>

        <table border="0" width="40%">
            <tr>
                <td align="center"><b><bean:message key="function.payoff.select"/></b></td>
                <td align="center"><b><bean:message key="function.specifications"/></b></td>
            </tr>

            <c:forEach var="payoffFunctions" varStatus="count" items="${periodConfigForm.payoffFunctions}">
                <tr>
                    <td align="center"><bean:write name="payoffFunctions" property="name"/></td>
                    <td align="center"><html:text name="payoffFunctions" property="numSpecs" indexed="true" size="3" maxlength="3"/></td>
                </tr>
            </c:forEach>
        </table>

        <br>

        <table border="0" width="40%">
            <tr>
                <td align="center"><b><bean:message key="function.bankruptcy.select"/></b></td>
                <td align="center"><b><bean:message key="function.specifications"/></b></td>
            </tr>

            <c:forEach var="bankruptcyFunctions" varStatus="count" items="${periodConfigForm.bankruptcyFunctions}">
                <tr>
                    <td align="center"><bean:write name="bankruptcyFunctions" property="name"/></td>
                    <td align="center"><html:text name="bankruptcyFunctions" property="numSpecs" indexed="true" size="3" maxlength="3"/></td>
                </tr>
            </c:forEach>
        </table>

        <br>

        <table border="0" width="85%">
            <tr>
                <td></td><td></td><td></td><td></td>
                <td align="center">
                    <html:submit value="Next"/>
                </td>
            </tr>
        </table>

    </div>

</html:form>
