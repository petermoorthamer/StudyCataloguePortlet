<%@ page import="com.liferay.portal.kernel.model.Organization" %>
<%@ include file="/init.jsp" %>

<h1>Study details</h1>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request"/>
<jsp:useBean id="organizations" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="users" class="java.util.ArrayList" scope="request"/>


<portlet:renderURL var="viewURL">
    <portlet:param name="mvcPath" value="/view.jsp"></portlet:param>
</portlet:renderURL>

<portlet:actionURL name="createOrSaveStudy" var="createOrSaveStudyURL">
    <portlet:param name="studyId" value="<%=String.valueOf(study.getId())%>"></portlet:param>
</portlet:actionURL>

<portlet:actionURL name="newNotebook" var="newNotebookURL">
    <portlet:param name="studyId" value="<%=String.valueOf(study.getId())%>"></portlet:param>
</portlet:actionURL>

<aui:form action="<%= createOrSaveStudyURL %>" name="<portlet:namespace />studyfm">
    <aui:fieldset>
        <aui:input label="Name" name="studyName" value="<%=study.getName()%>" />
        <aui:input label="Number" name="studyNumber" value="<%=study.getNumber()%>" />
        <aui:input label="Description" name="studyDescription" value="<%=study.getDescription()%>" />
        <aui:input label="Acknowledgments" name="studyAcknowledgments" value="<%=study.getAcknowledgments()%>" />
        <aui:select label="Study Lead" name="studyLead" value="<%=study.getLeadUserId()%>">
            <c:forEach items="${users}" var="usr">
                <aui:option value="${usr.primaryKey}">${usr.fullName}</aui:option>
            </c:forEach>
        </aui:select>
        <aui:select label="Collaborators" helpMessage="Select one or more collaborating organizations" name="collaborators" multiple="true">
            <% for(int i=0;i<organizations.size();i++) {
                Organization org = (Organization)organizations.get(i);
                boolean selected = study.hasCollaborator(org.getPrimaryKey());
            %>
                <aui:option value="<%=org.getPrimaryKey()%>" selected="<%=selected%>"><%=org.getName()%></aui:option>
            <% } %>
        </aui:select>
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
