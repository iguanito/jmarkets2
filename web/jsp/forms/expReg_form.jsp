<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="saveExpReg" enctype="multipart/form-data" onsubmit="return validateExpRegForm(this);">

<H1><bean:message key="exp.reg.title"/></H1>

<hr size="1" class="strikeline">

<div class="content">

<table border="0">
  <tr><td colspan="2" height="5">&nbsp;</td></tr>
  <tr><td colspan="2"><html:errors/></td></tr>
</table>

<table border="1">
  <tr>
    <td align="right" width='25%'>
      <bean:message key="exp.reg.email"/>: 
    </td>
    <td><html:text property="email"/></td>
    <td></td>
  </tr>

  <tr>
    <td align="right">
      <bean:message key="exp.reg.fname"/>:
    </td>
    <td><html:text property="fname"/></td>
      <td></td>
  </tr>

  <tr>
    <td align="right">
      <bean:message key="exp.reg.lname"/>:
    </td>
    <td><html:text property="lname"/></td>
      <td></td>
  </tr>

  <tr>
    <td align="right">
      <bean:message key="exp.reg.phone"/>:
    </td>
    <td><html:text property="phone"/></td>
      <td></td>
  </tr>

  <tr>
    <td align="right">
      <bean:message key="exp.reg.school"/>:
    </td>
    <td><html:text property="school"/></td>
      <td></td>
  </tr>

  <tr>
    <td align="right">
      <bean:message key="exp.reg.password"/>:
    </td>
    <td><html:password property="password"/></td>
       <td></td>
  </tr>

  <tr><td colspan='3' height='10'/></tr>
  <tr>
 
    <td align="center" colspan='2'>
      <html:reset value="Reset"/>
      <html:cancel value="Cancel"/>
      <html:submit value="Submit"/>
    </td>
    <td></td>
  </tr>
</table>

</div>

</html:form>


<html:javascript formName="expRegForm"/>
