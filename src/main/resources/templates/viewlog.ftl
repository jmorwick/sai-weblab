<#include "header.ftl">
<h1>Log: ${taskname}</h1>

<h2>Queries:</h2>
<ul>
<#list queryids as queryid>
<li><a href="/reports/view-query/${taskid}/${queryid}"> #${queryid}</a></li>
</#list>
</ul>
<#include "footer.ftl">
