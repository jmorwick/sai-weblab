<#include "header.ftl">
<div class="container">
    <h3>View Reports</h3>

    <legend>Available Reports:</legend>
    <ul>
        <#list reportids as id>
        <div class="row">
            <li> <a href="/reports/view/${id}">#${id} </a></li>
        </div>
        </#list>
    </ul>
<div>
<#include "footer.ftl">