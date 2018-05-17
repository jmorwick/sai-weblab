<#include "header.ftl">
<div class="container">
    <h3>Report: ${reportid}</h3>

    <ul>
    <#list .data_model?keys as key>
        <#if .data_model[key]?is_string>
            <li>${key} - ${.data_model[key]}</li>
        </#if>
    </#list>
    </ul>
</div>
<#include "footer.ftl">
