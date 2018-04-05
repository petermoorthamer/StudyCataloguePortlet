<%@ include file="/init.jsp" %>

<p>
	<b><liferay-ui:message key="study-catalogue-portlet.caption"/></b>
</p>

<portlet:renderURL var="notebookDetailsURL">
	<portlet:param name="mvcPath" value="/notebook-details.jsp"></portlet:param>
</portlet:renderURL>

<portlet:actionURL name="addNotebook" var="addNotebookURL"></portlet:actionURL>

<jsp:useBean id="studyNotebooks" class="java.util.ArrayList" scope="request"/>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= studyNotebooks %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.model.Notebook"
            modelVar="notebook">

        <liferay-ui:search-container-column-text property="url"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>


<aui:form action="<%= addNotebookURL %>" name="<portlet:namespace />fm">
    <aui:fieldset>
        <aui:input name="notebookUrl"></aui:input>
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit"></aui:button>
    </aui:button-row>
</aui:form>

<aui:button-row>
	<aui:button value="notebook details" onClick="<%= notebookDetailsURL.toString() %>"></aui:button>
</aui:button-row>
