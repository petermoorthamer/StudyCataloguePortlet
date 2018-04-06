package com.jnj.honeur.service;

import com.jnj.honeur.model.Notebook;
import com.jnj.honeur.model.Study;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Temporary implementation for testing
 */
@Component
public class StudyServiceFacade {

    private static final StudyServiceFacade INSTANCE = new StudyServiceFacade();

    private List<Study> studies = new ArrayList<>();
    private long studyCnt = 0;
    private List<Notebook> notebooks = new ArrayList<>();
    private long notebookCnt = 0;

    public static StudyServiceFacade getInstance() {
        return INSTANCE;
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study createStudy(Study study) {
        study.setId(++studyCnt);
        studies.add(study);
        return study;
    }

    @Indexable(type = IndexableType.REINDEX)
    public Study saveStudy(Study study) {
        return study;
    }

    @Indexable(type = IndexableType.DELETE)
    public void deleteStudy(Study study) {

    }

    public List<Study> findStudies() {
        return studies;
    }

    public Study findStudyById(final Long id) {
        Optional<Study> study = studies.stream().filter(s -> s.getId().equals(id)).findFirst();
        if(study.isPresent()) {
            return study.get();
        }
        return null;
    }






    public Notebook createNotebook(Study study, Notebook notebook) {
        notebook.setId(notebookCnt++);
        notebooks.add(notebook);
        return notebook;
    }

    public List<Notebook> findStudyNotebooks(Study study) {
        return notebooks;

    }

    /*public Notebook createNotebook(Notebook notebook) {
        try {

            String[] notebookEntries = prefs.getValues(STUDY_NOTEBOOKS, new String[1]);

            ArrayList<String> entries = new ArrayList<String>();

            if (notebookEntries[0] != null) {
                entries = new ArrayList<String>(Arrays.asList(prefs.getValues(
                        STUDY_NOTEBOOKS, new String[1])));
            }

            entries.add(notebook.getUrl());

            String[] array = entries.toArray(new String[entries.size()]);

            prefs.setValues(STUDY_NOTEBOOKS, array);

            try {
                prefs.store();
            }
            catch (IOException | ValidatorException ex) {
                Logger.getLogger(StudyCataloguePortlet.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

        }
        catch (ReadOnlyException ex) {
            Logger.getLogger(StudyCataloguePortlet.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return notebook;
    }

    public List<Notebook> findStudyNotebooks(Study study) {
        String[] studyNotebooks = prefs.getValues(STUDY_NOTEBOOKS, new String[1]);

        if (studyNotebooks[0] != null) {
            return parseNotebooks(studyNotebooks);
        }

        return Collections.emptyList();
    }

    private List<Notebook> parseNotebooks(String[] studyNotebooks) {
        final List<Notebook> notebooks = new ArrayList<Notebook>();

        for (String notebookUrl : studyNotebooks) {
            Notebook notebook = new Notebook();
            notebook.setUrl(notebookUrl);
            notebooks.add(notebook);
        }

        return notebooks;
    }*/


}
