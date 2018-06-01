<#include "header.ftl">
<div class="container">

    <legend>Available Reports:</legend>
    <ul>
        <#list reportids as id>
        <div class="row">
            <li> <a href="/reports/view/${id}">#${id} </a></li>
        </div>
        </#list>
    </ul>

    <legend>Start / Stop Listening to Log Messages</legend>

    <form method="POST" action="/reports/start-listening">
        <select name="appender" multiple>
        <#list inactiveappenders as name>
            <option>${name}</option>
        </#list>
        </select>
        <input type="submit" name="Start Listening"/>
    </form>

    <form method="POST" action="/reports/stop-listening">
        <select name="appender" multiple>
        <#list activeappenders as name>
            <option>${name}</option>
        </#list>
        </select>
        <input type="submit" name="Stop Listening"/>
    </form>

    <legend>Available Logs</legend>
    <ul>
        <#list logids as id>
            <div class="row">
                <li> <a href="/reports/log/${id}">#${id} </a></li>
            </div>
        </#list>
    </ul>

<div>
<#include "footer.ftl">