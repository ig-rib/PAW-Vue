package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.util.Base64;

public class UserDto {
    private long id;
    private String username;
    private String email;
    private String password;
    private String description;
    private int reputation;
    private Instant dateJoined;
    private URI icon;
    private boolean verified;
    private String lang;
    private String region;
    private boolean admin;

    public static UserDto fromUser(User user, boolean isAdmin, UriInfo uriInfo){
        final UserDto dto = new UserDto();

        dto.id = user.getId();
        dto.username = user.getUsername();
        // Need to use base for the case of current user
        dto.icon = uriInfo.getBaseUriBuilder().path("user").path(String.valueOf(user.getId())).path("profile-photo").build();
        dto.description = user.getDescription();
        dto.reputation = user.getReputation();
        dto.admin = isAdmin;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getIcon() {
        return icon;
    }

    public void setIcon(URI icon) {
        this.icon = icon;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
