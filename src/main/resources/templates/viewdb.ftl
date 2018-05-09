<#include "header.ftl">
<div class="container">
    <h1>DBInterface: ${dbname}</h1>

    <legend>Statistics</legend>
    <div class="dbstats">
        <#list stats as statname, statvalue>
            <div>
                <label>${statname}</label> <div class="value">${statvalue}</div>
                <#if statProgress[statname]?? >
                   <div class="annotation">(%${statProgress[statname]})</div>
                </#if>
                <#if statComputable[statname]?? >
                    <form action="/dbs/recompute/${dbname}/${statname}" method="POST">
                        <button type="submit" class="btn btn-primary">Recompute</button>
                    </form>
                </#if>
            </div>
        </#list>
    </div>

    <form action="/dbs/retrieve/${dbname}" method="get">
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


    <form action="/dbs/create/${dbname}" method="post">
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


    <form action="/dbs/clear/${dbname}" method="post">
        <legend>Clear the Database</legend>
        <div class="form-group">
            <button type="submit" class="btn btn-primary">Clear Database</button>
        </div>
    </form>

    <form action="/dbs/populate/${dbname}" method="post">
        <legend>Populate the Database</legend>
        <div class="form-group">
            <label>Data Source</label>
            <select class="form-control" name="populatorname">
            <#list populators as populatorname>
                <option>${populatorname}</option>
            </#list>
            </select>
            <button type="submit" class="btn btn-primary">Populate Database</button>
        </div>
    </form>

</div>

<#include "footer.ftl">
