package com.jnj.honeur.model;


import com.liferay.portal.kernel.model.impl.BaseModelImpl;

import java.io.Serializable;

public class StudyModel extends BaseModelImpl<Study> {
    public static final String TITLE = "name";
    public static final String UID = "id";
    public static final String COMPANY_ID = "companyId";
    public static final String GROUP_ID = "groupId";

    private static final Boolean ENTITY_CACHE_ENABLED = true;
    private static final Boolean FINDER_CACHE_ENABLED = true;

    private Study study;
    private Long companyId;
    private Long groupId;

    public StudyModel(Study study) {
        this.study = study;
    }

    private StudyModel(Study study, Long companyId, Long groupId) {
        this.study = study;
        this.companyId = companyId;
        this.groupId = groupId;
    }

    public Long getId() {
        return study.getId();
    }

    public void setId(Long id) {
        study.setId(id);
    }

    public String getName() {
        return study.getName();
    }

    public void setName(String name) {
        study.setName(name);
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public Serializable getPrimaryKeyObj() {
        return study.getId();
    }

    @Override
    public void setPrimaryKeyObj(Serializable primaryKeyObj) {
        study.setId((Long) primaryKeyObj);
    }

    @Override
    public String toXmlString() {
        return null;
    }

    @Override
    public Object clone() {
        return new StudyModel(study, companyId, groupId);
    }

    @Override
    public int compareTo(Study o) {
        return 0;
    }

    @Override
    public Class<?> getModelClass() {
        return Study.class;
    }

    @Override
    public String getModelClassName() {
        return Study.class.getName();
    }

    @Override
    public boolean isEntityCacheEnabled() {
        return ENTITY_CACHE_ENABLED;
    }

    @Override
    public boolean isFinderCacheEnabled() {
        return FINDER_CACHE_ENABLED;
    }
}
