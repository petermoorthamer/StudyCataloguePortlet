<%@ include file="./init.jsp" %>

<h1><liferay-ui:message key="study-catalogue-portlet.caption"/></h1>

<portlet:actionURL name="addStudy" var="addStudyURL" />

<liferay-portlet:renderURL varImpl="searchURL">
    <portlet:param name="mvcPath" value="/view_search.jsp" />
</liferay-portlet:renderURL>

<liferay-portlet:renderURL var="studyModels">
    <portlet:param name="mvcPath" value="/StudyModel/view.jsp" />
</liferay-portlet:renderURL>
<aui:button onClick="<%= studyModels %>" value="Study Models" />

<aui:form action="<%= searchURL %>" method="get" name="<portlet:namespace />searchfm">
    <liferay-portlet:renderURLParams varImpl="searchURL" />

    <div class="search-form">
        <span class="aui-search-bar">
            <aui:input inlineField="<%= true %>" label=""
                       name="keywords" size="30" title="search-entries" type="text"
            />

            <aui:button type="submit" value="search" />
        </span>
    </div>
</aui:form>

<jsp:useBean id="studies" class="java.util.ArrayList" scope="request"/>

<h3>Studies</h3>

<liferay-ui:search-container>
    <liferay-ui:search-container-results results="<%= studies %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.Study"
            modelVar="study">

        <liferay-ui:search-container-column-text property="name"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>

<h3>Add study</h3>

<aui:form action="<%= addStudyURL %>" name="<portlet:namespace />fm">
    <aui:fieldset>
        <aui:input name="studyName"></aui:input>
    </aui:fieldset>
    <aui:fieldset>
        <aui:input name="studyNumber"></aui:input>
    </aui:fieldset>
    <aui:fieldset>
        <aui:input name="studyDescription"></aui:input>
    </aui:fieldset>
    <aui:fieldset>
        <aui:input name="studyAcknowledgments"></aui:input>
    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit"></aui:button>
    </aui:button-row>
</aui:form>


