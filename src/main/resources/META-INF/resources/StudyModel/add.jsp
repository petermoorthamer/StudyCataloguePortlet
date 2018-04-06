<%@ include file="../init.jsp"%>

<h1><liferay-ui:message key="study-catalogue-portlet.studymodel.add"/></h1>
<portlet:renderURL  var="listURL">
    <portlet:param name="mvcPath" value="/StudyModel/list.jsp"/>
</portlet:renderURL>

<portlet:actionURL name="addStudyModel" var="doAddStudyModelURL" />

<aui:form action="<%= doAddStudyModelURL %>" name="<portlet:namespace />addStudyModelFm">
    <aui:fieldset>
        <aui:input name="name" />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit"/>
        <aui:button type="cancel" onClick="<%= listURL %>"/>
    </aui:button-row>
</aui:form>