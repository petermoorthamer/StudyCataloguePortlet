<%@ page import="com.jnj.honeur.constants.StudyCataloguePortletKeys" %>
<%@ page import="com.jnj.honeur.service.StudyServiceFacade" %>
<%@ include file="../init.jsp"%>

<aui:button-row cssClass="studyCatalogue-buttons">
    <portlet:renderURL var="saveStudyModelURL">
        <portlet:param name="mvcPath" value="<%=StudyCataloguePortletKeys.StudyModelAssetURLEdit + ".jsp"%>"/>
    </portlet:renderURL>
    <aui:button onClick="<%=saveStudyModelURL.toString()%>" value="Add Study Model" />
</aui:button-row>

<liferay-ui:search-container total="<%=StudyServiceFacade.getInstance().findStudyModels().size()%>">
    <liferay-ui:search-container-results results="<%=StudyServiceFacade.getInstance().findStudyModels()%>" />
    <liferay-ui:search-container-row className="com.jnj.honeur.model.StudyModel" modelVar="studyModel">
        <liferay-ui:search-container-column-text property="name" />
        <liferay-ui:search-container-column-text property="uuid" />
        <liferay-ui:search-container-column-text property="userName" />
    </liferay-ui:search-container-row>
    <liferay-ui:search-iterator />
</liferay-ui:search-container>