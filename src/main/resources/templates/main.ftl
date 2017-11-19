<#include "header.ftl">
<div class="container">
  <legend>Select a Database Interface:</legend>
  <ul>
    <#list dbs as dbname, dbtype>
      <li><a href="/dbs/view/${dbname}">${dbname}</a> - ${dbtype}</li>
    </#list>
  </ul>
</div>
<#include "footer.ftl">
