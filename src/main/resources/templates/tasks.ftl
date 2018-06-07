<#include "header.ftl">
<script>
    var currenttaskform = null;
    function switchtask(form) {
        if(currenttaskform != null)
            currenttaskform.style.display="none";
        currenttaskform=document.getElementById(form.value+"-taskform");
        if(currenttaskform != null)
            currenttaskform.style.display="block";
    }
</script>

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

    <legend>Inactive Tasks:</legend>
    <ul>
        <#list inactivetasks as taskid, taskname>
        <div class="row">
            <li>#${taskid} - ${taskname} (${startTimes[taskid]} - ${endTimes[taskid]}) | Task Took ${taskTimes[taskid]}ms to finish</li>
        </div>
        </#list>
    </ul>

    <legend>Launch Task:</legend>
    <select onchange="switchtask(this)">
        <option selected>none</option>
    <#list tasks as view, name>
        <option>${name}</option>
    </#list></select>
    <#list tasks as view, name>
        <div class="task-form" id="${name}-taskform" style="display: none">
            <#include "tasks/${view}.ftl">
        </div>
    </#list>

</div>
<#include "footer.ftl">
