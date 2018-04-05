<%@ include file="/init.jsp" %>

<h1>Notebook details</h1>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"></portlet:param>
</portlet:renderURL>

<portlet:actionURL name="shareNotebook" var="shareNotebookURL"></portlet:actionURL>

<aui:form action="<%= shareNotebookURL %>" name="<portlet:namespace />fm">
    <aui:fieldset>
        <aui:input name="name"></aui:input>
        <aui:input name="message"></aui:input>
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit"></aui:button>
        <aui:button type="cancel" onClick="<%= viewURL.toString() %>"></aui:button>
    </aui:button-row>
</aui:form>