<#include "header.ftl">
<div class="container">
    <h3>Report: ${reportid}</h3>

    <ul>
    <#list retrievedgraphs as t>
        <li><a href="/dbs/retrieve/${t[1]}/${t[0]}">${t[0]} - ${t[1]}</a></li>
    </#list>
    </ul>
</div>
<#include "footer.ftl">