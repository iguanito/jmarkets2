<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="savePeriodConfig">

    <H1>Period <c:out value="${periodNum}"/> Configuration</H1>

    <hr size="1" class="strikeline">

    <div class="content">

        <table border="0" width="100%">
            <tr><td colspan="2"><html:errors/></td></tr>
        </table>

        <table width="80%">
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td width="20%" align="right">
                    <html:submit property="method">
                        <bean:message key="period.button.done"/>
                    </html:submit>
                </td>
            </tr>
            <tr>
            <td align="left">
                <logic:equal name="periodConfigForm" property="firstPeriod" value="true">
                    <html:submit property="method" disabled="true">
                        <bean:message key="period.button.prev"/>
                    </html:submit>
                </logic:equal>
                <logic:equal name="periodConfigForm" property="firstPeriod" value="false">
                    <html:submit property="method">
                        <bean:message key="period.button.prev"/>
                    </html:submit>
                </logic:equal>
            </td>

            <td align="left">
                <logic:equal name="periodConfigForm" property="lastPeriod" value="true">
                    <html:submit property="method" disabled="true">
                        <bean:message key="period.button.next"/>
                    </html:submit>
                </logic:equal>
                <logic:equal name="periodConfigForm" property="lastPeriod" value="false">
                    <html:submit property="method">
                        <bean:message key="period.button.next"/>
                    </html:submit>
                </logic:equal>
            </td>

            <td align="left">
                <html:submit property="method">
                    <bean:message key="period.button.save"/>
                </html:submit>
            </td>

            <td width="50%"></td>

            <td align="center">
                <table>
                    <tr>
                        <td>
                            <html:submit property="method">
                                <bean:message key="period.button.copy"/>
                            </html:submit>
                        </td>

                        <td>
                            <html-el:select property="copyFrom" size="1">
                                <c:forEach var="per" varStatus="status" items="${periodConfigForm.period}">
                                    <html-el:option value="${per.id}"><bean:message key="period.copyPrefix"/> <c:out value="${per.id}"/></html-el:option>
                                </c:forEach>
                            </html-el:select>
                        </td>
                    </tr>
                </table>
            </td>

            </tr>
        </table>

        <br>
        
        <b><bean:message key="marketEngine.title"/></b>
        <html-el:select property="period[${periodNum}].marketEngine">
            <html-el:option value="continuous"><bean:message key="marketEngine.continuous"/></html-el:option>
            <html-el:option value="call"><bean:message key="marketEngine.call"/></html-el:option>
        </html-el:select>
        
        <br><br>


        <table border="1" rules="none" cellpadding="10%">
            <b><bean:message key="subject.title"/></b>
            <tr>
                <td align="center" width="15%"><bean:message key="subject.id"/></td>
                <td align="center" width="15%"><bean:message key="subject.group"/></td>
                <td align="center" width="70%"><bean:message key="subject.announcement"/></td>
            </tr>

            <c:forEach var="subject" varStatus="status" items="${periodConfigForm.subject}">

                <tr>
                    <td align="center" width="15%"><c:out value="${status.index}"/></td>
                    <td align="center" width="15%"><html-el:select name="subject" property="group" indexed="true">
                            <c:forEach var="group" varStatus="status" items="${periodConfigForm.group}">

                                <html-el:option value="${group.id}"><c:out value="${group.name}"/></html-el:option>

                            </c:forEach>
                    </html-el:select></td>
                    <td><html:textarea name="subject" property="announcement" cols="100" rows="15" indexed="true"/></td>
                </tr>
            </c:forEach>
        </table>
        <br/>
        <table border="1" rules="none" cellpadding="10%">
            <b><bean:message key="group.title"/></b>
            <tr>
                <td align="center" width="15%"><bean:message key="group.group"/></td>
                <td align="center" width="15%"><bean:message key="group.cash"/></td>
                <td align="center" width="15%"><bean:message key="group.addCash"/></td>
                <td align="center" width="15%"><bean:message key="group.payoffFunction"/></td>
                <td align="center" width="15%"><bean:message key="group.bankruptcyFunction"/></td>
                <td align="center" width="15%"><bean:message key="group.bankruptcyCutoff"/></td>
            </tr>

            <c:forEach var="group" varStatus="status" items="${periodConfigForm.group}">

            <tr>
            <td align="center" width="15%"><bean:write name="group" property="name"/></td>
            <td align="center" width="15%"><html:text name="group" property="cashInitial" indexed="true" size="10" maxlength="10"/></td>
            <td align="center" width="15%"><html:checkbox name="group" property="addCash" indexed="true"/></td>
            <td align="center" width="15%"><html-el:select name="group" property="payoffFunctionName" indexed="true">
                <c:forEach var="function" varStatus="count" items="${periodConfigForm.payoffFunctionSpecs}">
                    <html-el:option value="${function.specName}"><c:out value="${function.specName}"/></html-el:option>
                </c:forEach>
            </html-el:select></td>
            <td align="center" width="15%"><html-el:select name="group" property="bankruptcyFunctionName" indexed="true">
                <c:forEach var="function" varStatus="count" items="${periodConfigForm.bankruptcyFunctionSpecs}">
                    <html-el:option value="${function.specName}"><c:out value="${function.specName}"/></html-el:option>
                </c:forEach>
            </html-el:select></td>
            <td align="center" width="15%"><html:text name="group" property="bankruptcyCutoff" indexed="true" size="5" maxlength="5"/></td>
            </tr>

            </c:forEach>

        </table>

        <br>
        <b><bean:message key="period.functionParams"/></b>
        <c:forEach var="payoffFunctionSpecs" varStatus="count" items="${periodConfigForm.payoffFunctionSpecs}">

        <table border="0" rules="none" width="33%">
            <tr>
                <td><i><bean:write name="payoffFunctionSpecs" property="specName"/></i></td>
                <td></td>
            </tr> 
        </table>

        <table border="1" rules="none">
            <tr>
                <td></td>

                <c:forEach begin="0" end="${periodConfigForm.numStates - 1}" varStatus="status">
                    <td><c:out value="State ${status.index}"/></td>
                </c:forEach>
            </tr>

            <c:forEach var="fieldNames" varStatus="status" items="${payoffFunctionSpecs.fieldNames}">
            <tr>
                <td><c:out value="${payoffFunctionSpecs.fieldNames[status.index]}"/></td>

                <c:forEach begin="0" end="${periodConfigForm.numStates - 1}" varStatus="stateStatus">
                    <td><html-el:text name="periodConfigForm" property="payoffFunctionSpecs[${count.index}].fieldValues[${status.index}].stateValues[${stateStatus.index}]"/></td>
                </c:forEach>
            </tr>
            </c:forEach>
        </table>
    
        <br>

        </c:forEach>

        <c:forEach var="bankruptcyFunctionSpecs" varStatus="count" items="${periodConfigForm.bankruptcyFunctionSpecs}">

        <table border="0" rules="none" width="33%">
            <tr>
                <td><i><bean:write name="bankruptcyFunctionSpecs" property="specName"/></i></td>
                <td></td>
            </tr> 
        </table>

        <table border="1" rules="none">
            <tr>
                <td></td>

                <c:forEach begin="0" end="${periodConfigForm.numStates - 1}" varStatus="status">
                    <td><c:out value="State ${status.index}"/></td>
                </c:forEach>
            </tr>

            <c:forEach var="securities" varStatus="secStatus" items="${bankruptcyFunctionSpecs.fieldNames}">
            <tr>
                <td><c:out value="${bankruptcyFunctionSpecs.fieldNames[secStatus.index]}"/></td>

                <c:forEach begin="0" end="${periodConfigForm.numStates - 1}" varStatus="stateStatus">
                    <td><html-el:text name="periodConfigForm" property="bankruptcyFunctionSpecs[${count.index}].fieldValues[${secStatus.index}].stateValues[${stateStatus.index}]"/></td>
                </c:forEach>
            </tr>
            </c:forEach>
        </table>
    
        <br>

        </c:forEach>

        <br>

        <c:forEach var="security" varStatus="status" items="${periodConfigForm.security}">

        <table border="1" rules="none" width="80%">
            <b><bean:message key="security.title"/> <c:out value="${status.index}"/></b>

            <tr valign="top"><td width="40%">

                <table border="0" rules="none" width="100%">
                <tr>
                <td align="center" width="40%"><bean:message key="security.name"/></td>
                <td align="center" width="60%"><html:text name="security" property="title" indexed="true" size="20"/></td>   
            </tr>

                    <tr>
                    <td align="center" width="40%"><bean:message key="security.minPrice"/></td>
                    <td align="center" width="60%"><html:text name="security" property="minPrice" indexed="true" size="5" maxlength="5"/></td>
                    </tr>

                    <tr>
                    <td align="center" width="40%"><bean:message key="security.maxPrice"/></td>
                    <td align="center" width="60%"><html:text name="security" property="maxPrice" indexed="true" size="5" maxlength="5"/></td>
                    </tr>

                    <tr>
                    <td align="center" width="40%"><bean:message key="security.time"/></td>
                    <td align="center" width="60%"><html:text name="security" property="timeOpen" indexed="true" size="10" maxlength="30"/></td>
                    </tr>
                </table>

            </td><td width="60%">
    
                <table border="0" rules="none" width="100%">
                    <tr>
                    <td align="center" width="30%"></td>
                    <td align="center" width="10%"><bean:message key="security.buy"/></td>
                    <td align="center" width="10%"><bean:message key="security.sell"/></td>
                    <td align="center" width="10%"><bean:message key="security.addSurplus"/></td>
                    <td align="center" width="10%"><bean:message key="security.addDividend"/></td>
                    <td align="center" width="20%"><bean:message key="security.initials"/></td>
                    <td align="center" width="20%"><bean:message key="security.ssconstraint"/></td>
                    </tr>

                    <c:forEach var="group" varStatus="groupnum" items="${periodConfigForm.group}">
                        <tr>
                        <td align="center" width="30%"><bean:write name="group" property="name"/></td>
                        <td align="center" width="10%"><html-el:checkbox property="security[${status.index}].buyPriveleges[${groupnum.index}]"/></td>
                        <td align="center" width="10%"><html-el:checkbox property="security[${status.index}].sellPriveleges[${groupnum.index}]"/></td>
                        <td align="center" width="10%"><html-el:checkbox property="security[${status.index}].addSurplus[${groupnum.index}]"/></td>
                        <td align="center" witth="10%"><html-el:checkbox property="security[${status.index}].addDividend[${groupnum.index}]"/></td>
                        <td align="center" width="20%"><html-el:text property="security[${status.index}].initials[${groupnum.index}]" size="3" maxlength="3"/></td>
                        <td align="center" width="20%"><html-el:text property="security[${status.index}].constraints[${groupnum.index}]" size="3" maxlength="3"/></td>
                        </tr>
                    </c:forEach>
                </table>

            </td></tr>
        </table>

        <br>

        </c:forEach>

        <table>
            <tr>
            <td align="center">
                <logic:equal name="periodConfigForm" property="firstPeriod" value="true">
                    <html:submit property="method" disabled="true">
                        <bean:message key="period.button.prev"/>
                    </html:submit>
                </logic:equal>
                <logic:equal name="periodConfigForm" property="firstPeriod" value="false">
                    <html:submit property="method">
                        <bean:message key="period.button.prev"/>
                    </html:submit>
                </logic:equal>
            </td>

            <td align="center">
                <logic:equal name="periodConfigForm" property="lastPeriod" value="true">
                    <html:submit property="method" disabled="true">
                        <bean:message key="period.button.next"/>
                    </html:submit>
                </logic:equal>
                <logic:equal name="periodConfigForm" property="lastPeriod" value="false">
                    <html:submit property="method">
                        <bean:message key="period.button.next"/>
                    </html:submit>
                </logic:equal>
            </td>

            <td align="center">
                <html:submit property="method">
                    <bean:message key="period.button.save"/>
                </html:submit>
            </td>
            </tr>
        </table>

    </div>

</html:form>
