<%@ include file="/init.jsp" %>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request"/>
<jsp:useBean id="notebook" class="com.jnj.honeur.catalogue.model.Notebook" scope="request"/>
<jsp:useBean id="sharedNotebookResult" class="com.jnj.honeur.catalogue.model.SharedNotebookResult" scope="request"/>

<%=sharedNotebookResult%>

<h2>Notebook result storage logs</h2>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= sharedNotebookResult.getStorageLogEntryList() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.storage.model.StorageLogEntry"
            modelVar="storageLogEntry">

        <liferay-ui:search-container-column-text property="user"  />
        <liferay-ui:search-container-column-text property="action"  />
        <liferay-ui:search-container-column-text property="dateTime"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<portlet:renderURL var="sharedNotebookURL" >
    <portlet:param name="studyId" value="<%= String.valueOf(study.getId())%>"/>
    <portlet:param name="notebookId" value="<%= String.valueOf(notebook.getId())%>"/>
    <portlet:param name="sharedNotebookUuid" value="<%= sharedNotebookResult.getSharedNotebook().getUuid()%>"/>
    <portlet:param name="mvcPath" value="/shared-notebook-details.jsp" />
</portlet:renderURL>

<aui:button type="cancel" value="Back" onClick="<%= sharedNotebookURL.toString() %>" />
