<%@ include file="/init.jsp" %>

<h1>Study details</h1>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request"/>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"></portlet:param>
</portlet:renderURL>

<portlet:actionURL name="createOrSaveStudy" var="createOrSaveStudyURL" />

<portlet:actionURL name="newNotebook" var="newNotebookURL">
    <portlet:param name="studyId" value="<%=String.valueOf(study.getId())%>"></portlet:param>
</portlet:actionURL>

<aui:form action="<%= createOrSaveStudyURL %>" name="<portlet:namespace />studyfm">
    <aui:fieldset>
        <aui:input name="studyName" value="<%=study.getName()%>" />
        <aui:input name="studyNumber" value="<%=study.getNumber()%>" />
        <aui:input name="studyDescription" value="<%=study.getDescription()%>" />
        <aui:input name="studyAcknowledgments" value="<%=study.getAcknowledgments()%>" />
        <aui:input name="studyId" type="hidden" value="<%= study.getId() %>" />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit" />
        <aui:button type="cancel" onClick="<%= viewURL.toString() %>" />
    </aui:button-row>
</aui:form>


<h2>Notebooks</h2>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= study.getNotebookList() %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.Notebook"
            modelVar="notebook">

        <portlet:renderURL var="notebookDetailsURL" >
            <portlet:param name="studyId" value="<%= String.valueOf(study.getId())%>"/>
            <portlet:param name="notebookId" value="<%= String.valueOf(notebook.getId())%>"/>
            <portlet:param name="mvcPath" value="/notebook-details.jsp" />
        </portlet:renderURL>

        <liferay-ui:search-container-column-text property="id" href="<%= notebookDetailsURL %>"  />

        <liferay-ui:search-container-column-text property="url" />
        <liferay-ui:search-container-column-text><a href="<%=notebook.getUrl()%>" target="_blank">Open</a></liferay-ui:search-container-column-text>


    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:button type="submit" value="Add notebook" onClick="<%= newNotebookURL.toString() %>" />
