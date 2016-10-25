<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="saveSessionConfig" enctype="multipart/form-data" onsubmit="return validateSessionConfigForm(this);">

<H1><bean:message key="session.config"/></H1>

<hr size="1" class="strikeline">

<div class="content">

<table border="0" width="100%">
  <tr><td colspan="2" height="5">&nbsp;</td></tr>
  <tr><td colspan="2"><html:errors/></td></tr>
  <tr>
    <td align="right" width="30%">
      <bean:message key="session.name"/>: 
    </td>
    <td><html:text property="name" size="20" maxlength="20"/></td>
  </tr>

  <tr>
    <td align="right" width="30%">
      <bean:message key="session.numPeriods"/>: 
    </td>
    <td><html:text property="numPeriods" size="3" maxlength="3"/></td>
  </tr>

  <tr>
    <td align="right" width="30%">
      <bean:message key="session.numSubjects"/>:
    </td>
    <td><html:text property="numSubjects" size="3" maxlength="3"/></td>
  </tr>

  <tr>
    <td align="right" width="30%">
      <bean:message key="session.numGroups"/>:
    </td>
    <td><html:text property="numGroups" size="3" maxlength="3"/></td>
  </tr>
  <tr>
    <td align="right" width="30%">
      <bean:message key="session.showPastOrders"/>:
    </td>
    <td><html:checkbox property="showPastOrders"/></td>
  </tr>
  
  <tr>
    <td align="right" width="30%">
      <bean:message key="session.showPastTransactions"/>:
    </td>
    <td><html:checkbox property="showPastTransactions"/></td>
  </tr>
  <tr>
    <td align="right" width="30%">
      <bean:message key="session.timeoutLength"/>:
    </td>
    <td><html:text property="timeoutLength" size="3" maxlength="3"/></td>
  </tr>
  
  <tr>
    <td align="right" width="30%">
      <bean:message key="session.manualAdvance"/>:
    </td>
       <td><html:checkbox property="manualAdvance"/></td>
  </tr>
  
 </table>
 <br>

<table border="0" width="100%">
  <tr>
    <td align="right" width="30%">
      <bean:message key="session.loadFromFile"/>
    </td>
    <td><html:checkbox property="loadFromFile"/></td>
  </tr>

  <tr>
    <td align="right" width="30%">
      <bean:message key="session.uploadFile"/>
    </td>
    <td><html:file property="sessionFile"/></td>
  </tr>
</table>

<br>    

<table border="0" width="70%">  
  <tr>
    <td align="right">
      <html:submit value="Submit"/>
    </td>
    <td></td>
  </tr>
</table>

</div>

</html:form>


<html:javascript formName="sessionConfigForm"/>
