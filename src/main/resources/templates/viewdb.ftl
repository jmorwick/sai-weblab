<#include "header.ftl">
<h1>DBInterface: ${dbname}</h1>

<h2>Statistics</h2>
<div class="dbstats">
    <#list stats as statname, statvalue>
        <div class="row">
            <label>${statname}</label> <div class="value">${statvalue}</div>
            <#if statProgress[statname]?? >
               <div class="annotation">(%${statProgress[statname]})</div>
            </#if>
            <form action="/dbs/recompute/${dbname}/${statname}" method="POST">
                <input type="submit" name="submit" value="recompute"/>
            </form>
        </div>
    </#list>
</div>

<h2>Retrieve a Graph</h2>
<form action="/dbs/retrieve/${dbname}" method="get">
    <label>Graph ID</label>
    <input type="text" name="id"/>
    <input type="submit" name="submit" value="view"/>
</form>

<h2>Add a Graph</h2>
<form action="/dbs/create/${dbname}" method="post">
    <label>Encoder</label>
    <select name="decodername">
    <#list decoders as decodername>
        <option>${decodername}</option>
    </#list>
    </select>
    <label>Graph Data</label>
    <textarea name="encoding"></textarea>
    <input type="submit" name="submit" value="add graph"/>
</form>

<h2>Clear the Database</h2>
<form action="/dbs/clear/${dbname}" method="post">
    <input type="submit" name="submit" value="clear database"/>
</form>


<h2>Populate the Database</h2>
<form action="/dbs/populate/${dbname}" method="post">
    <label>Data Source</label>
    <select name="populatorname">
    <#list populators as populatorname>
        <option>${populatorname}</option>
    </#list>
    </select>
    <input type="submit" name="submit" value="populate database"/>
</form>

<h2>Simple Retreival</h2>
<form action="/dbs/retrieval/${dbname}" method="post">
    <label>max results</label>
    <input type="input" name="max"/>

    <input type="submit" name="submit" value="retrieve graphs"/>
</form>

<h2>Structural Retreival</h2>
<form action="/dbs/structural-retrieval/${dbname}" method="post">
    <label>Retriever</label>
    <select name="retriever">
    </select>
    <label>Encoding</label>
    <select name="encoding">
        <option>default</option>
    </select>
    <label>Graph Data</label>
    <textarea name="data"></textarea>
    <label>max results</label>
    <input type="input" name="max"/>

    <label>Results Evaluators</label>
    <select multiple name="evaluators">
    </select>

    <input type="submit" name="submit" value="retrieve similar graphs"/>
</form>

<h2>Retreival Experiment</h2>
<form action="/dbs/test/${dbname}" method="post">
    <label>Retriever</label>
    <select name="retriever">
    </select>
    <label>DB Containing Queries</label>
    <input type="text" name="folds"/>
    <input type="submit" name="submit" value="launch test"/>
</form>


<h2>Cross Validation Retrieval Experiment</h2>
<form action="/dbs/test/${dbname}" method="post">
    <label>Retriever</label>
    <select name="retriever">
    </select>
    <label>Similarity Metric</label>
    <select name="metric">
        <option>default</option>
    </select>
    <label>Folds</label>
    <input type="text" name="folds"/>
    <input type="submit" name="submit" value="launch test"/>
</form>

<h2></h2>


<#include "footer.ftl">
