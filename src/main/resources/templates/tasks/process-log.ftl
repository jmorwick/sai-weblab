<form action="/tasks/process-log-file" enctype="multipart/form-data"  method="post" ID=log-processing-form">
    <legend>Process an Experiment Log</legend>
    <div class="form-group">
        <label>Log File to Upload</label>
        <input type="file" name="file" />

        <label>Process Results</label>
        <select multiple name="processors[]">
        <#list logprocessors as processorname, processortype>
            <option>${processorname}</option>
        </#list>
        </select>

        <button type="submit" class="btn btn-primary">Process Log</button>
    </div>
</form>