<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html:form action="saveOutputWriter" enctype="multipart/form-data" >

<H1><bean:message key="output.title"/></H1>

<hr size="1" class="strikeline">

<div class="content">

<table border="0" width="100%">
  <tr><td colspan="2" height="5">&nbsp;</td></tr>
  <tr><td colspan="2"><html:errors/></td></tr>
  <tr>
    <td align="right" width="25%">
      <bean:message key="output.session"/>: 
    </td>
    <td>
      <html:select property="sessionId">
        <html:options collection="sessions" property="value" labelProperty="label"/>
      </html:select>
    </td>
  </tr>
</table>

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
