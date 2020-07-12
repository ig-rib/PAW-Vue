package ar.edu.itba.paw.webapp.dto;

public class RegistrationErrorDto {

    private boolean emailExists;
    private boolean usernameExists;

    public boolean isEmailExists() {
        return emailExists;
    }

    public void setEmailExists(boolean emailExists) {
        this.emailExists = emailExists;
    }

    public boolean isUsernameExists() {
        return usernameExists;
    }

    public void setUsernameExists(boolean usernameExists) {
        this.usernameExists = usernameExists;
    }
}
