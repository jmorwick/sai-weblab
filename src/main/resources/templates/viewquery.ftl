<#include "header.ftl">
<div class="container">
    <h3>Log: ${taskname}</h3>

    <h3>Query: #${queryid}</h3>
    <blockquote>${query}</blockquote>

    <legend>Retrieved Graphs</legend>
    <div class="container">
        <ul class="list-inline">
            <#list graphids as graphid>
                <li class="list-inline-item"><a href="/dbs/retrieve/${dbname}?id=${graphid}">Graph #${graphid}</a></li>
            </#list>
        </ul>
    </div>
</div>
<#include "footer.ftl">
