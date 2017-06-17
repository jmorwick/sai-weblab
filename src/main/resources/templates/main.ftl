<#include "header.ftl">
<h1>Query a database</h1>
<select name="db">
  <#list dbs as dbname, dbtype>
    <option value="${dbname}">${dbname} - ${dbtype}</option>
  </#list>
    <!-- TODO: include all DB's as option tags -->
</select>


<!-- TODO: loop below for each database:
<div>
    <h2><%= name %></h2>
    <b>size: </b> <%= db.getDatabaseSize() %>
</div>
-->

<#include "footer.ftl">
