<%@ include file="../../init.jsp"%>

<jsp:useBean id="study" class="com.jnj.honeur.catalogue.model.Study" scope="request" />

<dl>
    <dt>Study</dt>
    <dd>${study.name}</dd>
    <dt>Number</dt>
    <dd>${study.number}</dd>
    <dt>Description</dt>
    <dd>${study.description}</dd>
    <dt>Acknowledgments</dt>
    <dd>${study.acknowledgments}</dd>
</dl>
