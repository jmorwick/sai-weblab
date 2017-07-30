<#include "header.ftl">

<h1>Log: ${taskname}</h1>

<h2>Query: #${queryid}</h2>
<blockquote>${query}</blockquote>

<h2>Retrieved Graphs</h2>
<ol>
<#list graphids as graphid>
    <li>Graph <a href="/dbs/retrieve/${dbname}?id=${graphid}"> #${graphid}</a></li>
</#list>
</ol>

<#include "footer.ftl">
