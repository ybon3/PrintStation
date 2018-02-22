<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.dtc.printStation.server.print.PrinterManager"%>
<%@page import="com.dtc.printStation.server.print.DtcPrintService.SupportFormat"%>
<%@page import="com.dtc.printStation.shared.vo.Printer"%>
<%
String mimeTypeSelection = "<select name='type'>";
for (SupportFormat format : SupportFormat.values()) {
	mimeTypeSelection += "<option>" + format.mimeType + "</option>";
}
mimeTypeSelection += "</select>";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
body {
	line-height: 30px;
}
</style>
</head>
<body>

<div><h3>遠端列印測試</h3></div>
<form action="<%=request.getContextPath()%>/print" method="post" enctype="multipart/form-data">
<%-- <form action="<%=request.getContextPath()%>/print" method="post"> --%>
name: 
<select name="pn">
<%
List<Printer> list = PrinterManager.getInstance().getPrinter();
for (Printer item : list) {
	if (item.getName().equals("Microsoft XPS Document Writer")) {
		out.print("<option selected>");
	} else {
		out.print("<option>");
	}
	out.print(item.getName() + "</option>");
}
%>
</select><br/>
file1: <textarea name="b64" cols="80" rows="10"></textarea> type1: <%=mimeTypeSelection %><br/>
file2: <textarea name="b64" cols="80" rows="10"></textarea> type2: <%=mimeTypeSelection %><br/>
file3: <textarea name="b64" cols="80" rows="10"></textarea> type3: <%=mimeTypeSelection %><br/>
file: <input type="file" name="f" multiple><br/>
<input type="submit">
</form>

<hr size="1"/>

<div><h3>檔案 Base64 轉換服務</h3></div>
<form action="<%=request.getContextPath()%>/encode" method="post" enctype="multipart/form-data">
file: <input type="file" name="f" /><br/>
<input type="submit">
</form>
</body>
</html>