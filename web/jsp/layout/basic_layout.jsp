<%@ page import="edu.caltechUcla.sselCassel.projects.jMarkets.shared.JMConstants" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<html:html locale="false">
<head>
<title><tiles:getAsString name="title"/></title>
<LINK rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jMarkets.css">
</head>
<body>
<table width="100%" border="0">
    <tr><td colspan="2">
         <logic:present name="<%=JMConstants.EXPERIMENTER_KEY%>">
                <tiles:insert attribute="exper_header" />
         </logic:present>
      
         <logic:notPresent name="<%=JMConstants.EXPERIMENTER_KEY%>">
                <tiles:insert attribute="header" />
         </logic:notPresent>
        </td>
    </tr>

    <tr>
        <td><tiles:insert attribute='content' ignore="true"/></td>
    </tr>
    <tr><td colspan="2">
         <logic:present name="<%=JMConstants.EXPERIMENTER_KEY%>">
                <tiles:insert attribute="exper_footer" />
         </logic:present>
      
         <logic:notPresent name="<%=JMConstants.EXPERIMENTER_KEY%>">
                <tiles:insert attribute="footer" />
         </logic:notPresent>
        </td>
    </tr>
    <tr><td colspan="2">
        <br>
        <table>
            <tr>
                <td width="10%">&nbsp;</TD>
                <td align="center" class="copyright">
                Copyright &#169; 2004-09 -- SSEL, Caltech
                </td>
                <TD width="15%">&nbsp;</TD>
            </tr>
        </table>
        </td>
    </tr>
</table>
</body>
</html:html>
