<%@ include file="/init.jsp" %>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request"/>
<jsp:useBean id="notebook" class="com.jnj.honeur.catalogue.model.Notebook" scope="request"/>
<jsp:useBean id="sharedNotebook" class="com.jnj.honeur.catalogue.model.SharedNotebook" scope="request"/>

<h1>Notebook results</h1>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= sharedNotebook.getNotebookResults() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.SharedNotebookResult"
            modelVar="notebookResult">

        <portlet:renderURL var="notebookResultURL" >
            <portlet:param name="studyId" value="<%= String.valueOf(study.getId())%>"/>
            <portlet:param name="notebookId" value="<%= String.valueOf(notebook.getId())%>"/>
            <portlet:param name="sharedNotebookUuid" value="<%= sharedNotebook.getUuid()%>"/>
            <portlet:param name="sharedNotebookResultUuid" value="<%= notebookResult.getUuid()%>"/>
            <portlet:param name="mvcPath" value="/notebook-result-details.jsp" />
        </portlet:renderURL>

        <liferay-ui:search-container-column-text property="uuid" href="<%= notebookResultURL %>" />
        <liferay-ui:search-container-column-text property="creationDate" />
        <liferay-ui:search-container-column-text><a href="<%=notebookResult.getDownloadUrl()%>" target="_blank">Download</a></liferay-ui:search-container-column-text>

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<h2>Notebook storage logs</h2>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= sharedNotebook.getStorageLogEntryList() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.storage.model.StorageLogEntry"
            modelVar="storageLogEntry">

        <liferay-ui:search-container-column-text property="user"  />
        <liferay-ui:search-container-column-text property="action"  />
        <liferay-ui:search-container-column-text property="dateTime"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<portlet:renderURL var="notebookDetailsURL" >
    <portlet:param name="studyId" value="<%= String.valueOf(study.getId()) %>"/>
    <portlet:param name="notebookId" value="<%= String.valueOf(notebook.getId()) %>"/>
    <portlet:param name="mvcPath" value="/notebook-details.jsp" />
</portlet:renderURL>

<aui:button type="cancel" value="Back" onClick="<%= notebookDetailsURL.toString() %>" />
