<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<H1><bean:message key="label.experimenter.menu"/></H1>

<hr size="1" class="strikeline">

<div class="content">

    <ul class="articlelink">
    <li>
        <html:link href="SessionConfig"><bean:message key="label.experimenter.sessionConfig"/></html:link>
    </li>

    <li>
        <html:link href="MonitorSession"><bean:message key="label.experimenter.monitor"/></html:link>
    </li>
        
    <li>
        <html:link href="editOutputWriter.do"><bean:message key="label.experimenter.output"/></html:link>
    </li>
    
    </ul>
</div>















