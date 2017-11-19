<#include "header.ftl">
<div class="container">
    <h3>DBInterface: ${dbname}</h3>


    <legend>Encoding</legend>
    <form action="/dbs/test/${dbname}" method="post">
        <label>Encoder</label>
        <select name="encodername">
        <#list encoders as encodername>
            <option>${encodername}</option>
        </#list>
        </select>
    </form>
    <pre id="jsonGraph" class="prettyprint"></pre>
    <script text="text/javascript">
        var graph = "${encoding?js_string}";
        refactorGraph(graph);
    </script>
</div>
<#include "footer.ftl">
