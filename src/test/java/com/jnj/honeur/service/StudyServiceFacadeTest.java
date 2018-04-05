package com.jnj.honeur.service;

import com.jnj.honeur.model.Study;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StudyServiceFacadeTest {

    private StudyServiceFacade studyServiceFacade = new StudyServiceFacade();

    @Test
    public void findStudyById() {
        studyServiceFacade.createStudy(new Study());
        studyServiceFacade.createStudy(new Study());
        assertNotNull(studyServiceFacade.findStudyById(1L));
        assertNotNull(studyServiceFacade.findStudyById(2L));
        assertNull(studyServiceFacade.findStudyById(3L));


    }
}