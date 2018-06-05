<form action="/tasks/process-log-file" enctype="multipart/form-data"  method="post" ID=log-processing-form">
    <legend>Process an Experiment Log</legend>
    <div class="form-group">
        <label>Log to process</label>
        <select name="log">
        <#list logs as name>
            <option>${name}</option>
        </#list>
        </select>

        <label>Processors</label>
        <select multiple name="processors[]">
        <#list logprocessors as processorname, processortype>
            <option>${processorname}</option>
        </#list>
        </select>

        <button type="submit" class="btn btn-primary">Process Log</button>
    </div>
</form>