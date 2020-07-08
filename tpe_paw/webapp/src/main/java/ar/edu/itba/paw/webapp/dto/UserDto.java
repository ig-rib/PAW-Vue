package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import java.time.Instant;

public class UserDto {
    private long id;
    private String username;
    private String email;
    private String password;
    private int reputation;
    private Instant dateJoined;
    private byte[] icon;
    private boolean verified;
    private String lang;
    private String region;

    public static UserDto fromUser(User user){
        final UserDto dto = new UserDto();

        dto.id = user.getId();
        dto.username = user.getUsername();

        return dto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public Instant getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Instant dateJoined) {
        this.dateJoined = dateJoined;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
