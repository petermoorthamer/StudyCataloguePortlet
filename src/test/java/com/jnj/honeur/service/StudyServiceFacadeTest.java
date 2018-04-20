package com.jnj.honeur.service;

import com.jnj.honeur.catalogue.model.Study;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class StudyServiceFacadeTest {

    private StudyServiceFacade studyServiceFacade = new StudyServiceFacade();

    @Test
    public void findStudies() {
        //List<Study> studies = studyServiceFacade.findStudies();
        //assertNotNull(studies);
    }

    @Test
    public void isStudyAccessibleForUser() throws PortalException {
        Study study = new Study();
        User user = Mockito.mock(User.class);

        assertFalse(studyServiceFacade.isStudyAccessibleForUser(study, user));

        study.setCollaboratorIds(new String[] { "1" });
        when(user.getOrganizationIds()).thenReturn(new long[] { 2 });

        assertFalse(studyServiceFacade.isStudyAccessibleForUser(study, user));

        study.setCollaboratorIds(new String[] { "1", "2" });
        assertTrue(studyServiceFacade.isStudyAccessibleForUser(study, user));

        study.setCollaboratorIds(new String[] { "2" });
        assertTrue(studyServiceFacade.isStudyAccessibleForUser(study, user));
    }
}