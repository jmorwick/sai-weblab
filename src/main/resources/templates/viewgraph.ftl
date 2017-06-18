<#include "header.ftl">
<h1>DBInterface: ${dbname}</h1>


<h2>Encoding</h2>
<form action="/dbs/test/${dbname}" method="post">
    <label>Decoder</label>
    <select name="decodername">
    <#list decoders as decodername>
        <option>${decodername}</option>
    </#list>
</form>

<h2></h2>


<#include "footer.ftl">
