package com.jnj.honeur.catalogue.model;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Moorthamer
 */
public class SharedNotebookStatistics {

    private SharedNotebook sharedNotebook;

    private List<String> participatingOrganizations = new ArrayList<>();
    private List<String> downloadingOrganizations = new ArrayList<>();
    private List<String> uploadingOrganizations = new ArrayList<>();

    public SharedNotebookStatistics(SharedNotebook sharedNotebook) {
        this.sharedNotebook = sharedNotebook;
    }

    public List<String> getParticipatingOrganizations() {
        return participatingOrganizations;
    }
    public void setParticipatingOrganizations(List<String> participatingOrganizations) {
        this.participatingOrganizations = participatingOrganizations;
    }

    public List<String> getDownloadingOrganizations() {
        return downloadingOrganizations;
    }
    public void setDownloadingOrganizations(List<String> downloadingOrganizations) {
        this.downloadingOrganizations = downloadingOrganizations;
    }

    public List<String> getUploadingOrganizations() {
        return uploadingOrganizations;
    }
    public void setUploadingOrganizations(List<String> uploadingOrganizations) {
        this.uploadingOrganizations = uploadingOrganizations;
    }

    public String getVersion() {
        return sharedNotebook.getVersion();
    }

    public int getTotalCount() {
        return participatingOrganizations.size();
    }

    public int getDownloadCount() {
        return downloadingOrganizations.size();
    }

    public int getUploadCount() {
        return uploadingOrganizations.size();
    }

    public BigDecimal getDownloadPercentage() {
        if(getTotalCount() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal downloadPercentage = new BigDecimal(getDownloadCount())
                .divide(new BigDecimal(getTotalCount()), 10, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100));
        return downloadPercentage.setScale(1, BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal getUploadPercentage() {
        if(getTotalCount() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal uploadPercentage = new BigDecimal(getUploadCount())
                .divide(new BigDecimal(getTotalCount()), 10, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100));
        return uploadPercentage.setScale(1, BigDecimal.ROUND_HALF_EVEN);
    }

}
