package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validations.FieldMatch;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@FieldMatch(first = "newPassword", second = "repeatNewPassword", message = "{FieldMatch.resetPasswordForm.passwords}")
public class ResetPasswordForm {

    @Size(min=8)
    @NotBlank
    private String newPassword;
    @Size(min=8)
    @NotBlank
    private String repeatNewPassword;
    private String email;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
