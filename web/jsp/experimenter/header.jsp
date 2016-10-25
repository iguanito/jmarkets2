<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<table border="2" width="100%">
    <tr align="center" class="articletitle">
      <td colspan=4 align="center" width="100%">
            <html:link href="Welcome"><bean:message key="label.title"/></html:link>
      </td>
    </tr>
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
            <html:link href="mailto:jmarkets@ssel.caltech.edu"><bean:message key="label.contactus"/></html:link>
        </td>
    </tr>
   
</table>


