<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="saveExpLogin" enctype="multipart/form-data" onsubmit="return validateExpLoginForm(this);">

<H1><bean:message key="exp.login.title"/></H1>

<hr size="1" class="strikeline">

<div class="content">

<table border="0">
  <tr><td colspan="2" height="5">&nbsp;</td></tr>
  <tr><td colspan="2"><html:errors/></td></tr>
  <tr>
    <td align="right">
      <bean:message key="prompt.email"/>: 
    </td>
    <td><html:text property="email"/></td>
  </tr>

  <tr>
    <td align="right">
      <bean:message key="prompt.password"/>:
    </td>
    <td><html:password property="password"/></td>
  </tr>
</table>
      
<table border="0" width="40%">  
  <tr>
    <td align="right">
      <html:submit value="Submit"/>
    </td>
    <td></td>
  </tr>
</table>

</div>

</html:form>


<html:javascript formName="expLoginForm" dynamicJavascript="false"/>
