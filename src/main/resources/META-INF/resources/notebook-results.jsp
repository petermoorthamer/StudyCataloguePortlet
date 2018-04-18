<%@ include file="/init.jsp" %>

<jsp:useBean id="sharedStudyNotebook" class="com.jnj.honeur.catalogue.model.SharedNotebook" scope="request"/>

<h1>Notebook results</h1>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= sharedStudyNotebook.getNotebookResults() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.SharedNotebookResult"
            modelVar="notebookResult">

        <liferay-ui:search-container-column-text property="uuid"  />
        <liferay-ui:search-container-column-text property="lastModified" />
        <liferay-ui:search-container-column-text><a href="<%=notebookResult.getDownloadUrl()%>" target="_blank">Download</a></liferay-ui:search-container-column-text>

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<h2>Notebook storage logs</h2>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= sharedStudyNotebook.getStorageLogEntryList() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.storage.model.StorageLogEntry"
            modelVar="storageLogEntry">

        <liferay-ui:search-container-column-text property="user"  />
        <liferay-ui:search-container-column-text property="action"  />
        <liferay-ui:search-container-column-text property="dateTime"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>
