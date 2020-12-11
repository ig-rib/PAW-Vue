package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

import static ar.edu.itba.paw.interfaces.service.ReportService.MIN_REPUTATION_TO_REPORT;

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
    private boolean canReport;

    public static UserDto fromUser(User user, boolean isAdmin, UriInfo uriInfo){
        final UserDto dto = new UserDto();

        dto.id = user.getId();
        dto.username = user.getUsername();
        // Need to use base for the case of current user
        dto.icon = uriInfo.getBaseUriBuilder().path("user").path(String.valueOf(user.getId())).path("profile-photo").build();
        dto.description = user.getDescription();
        dto.reputation = user.getReputation();
        dto.admin = isAdmin;
        dto.canReport = user.getReputation() >= MIN_REPUTATION_TO_REPORT;
        dto.verified = user.isVerified();
        dto.dateJoined = user.getDateJoined();
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

    public boolean isAdmin() {
        return admin;
    }

    public boolean isCanReport() {
        return canReport;
    }

    public void setCanReport(boolean canReport) {
        this.canReport = canReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return id == userDto.id &&
                reputation == userDto.reputation &&
                verified == userDto.verified &&
                admin == userDto.admin &&
                canReport == userDto.canReport &&
                Objects.equals(username, userDto.username) &&
                Objects.equals(email, userDto.email) &&
                Objects.equals(password, userDto.password) &&
                Objects.equals(description, userDto.description) &&
                Objects.equals(dateJoined, userDto.dateJoined) &&
                Objects.equals(icon, userDto.icon) &&
                Objects.equals(lang, userDto.lang) &&
                Objects.equals(region, userDto.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, description, reputation, dateJoined, icon, verified, lang, region, admin, canReport);
    }
}
