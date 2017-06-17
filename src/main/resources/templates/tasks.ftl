<!--   TODO:   migrate old jsp code below to controller / template



<%@page import="net.sourcedestination.funcles.tuple.Tuple2"%>
<%@page import="net.sourcedestination.sai.weblab.Tasks"%>
<%@page import="net.sourcedestination.sai.weblab.Resources"%>
<%@page import="net.sourcedestination.sai.task.Task"%>
<%@page import="java.util.function.Supplier"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jspf" />
<h1>Initiate a Task:</h1>
<form action="./Tasks" method="POST">
    <select name="initiator">
        <%
        for(Tuple2<String, Supplier<Task>> t : Tasks.getInitiators()) {
        %>  <option><%= t._1() %></option>
        <%
        }

        %>
    </select>

    <input type="submit" name="action" value="initiate">
</form>


<h1>Currently Running Tasks:</h1>

<% // TODO: include all task details
for(Task t : Tasks.TASKS) {
if(!t.running()) continue;
%>  <option><%=t.getTaskName()+"("+t.getProgressUnits()+")("+t.getPercentageDone()+"%)"
    + (Tasks.CRASHED_TASKS.containsKey(t) ? Tasks.CRASHED_TASKS.get(t).getMessage() : "")
    %></option>
<%
}
%>

<h1>Inactive Tasks:</h1>

<% // TODO: include all task details
for(Task t : Tasks.TASKS) {
if(t.running()) continue;
%>  <option><%=t.getTaskName()+"("+t.getProgressUnits()+")("+t.getPercentageDone()+"%)"
    + (Tasks.CRASHED_TASKS.containsKey(t) ? Tasks.CRASHED_TASKS.get(t).getMessage() : "")
    %></option>
<%
}
%>
<jsp:include page="footer.jspf" />

-->