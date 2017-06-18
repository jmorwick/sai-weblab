<#include "header.ftl">
<h1>Select a Database Interface:</h1>
<ul>
  <#list dbs as dbname, dbtype>
    <li><a href="/dbs/view/${dbname}">${dbname}</a> - ${dbtype}</li>
  </#list>
</ul>

<#include "footer.ftl">
