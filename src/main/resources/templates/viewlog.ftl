<#include "header.ftl">
<div class="container">
    <h3>Log: ${taskname}</h3>

    <legend>Queries:</legend>
    <ul>
    <#list queryids as queryid>
    <li><a href="/reports/view-query/${taskid}/${queryid}"> #${queryid}</a></li>
    </#list>
    </ul>
</div>
<#include "footer.ftl">
