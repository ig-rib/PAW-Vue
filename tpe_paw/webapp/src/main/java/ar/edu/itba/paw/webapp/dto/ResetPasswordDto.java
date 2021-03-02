package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validations.FieldMatch;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@FieldMatch(first = "password", second = "repeatPassword", message = "{FieldMatch.resetPasswordForm.passwords}")
public class ResetPasswordDto {

    private String token;
    @Size(min=8)
    @NotBlank
    @Pattern(regexp = "^\\S*$", message = "{form.error.password}")
    private String password;
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
