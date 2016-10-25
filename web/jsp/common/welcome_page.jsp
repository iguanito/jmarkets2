<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<H1><bean:message key="label.menu"/></H1>

<hr size="1" class="strikeline">

<div class="content">

    <ul class="articlelink">
    <li>
        <html:link href="ClientReg"><bean:message key="label.client.signup"/></html:link>
    </li>

    <li>
        <html:link href="editJoinExp.do"><bean:message key="label.client.participate.long"/></html:link>
    </li>
    
    </ul>
</div>















