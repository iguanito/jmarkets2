<%@ include file="/jsp/common/taglibsheader.jsp" %>

<html>
<head>
<title>jMarket Client <c:out value="${clientName}"/></title>
<script language="JavaScript">

</script>
</head>
<body BGCOLOR="#ffffff" LINK="#000099">
<B><FONT SIZE="-1">Please DO NOT close this window or browse to another page<FONT></B>
<br>
<br>

<blockquote>
<applet code="edu.caltechUcla.sselCassel.projects.jMarkets.client.control.ClientApplet.class"
	codebase="."
	archive="jMarkets-client.jar, jfreechart-0.9.20.jar, jcommon-0.9.5.jar"
	width=10
	height=10>
        <param name="id" value="<c:out value="${clientId}"/>">
        <param name="name" value="<c:out value="${clientName}"/>">
</applet>

<p>
</p>
</body>
</html>

