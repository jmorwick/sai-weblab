<#include "header.ftl">
<h1>Manage Tasks</h1>

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

-->


<h2>Currently Running Tasks:</h2>
<#list activetasks as taskid, taskname>
<div class="row">
    <h3>#${taskid} - ${taskname}</h3>
    <label>percentage complete: </label> <span class="labeled-value">${percentagecomplete[taskid]}</span>
</div>
</#list>

<h2>Inactive Tasks:</h2>
<#list inactivetasks as taskid, taskname>
<div class="row">
    <h3>#${taskid} - ${taskname}</h3>
</div>
</#list>


<#include "footer.ftl">
