<#include "header.ftl">
<div class="container">
    <h3>Log: ${logid}</h3>

    <pre>
    <#list log as entry>
${entry}
    </#list>
    </pre>
</div>
<#include "footer.ftl">
