package ar.edu.itba.paw.webapp.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReportDto {

    @NotBlank
    @Size(max=300)
    private String detail;
    private String baseUri;
    private boolean ownerDismissed;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public boolean isOwnerDismissed() {
        return ownerDismissed;
    }

    public void setOwnerDismissed(boolean ownerDismissed) {
        this.ownerDismissed = ownerDismissed;
    }
}
