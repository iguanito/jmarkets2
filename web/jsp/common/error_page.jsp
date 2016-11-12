<%@ page isErrorPage="true" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.struts.action.*" %>
<%@ page import="org.apache.struts.Globals" %>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <!--DWLayoutTable-->
    <tr> 
        <td with="12">&nbsp;</td>
        <td width="680" height="100%">
<p>
<h3>The following error occurred in the jMarkets Web Application:</h3>
<br>
<hr>
</p>
<p><font color="red" class="articlecontent">

<%
	String message = "Unknown Error: Please report to the <a href=\"mailto:wmyuan@hss.caltech.edu\" class=\"articlelink\">site administrator</a>."; 
	javax.servlet.jsp.JspException jspException = (javax.servlet.jsp.JspException)(request.getAttribute(Globals.EXCEPTION_KEY));
	if (jspException != null){
		message = ((Exception)jspException).getMessage();
	} else if (exception != null ){
		message = ((Exception)exception).getMessage();
	}
   
   out.println(message);
   if (jspException != null){
   	ByteArrayOutputStream ostr = new ByteArrayOutputStream();
   	jspException.printStackTrace(new PrintStream(ostr));
   	out.println(ostr.toString());
   	}
%>
</font>
<br>
</p>
        </td>
    </tr>
    <tr><td height="20"></td></tr>
</table>





	
