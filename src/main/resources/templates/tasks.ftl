<#include "header.ftl">
<script>
    var currenttaskform = null;
    function switchtask(form) {
        if(currenttaskform != null)
            currenttaskform.style.display="none";
        else {
            forms = document.getElementsByClassName('task-form');
            for (i = 0; i < forms.length; i++)
                forms[i].style.display = "none";
        }

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
            <li>#${taskid} - ${taskname} (${startTimes[taskid]} - ${endTimes[taskid]}) | Task Took ${taskTimes[taskid]}ms to finish.
            <#if tasklogs?keys?seq_contains(taskid)><ul>
                <li><a href="/reports/log/${tasklogs[taskid]}">(log: ${tasklogs[taskid]})</a></li>
            </ul></#if>
            </li>
        </div>
        </#list>
    </ul>
    
    <legend>Launch Task:</legend>
    <select onchange="switchtask(this)">
    <#list tasks as view, name>
        <option <#if name?is_first>SELECTED</#if>>${name}</option>
    </#list>
    </select>
    <#list tasks as view, name>
        <div class="task-form" id="${name}-taskform" style="display: <#if name?is_first>block<#else>none</#if>">
            <#include "tasks/${view}.ftl">
        </div>
    </#list>

</div>
<#include "footer.ftl">
