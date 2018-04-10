<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.jnj.honeur.model.StudyModel" %>
<%@ page import="com.jnj.honeur.service.StudyServiceFacade" %>
<%@ page import="com.jnj.honeur.constants.StudyCataloguePortletKeys" %>
<%@ include file="../init.jsp"%>

<%
    long studyId = ParamUtil.getLong(renderRequest, "id");
    StudyModel studyModel = null;
    if (studyId > 0) {
        studyModel = StudyServiceFacade.getInstance().findStudyModelById(studyId);
    }
%>

<h1><liferay-ui:message key="study-catalogue-portlet.studymodel.add"/></h1>
<portlet:renderURL  var="listURL">
    <portlet:param name="mvcPath" value="<%=StudyCataloguePortletKeys.StudyModelAssetURLList + ".jsp"%>"/>
</portlet:renderURL>

<portlet:actionURL name="saveStudyModel" var="doSaveStudyModelURL" />

<aui:form action="<%= doSaveStudyModelURL %>" name="<portlet:namespace />StudyModelFm">
    <aui:model-context bean="<%= studyModel%>" model="<%= StudyModel.class%>" />
    <aui:fieldset>
        <aui:input name="name" />
        <aui:input name="id" type="hidden" />
    </aui:fieldset>

    <aui:button-row>
        <aui:button type="submit"/>
        <aui:button type="cancel" onClick="<%= listURL %>"/>
    </aui:button-row>
</aui:form>