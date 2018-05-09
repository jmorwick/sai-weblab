<form action="/dbs/retrieval" method="post">
    <legend>Structural Retrieval</legend>
        <label>Retriever</label>
        <select class="form-control" name="retriever">
        <#list retrievers as retrievername>
            <option>${retrievername}</option>
        </#list>
        </select>
        <label>Encoding</label>
        <select class="form-control" name="format">
            <#list decoders as decodername>
                <option<#if decodername == defaultdecoder> SELECTED</#if>>${decodername}</option>
            </#list>
        </select>
        <label>Graph Data</label>
        <textarea class="form-control" name="query"></textarea>
        <label>skip initial n results</label>
        <input class="form-control" type="input" name="skip"/>
        <label>max results</label>
        <input class="form-control" type="input" name="max"/>

        <button type="submit" class="btn btn-primary">Retrieve Similar Graphs</button>
</form>