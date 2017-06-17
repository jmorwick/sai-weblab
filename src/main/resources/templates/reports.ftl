<#include "header.ftl">
<h1>View Reports</h1>

<!--   TODO:   migrate old jsp code below to controller / template


<%@page import="net.sourcedestination.funcles.tuple.Tuple2"%>
<%@page import="net.sourcedestination.sai.weblab.Tasks"%>
<%@page import="net.sourcedestination.sai.weblab.Resources"%>
<%@page import="net.sourcedestination.sai.reporting.Log"%>
<%@page import="net.sourcedestination.sai.task.Task"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.function.Supplier"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jspf" />
<h1>View Report:</h1>
<select>
    <% // TODO: include all task details
    for(Map.Entry<Task,Log> e : Tasks.FINISHED_TASKS.entrySet()) {
    Task task = e.getKey();
    Log log = e.getValue();
    %>
    <option><%= log.getTaskName() + " - " + log.getStartTime() %></option>
</select>
<%
}
%>
<jsp:include page="footer.jspf" />

-->

<#include "footer.ftl">
