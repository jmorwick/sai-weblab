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
            <label>Graph ID</label>
            <input class="form-control" type="text" name="id"/>
            <button type="submit" class="btn btn-primary">View</button>
        </div>
    </form>


    <form action="/dbs/create/${dbname}" method="post">
        <legend>Add a Graph</legend>
        <div class="form-group">
            <label>Encoder</label>
            <select class="form-control" name="decodername">
            <#list decoders as decodername>
                <option>${decodername}</option>
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

    <form action="/dbs/retrieval/${dbname}" method="post">
        <legend>Simple Retreival</legend>
        <div class="form-group">
            <input type="hidden" name="retriever" value="SimpleSequentialRetreiver">
            <input type="hidden" name="query" value="">
            <input type="hidden" name="format" value="none">
            <label>Number of Results to Skip</label>
            <input class="form-control" type="input" name="skip"/>
            <label>Max Results</label>
            <input class="form-control" type="input" name="max"/>
            <button type="submit" class="btn btn-primary">Retrieve Graphs</button>
        </div>
    </form>

    <h2>Structural Retreival</h2>
    <form action="/dbs/retrieval/${dbname}" method="post">
        <label>Retriever</label>
        <select class="form-control" name="retriever">
        </select>
        <label>Encoding</label>
        <select class="form-control" name="encoding">
            <option>none</option>
        </select>
        <label>Graph Data</label>
        <textarea class="form-control" name="data"></textarea>
        <label>skip initial n results</label>
        <input class="form-control" type="input" name="skip"/>
        <label>max results</label>
        <input class="form-control" type="input" name="max"/>

        <label>Results Evaluators</label>
        <select multiple name="evaluators">
        </select>
        <button type="submit" class="btn btn-primary">Retrieve Similar Graphs</button>
    </form>

    <h2>Retreival Experiment</h2>
    <form action="/dbs/test/${dbname}" method="post">
        <label>Retriever</label>
        <select class="form-control" name="retriever">
        </select>
        <label>DB Containing Queries</label>
        <input class="form-control" type="text" name="folds"/>
        <button type="submit" class="btn btn-primary">Launch Test</button>
    </form>

    <form action="/dbs/test/${dbname}" method="post">
        <legend>Cross Validation Retrieval Experiment</legend>
        <label>Retriever</label>
        <select class="form-control" name="retriever">
        </select>
        <label>Similarity Metric</label>
        <select class="form-control" name="metric">
            <option>default</option>
        </select>
        <label>Folds</label>
        <input class="form-control" type="text" name="folds"/>
        <button type="submit" class="btn btn-primary">Launch Test</button>
    </form>

</div>

<#include "footer.ftl">
