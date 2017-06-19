<#include "header.ftl">
<h1>DBInterface: ${dbname}</h1>


<h2>Encoding</h2>
<form action="/dbs/test/${dbname}" method="post">
    <label>Encoder</label>
    <select name="encodername">
    <#list encoders as encodername>
        <option>${encodername}</option>
    </#list>
    </select>
</form>

<pre>${encoding}</pre>

<#include "footer.ftl">
