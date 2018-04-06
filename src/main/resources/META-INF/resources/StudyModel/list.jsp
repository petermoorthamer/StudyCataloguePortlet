<%@include file="../init.jsp" %>

<portlet:renderURL var="addStudyModelURL">
    <portlet:param name="mvcPath" value="/StudyModel/add.jsp"/>
</portlet:renderURL>
<portlet:renderURL var="returnURL">
    <portlet:param name="mvcPath" value="/view.jsp"/>
</portlet:renderURL>

<h1><liferay-ui:message key="study-catalogue-portlet.studymodel.list"/></h1>
<aui:button-row>
    <aui:button onClick="<%= addStudyModelURL %>" value="Add Study Model"/>
    <aui:button onClick="<%= returnURL %>" value="Back"/>
</aui:button-row>

<jsp:useBean id="studyModels" class="java.util.ArrayList" scope="request"/>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= studyModels %>"/>
    <liferay-ui:search-container-row className="com.jnj.honeur.model.StudyModel" modelVar="studyModel">
        <liferay-ui:search-container-column-text property="name"/>
        <liferay-ui:search-container-column-text property="id"/>
        <liferay-ui:search-container-column-text property="companyId"/>
    </liferay-ui:search-container-row>
    <liferay-ui:search-iterator/>
</liferay-ui:search-container>
