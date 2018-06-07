<script>
    var noquerygenerators = new Set();
    <#list noquery as genname>
        noquerygenerators.add('${genname}');
    </#list>
    function generatorSwitch(form) {
        gen = form.elements['generator'];
        query = form.elements['query'];
        format = form.elements['format'];
        ret = form.elements['retriever'];
        if(noquerygenerators.has(ret.value)) {
            query.hidden = true;
            format.hidden = true;
            gen.hidden = true;
            gen.selectedIndex = 0;
        } else {
            gen.hidden = false;
            if(gen.value == 'manual') {
                query.hidden = false;
                format.hidden = false;
            } else  {
                query.hidden = true;
                format.hidden = true;
            }
        }
    }

    function submitRetrievalForm(form) {
        if(skip.val() != parseInt(skip.val(),10))
            form.removeChild(skip);

        max = form.elements['max'];
        if(max.val() != parseInt(max.val(),10))
            form.removeChild(max);
    }
</script>

<form action="/tasks/retrieval" method="post" ID="simple-retrieval-form" onsubmit="submitRetrievalForm(this)" onload="generatorSwitch(this)">
    <legend>Retreival Experiment</legend>
    <div class="form-group">

        <label>Retriever</label>
        <select class="form-control" name="retriever" onchange="generatorSwitch(this.form)">
        <#list retrievers as retrievername>
            <option>${retrievername}</option>
        </#list>
        </select>

        <label>Query Generator</label>
        <select class="form-control" name="generator" onchange="generatorSwitch(this.form)">
            <option>none</option>
            <option>manual</option>
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