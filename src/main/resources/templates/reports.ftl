<#include "header.ftl">
<div class="container">
    <h3>View Reports</h3>

    <legend>Available Logs:</legend>
    <ul>
        <#list logs as logid, taskname>
        <div class="row">
            <li>#${logid} - ${taskname}  <a href="/reports/view/${logid}">(log)</a></li>
        </div>
        </#list>
    </ul>
<div>
<#include "footer.ftl">