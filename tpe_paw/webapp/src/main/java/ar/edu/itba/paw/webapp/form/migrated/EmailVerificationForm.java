package ar.edu.itba.paw.webapp.form.migrated;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EmailVerificationForm {

    @Pattern(regexp = "^[0-9]{6}$", message = "{Pattern.verification.code}")
    @NotBlank(message = "{NotBlank.verification.code}")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}