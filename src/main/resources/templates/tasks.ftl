<#include "header.ftl">
<div class="container">
    <h2>Manage Tasks</h2>

    <legend>Currently Running Tasks:</legend>
    <ul>
        <#list activetasks as taskid, taskname>
        <div class="row">
            <li>#${taskid} - ${taskname} (${startTimes[taskid]}) | <label>progress: </label><span class="labeled-value">${progress[taskid]}</span> | <label>percentage complete: </label> <span class="labeled-value">${percentagecomplete[taskid]}</span></li>
        </div>
        </#list>
    </ul>

    <legend>Launch Task:</legend>

    <#include "tasks/populate.ftl">
    <#include "tasks/retrieval.ftl">
    <#include "tasks/process-log.ftl">

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
