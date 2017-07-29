<#include "header.ftl">
<h1>View Reports</h1>

<h2>Available Logs:</h2>
<#list logs as logid, taskname>
<div class="row">
    <h3>#${logid} - ${taskname}  <a href="/reports/view/${logid}">(log)</a></h3>
</div>
</#list>

<#include "footer.ftl">