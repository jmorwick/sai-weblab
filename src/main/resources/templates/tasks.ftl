<#include "header.ftl">
<div class="container">
    <h2>Manage Tasks</h2>

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


    <legend>Currently Running Tasks:</legend>
    <ul>
        <#list activetasks as taskid, taskname>
        <div class="row">
            <li>#${taskid} - ${taskname} (${startTimes[taskid]}) | <label>percentage complete: </label> <span class="labeled-value">${percentagecomplete[taskid]}</span></li>
        </div>
        </#list>
    </ul>

    <legend>Inactive Tasks:</legend>
    <ul>
        <#list inactivetasks as taskid, taskname>
        <div class="row">
            <li>#${taskid} - ${taskname} (${startTimes[taskid]} - ${endTimes[taskid]}) | Task Took ${taskTimes[taskid]}ms to finish</li>
        </div>
        </#list>
    </ul>
    

</div>
<#include "footer.ftl">
