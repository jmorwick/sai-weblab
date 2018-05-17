<#include "header.ftl">
<div class="container">


    <form action="/dbs" method="get">
        <div class="form-group">
            <label>Database</label>
            <select class="form-control" name="selecteddb" onchange="form.submit()">
                <option>none</option>
            <#list dbs as dbname, dbtype>
                <option <#if dbname == selecteddb>SELECTED</#if> value="${dbname}">${dbname}: ${dbtype}</option>
            </#list>
            </select>
        </div>
    </form>

    <#if selecteddb != "none">

    <div class="dbstats">
        <label>Size</label> <div>${dbsize}</div>
    </div>

    <form action="/dbs/retrieve/${selecteddb}" method="get">
        <legend>Retrieve a Graph</legend>
        <div class="form-group">

            <label>Encoding</label>
            <select class="form-control" name="format">
            <#list encoders as encodername>
                <option <#if encodername == defaultencoder>SELECTED</#if>>${encodername}</option>
            </#list>
            </select>
            <label>Graph ID</label>
            <input class="form-control" type="text" name="id"/>
            <button type="submit" class="btn btn-primary">View</button>
        </div>
    </form>


    <form action="/dbs/create/${selecteddb}" method="post">
        <legend>Add a Graph</legend>
        <div class="form-group">
            <label>Decoder</label>
            <select class="form-control" name="format">
            <#list decoders as decodername>
                <option <#if decodername == defaultdecoder>SELECTED</#if>>${decodername}</option>
            </#list>
            </select>
            <label>Graph Data</label>
            <textarea class="form-control" name="encoding"></textarea>
            <button type="submit" class="btn btn-primary">Add Graph</button>
        </div>
    </form>


    <form action="/dbs/clear/${selecteddb}" method="post">
        <legend>Clear the Database</legend>
        <div class="form-group">
            <button type="submit" class="btn btn-primary">Clear Database</button>
        </div>
    </form>

</div>

    </#if>
<#include "footer.ftl">
