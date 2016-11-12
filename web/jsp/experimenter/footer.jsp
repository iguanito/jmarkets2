<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<hr size=1 class="strikeline">
<table border="0" width="100%">
    <tr>
        <td align="center" width="25%" class="articlelink">
            <html:link href="http://www.clef.caltech.edu/exp/market-dc/start.htm"><bean:message key="label.home"/></html:link>
        </td>
        <td align="center" width="25%"  class="articlelink">
            <html:link href="ExpLogin"><bean:message key="label.experimenter.login"/></html:link>
        </td>
        <td align="center" width="25%" class="articlelink">
            <html:link href="editSessionConfig.do"><bean:message key="label.experimenter.sessionConfig"/></html:link>
        </td>
        <td align="center" width="25%"  class="articlelink">
            <html:link href="About"><bean:message key="label.about"/></html:link>
        </td>
    </tr>
  
</table>


