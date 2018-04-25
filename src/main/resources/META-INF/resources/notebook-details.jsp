<%@ include file="/init.jsp" %>

<h1>Notebook details</h1>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request"/>
<jsp:useBean id="notebook" class="com.jnj.honeur.catalogue.model.Notebook" scope="request"/>

<portlet:renderURL var="studyDetailsURL" >
    <portlet:param name="studyId" value="<%= String.valueOf(study.getId()) %>"/>
    <portlet:param name="mvcPath" value="/study-details.jsp" />
</portlet:renderURL>

<portlet:actionURL name="createOrSaveNotebook" var="createOrSaveNotebookURL" />

<aui:form action="<%= createOrSaveNotebookURL %>" name="<portlet:namespace />editNotebookFm">
    <aui:fieldset>
        <aui:input name="notebookName" value="<%=notebook.getName()%>" />
        <aui:input name="notebookUrl" value="<%=notebook.getUrl()%>" />
        <aui:input name="notebookId" type="hidden" value="<%= notebook.getId() %>" />
        <aui:input name="studyId" type="hidden" value="<%= study.getId() %>" />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit">
            <a href="<%=notebook.getUrl()%>" target="_blank">Open</a>
        </aui:button>
        <aui:button type="cancel" onClick="<%= studyDetailsURL.toString() %>" />
    </aui:button-row>
</aui:form>

<h2>Shared versions</h2>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= notebook.getSharedNotebookList() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.SharedNotebook"
            modelVar="sharedNotebook">

        <portlet:renderURL var="sharedNotebookURL" >
            <portlet:param name="studyId" value="<%= String.valueOf(study.getId())%>"/>
            <portlet:param name="notebookId" value="<%= String.valueOf(notebook.getId())%>"/>
            <portlet:param name="sharedNotebookUuid" value="<%= sharedNotebook.getUuid()%>"/>
            <portlet:param name="mvcPath" value="/shared-notebook-details.jsp" />
        </portlet:renderURL>

        <portlet:actionURL name="deleteSharedNotebook" var="deleteSharedNotebookURL">
            <portlet:param name="studyId" value="<%= String.valueOf(study.getId())%>"/>
            <portlet:param name="notebookId" value="<%= String.valueOf(notebook.getId())%>"/>
            <portlet:param name="sharedNotebookUuid" value="<%= sharedNotebook.getUuid()%>"/>
        </portlet:actionURL>

        <liferay-ui:search-container-column-text name="UUID" property="uuid" href="<%= sharedNotebookURL %>"  />
        <liferay-ui:search-container-column-text name="Filename" property="filename"  />
        <liferay-ui:search-container-column-text name="Version" property="version"  />
        <liferay-ui:search-container-column-text name="Created By" property="createdBy" />
        <liferay-ui:search-container-column-text name="Creation Date" property="creationDate" />
        <liferay-ui:search-container-column-text>
            <aui:button type="submit" value="Delete" onClick="<%= deleteSharedNotebookURL.toString() %>" />
        </liferay-ui:search-container-column-text>
        <liferay-ui:search-container-column-text><a href="<%=sharedNotebook.getDownloadUrl()%>" target="_blank">Download</a></liferay-ui:search-container-column-text>

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<portlet:actionURL name="shareNotebook" var="shareNotebookURL"></portlet:actionURL>

<aui:form action="<%= shareNotebookURL %>" name="<portlet:namespace />shareNotebookFm">
    <aui:fieldset>
        <aui:input name="studyId" type="hidden" value="<%= study.getId() %>" />
        <aui:input name="notebookId" type="hidden" value="<%= notebook.getId() %>" />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit" value="Share" />
    </aui:button-row>
</aui:form>


<h2>Notebook result statistics</h2>

<jsp:useBean id="notebookStatistics" class="java.util.ArrayList" scope="request"/>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= notebookStatistics %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.SharedNotebookStatistics"
            modelVar="statistics">

        <liferay-ui:search-container-column-text name="Version" property="version"  />
        <liferay-ui:search-container-column-text name="# Organizations" property="totalCount"  />
        <liferay-ui:search-container-column-text name="# Downloaded" property="downloadCount"  />
        <liferay-ui:search-container-column-text name="% Downloaded" property="downloadPercentage"  />
        <liferay-ui:search-container-column-text name="# Uploaded results" property="uploadCount" />
        <liferay-ui:search-container-column-text name="% Uploaded results" property="uploadPercentage"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>


