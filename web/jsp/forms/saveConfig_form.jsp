<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="saveSaveConfig" enctype="multipart/form-data">

<H1><bean:message key="save.title"/></H1>

<hr size="1" class="strikeline">

<div class="content">

<table border="0" width="100%">
  <tr><td colspan="2" height="5">&nbsp;</td></tr>
  <tr><td colspan="2"><html:errors/></td></tr>
  <tr>
    <td align="left" width="30%">
      <bean:message key="save.instruction"/>: <html-el:link href="${saveConfigForm.relativeSavePath}"><bean:message key="save.link"/></html-el:link>
    </td>
  </tr>
</table>

<table border="0" width="70%">  
  <tr>
    <td align="right">
      <html:submit value="Return"/>
    </td>
    <td></td>
  </tr>
</table>

</div>

</html:form>

