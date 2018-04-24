<%@ include file="./init.jsp" %>

<liferay-portlet:renderURL varImpl="searchURL">
    <portlet:param name="mvcPath" value="/view_search.jsp" />
</liferay-portlet:renderURL>

<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp" />
</portlet:renderURL>


<aui:form action="<%= searchURL %>" name="searchFm">
    <liferay-portlet:renderURLParams varImpl="searchURL" />

    <liferay-ui:header backURL="<%= viewURL.toString() %>" title="search" />

    <div class="search-form">
        <span class="aui-search-bar">
            <aui:input inlineField="<%= true %>" label="" name="keywords"
                       size="30" title="search-entries" type="text" />

            <aui:button type="submit" value="search" />
        </span>
    </div>
</aui:form>

<jsp:useBean id="studies" class="java.util.ArrayList" scope="request"/>

<liferay-ui:search-container delta="20" emptyResultsMessage="No studies were found." total="<%= studies.size() %>">
    <liferay-ui:search-container-results results="<%= studies %>" />

    <liferay-ui:search-container-row
            className="com.jnj.honeur.catalogue.model.Study"
            modelVar="study">

        <portlet:renderURL var="studyDetailsURL" >
            <portlet:param name="studyId" value="<%= String.valueOf(study.getId()) %>"/>
            <portlet:param name="mvcPath" value="/study-details.jsp" />
        </portlet:renderURL>

        <liferay-ui:search-container-column-text property="id" href="<%= studyDetailsURL %>" />
        <liferay-ui:search-container-column-text property="number"  />
        <liferay-ui:search-container-column-text property="name"  />
        <liferay-ui:search-container-column-text property="description"  />

    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator />
</liferay-ui:search-container>
