package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validations.UniqueEmail;
import ar.edu.itba.paw.webapp.validations.UniqueUsername;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegistrationDto {

    @Size(min=6, max=50)
    @Pattern(regexp = "^[a-zA-Z0-9-_.]+$")
    @NotBlank
    @UniqueUsername
    private String username;
    @Size(min=8)
    @NotBlank
    @Pattern(regexp = "^\\S*$", message = "{form.error.password}")
    private String password;
    @Email
    @NotBlank
    @UniqueEmail
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
