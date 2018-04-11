package com.jnj.honeur.service;

import com.jnj.honeur.model.StudyModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudyModelServiceFacade {

    private static final StudyModelServiceFacade INSTANCE = new StudyModelServiceFacade();

    public static StudyModelServiceFacade getInstance() {
        return INSTANCE;
    }

    private List<StudyModel> studyModels = new ArrayList<>();
    private long studyModelCnt = 0;
    private long companyId;

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Indexable(type = IndexableType.REINDEX)
    public StudyModel createStudyModel(StudyModel studyModel) {
        studyModel.setId(++studyModelCnt);
        studyModel.setCompanyId(this.companyId);
        studyModels.add(studyModel);
        return studyModel;
    }

    @Indexable(type = IndexableType.REINDEX)
    public StudyModel saveStudyModel(StudyModel studyModel) {
        return studyModel;
    }

    @Indexable(type = IndexableType.DELETE)
    public void deleteStudyModel(StudyModel studyModel) {

    }

    public List<StudyModel> findStudyModels() {
        return studyModels;
    }

    public StudyModel findStudyModelById(final Long id) {
        Optional<StudyModel> studyModel = studyModels.stream().filter(s -> s.getId().equals(id)).findFirst();
        if (studyModel.isPresent()) {
            return studyModel.get();
        }
        return null;
    }
}
