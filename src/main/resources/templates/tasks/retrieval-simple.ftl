<form action="/tasks/retrieval" method="post" ID=simple-retrieval-form">
    <legend>Simple Retreival</legend>
    <div class="form-group">
        <select class="form-control" name="dbname">
        <#list dbs as dbname, dbtype>
            <option>${dbname}</option>
        </#list>
        </select>
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