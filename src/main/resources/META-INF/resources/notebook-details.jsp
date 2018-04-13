<%@ include file="/init.jsp" %>

<h1>Notebook details</h1>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request"/>
<jsp:useBean id="notebook" class="com.jnj.honeur.catalogue.model.Notebook" scope="request"/>

<%=study%>
<br/>
<%=notebook%>

<portlet:renderURL var="studyDetailsURL" >
    <portlet:param name="studyId" value="<%= String.valueOf(study.getId()) %>"/>
    <portlet:param name="mvcPath" value="/study-details.jsp" />
</portlet:renderURL>

<portlet:actionURL name="createOrSaveNotebook" var="createOrSaveNotebookURL" />

<portlet:actionURL name="shareNotebook" var="shareNotebookURL"></portlet:actionURL>

<aui:form action="<%= createOrSaveNotebookURL %>" name="<portlet:namespace />notebookfm">
    <aui:fieldset>
        <aui:input name="notebookUrl" value="<%=notebook.getUrl()%>" />
        <aui:input name="notebookId" type="hidden" value="<%= notebook.getId() %>" />
        <aui:input name="studyId" type="hidden" value="<%= study.getId() %>" />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit" />
        <aui:button type="cancel" onClick="<%= studyDetailsURL.toString() %>" />
    </aui:button-row>
</aui:form>