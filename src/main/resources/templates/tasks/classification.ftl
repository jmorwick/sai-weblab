<form action="/tasks/classification" method="post" ID="classification-form">
    <h3>Classification Experiment</h3>
    <label>Data Source</label>
    <select class="form-control" name="db">
    <#list dbs as dbname, dbtype>
        <option>${dbname}</option>
    </#list>
    </select>
    <label>Classifier Generator</label>
    <select class="form-control" name="classifergen">
    <#list classifiers as classifiername>
        <option>${classifiername}</option>
    </#list>
    </select>
    <label>Folds</label>
    <input class="form-control" type="text" name="folds"/>
    <button type="submit" class="btn btn-primary">Launch Task</button>
</form>