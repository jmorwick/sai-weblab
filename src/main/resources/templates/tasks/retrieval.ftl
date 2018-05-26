<script>
    function generatorSwitch(form) {
        gen = form.elements['generator'];
        query = form.elements['query'];
        format = form.elements['format'];
        if(gen.value == 'manual') {
            query.hidden = false;
            format.hidden = false;
        } else  {
            query.hidden = true;
            format.hidden = true;
        }
    }
</script>

<form action="/tasks/retrieval" method="post" ID="simple-retrieval-form">
    <legend>Retreival Experiment</legend>
    <div class="form-group">

        <label>Retriever</label>
        <select class="form-control" name="retriever">
        <#list retrievers as retrievername>
            <option>${retrievername}</option>
        </#list>
        </select>

        <label>Query Generator</label>
        <select class="form-control" name="generator" onchange="generatorSwitch(this.form)">
            <option>manual</option>
            <option>none</option>
        <#list generators as generator>
            <option>${generator}</option>
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