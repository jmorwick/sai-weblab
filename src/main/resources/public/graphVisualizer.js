function refactorGraph(graph){
    var jsonObject = JSON.parse(graph);
    var serializedGraph = JSON.stringify(jsonObject, null, 2);
    console.log(serializedGraph);
    $("#jsonGraph").text(serializedGraph);
};
