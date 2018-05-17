<form action="/tasks/populate" method="post" ID=database-populator-form">
    <legend>Populate a Database</legend>
    <div class="form-group">
        <label>Data Source</label>
        <select class="form-control" name="populatorname">
        <#list populators as populatorname>
            <option>${populatorname}</option>
        </#list>
        </select>


        <label>Database</label>
        <select class="form-control" name="dbname">
        <#list dbs as dbname, dbtype>
            <option>${dbname}</option>
        </#list>
        </select>

        <button type="submit" class="btn btn-primary">Populate Database</button>
    </div>
</form>