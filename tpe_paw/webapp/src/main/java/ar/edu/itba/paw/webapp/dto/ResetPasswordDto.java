package ar.edu.itba.paw.webapp.dto;

public class ResetPasswordDto {

    private String password;
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
