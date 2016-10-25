<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<H1>Message Board</H1>

<hr size="1" class="strikeline">

<div class="content">

<table width="100%" border="0" cellpadding="10" cellspacing="0">
    <!--DWLayoutTable-->
    <tr> 
        <td>
        <html:messages id="message" message="true">
            <p class="message"><bean:write name="message" filter="false"/></p>
        </html:messages>
        </td>
    </tr>
    <tr>
        <td>
        <p>Please <bean:message key="message.contact"/> if you have any questions. Thank you!</p>
        </td>
    </tr>
    
    <tr><td height="20"></td></tr>
</table>

</div>