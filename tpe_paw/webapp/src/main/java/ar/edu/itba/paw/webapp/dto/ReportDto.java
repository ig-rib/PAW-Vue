package ar.edu.itba.paw.webapp.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReportDto {

    @NotBlank
    @Size(max=300)
    private String detail;
    private String baseUri;

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
}
