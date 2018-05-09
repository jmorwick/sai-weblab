<#include "header.ftl">
<div class="container">
    <h3>DBInterface: ${dbname}</h3>


    <form action="/dbs/retrieve/${dbname}" method="get">
        <div class="form-group">
            <label>Encoding</label>
            <select class="form-control" name="format" onchange="form.submit()">
            <#list encoders as encodername>
                <option <#if encodername == defaultencoder>SELECTED</#if>>${encodername}</option>
            </#list>
            </select>
            <input class="form-control" type="hidden" name="id" value="${id}"/>
        </div>
    </form>

    <pre id="jsonGraph" class="prettyprint"></pre>
    <script text="text/javascript">
        var graph = "${encoding?js_string}";
        refactorGraph(graph);
    </script>
</div>
<#include "footer.ftl">
