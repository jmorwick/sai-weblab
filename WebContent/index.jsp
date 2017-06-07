<%-- 
    This page lists available database interfaces and allows the user to 
    query these databases.

// TODO: add option to query "all graphs" from a DBInterface
// TODO: add option to use graph retriever and query it
// TODO: add option to retrieve a single graph w/ factory of choice and 
         inspect w/ visualizer of choice
// TODO: add option to compare two graphs with map generator of choice and 
         inspect mapping w/ visualizer

    Document   : index
    Created on : Jul 6, 2014, 3:55:04 PM
    Author     : Joey Kendall-Morwick <jkendallmorwick@capital.edu>
--%>
<%@page import="net.sourcedestination.funcles.consumer.Consumer2"%>
<%@page import="net.sourcedestination.funcles.tuple.Tuple2"%>
<%@page import="net.sourcedestination.sai.weblab.Databases"%>
<%@page import="net.sourcedestination.sai.weblab.Resources"%>
<%@page import="net.sourcedestination.sai.db.DBInterface"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jspf" />
<h1>Query a database</h1>
<select name="db">
    <%
    for(Tuple2<String, DBInterface> t : Databases.getDBInterfaces()) {
    %><option><%= t._1() %></option><%
    }
    %>
</select>

<% // Why is the commented out code producing an "uncaught IOException" error?
//Databases.getDBInterfaces().stream().forEach(
//		(Consumer2<String,DBInterface>)(name, db) -> {
for(Tuple2<String,DBInterface> t : Databases.getDBInterfaces()) {
	String name = t._1();
	DBInterface db = t._2();

%>
<div>
  <h2><%= name %></h2>
  <b>size: </b> <%= db.getDatabaseSize() %>
</div>
<% }
// });   // used with commented out code above
%>

<jsp:include page="footer.jspf" />