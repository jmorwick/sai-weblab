<form action="/tasks/retrieval" method="post" ID=simple-retrieval-form">
    <legend>Structural Retreival</legend>
    <div class="form-group">
        <select class="form-control" name="dbname">
        <#list dbs as dbname, dbtype>
            <option>${dbname}</option>
        </#list>
        </select>

        <label>Retriever</label>
        <select class="form-control" name="retriever">
        <#list retrievers as retrievername>
            <option>${retrievername}</option>
        </#list>
        </select>

        <label>Graph Data</label>
        <textarea class="form-control" name="query"></textarea>

        <label>Decoder</label>
        <select class="form-control" name="format">
            <#list decoders as decodername>
                <option <#if decodername == defaultdecoder>SELECTED</#if>>${decodername}</option>
            </#list>
        </select>

        <label>Number of Results to Skip</label>
        <input class="form-control" type="input" name="skip"/>

        <label>Max Results</label>
        <input class="form-control" type="input" name="max"/>

        <button type="submit" class="btn btn-primary">Retrieve Graphs</button>
    </div>
</form>