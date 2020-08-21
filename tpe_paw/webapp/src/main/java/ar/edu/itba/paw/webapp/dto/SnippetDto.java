package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Snippet;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;

import static ar.edu.itba.paw.webapp.utility.Constants.BACK_BASE_URL_LOCAL;
import static ar.edu.itba.paw.webapp.utility.Constants.FRONT_BASE_URL_LOCAL;

public class SnippetDto {

    private Long id;
    private URI owner;
    private LanguageDto language;
    private String code;
    private String title;
    private String description;
    private Instant dateCreated;
    private boolean flagged;
    private boolean deleted;

    public static SnippetDto fromSnippet(Snippet snippet){
        SnippetDto dto = new SnippetDto();

        dto.id = snippet.getId();
        // No use in bringing uriInfo, need to create URI from scratch
        dto.owner = UriBuilder.fromPath(BACK_BASE_URL_LOCAL).path("user").path(String.valueOf(snippet.getOwner().getId())).build();
        dto.language = LanguageDto.fromLanguage(snippet.getLanguage());
        dto.code = snippet.getCode();
        dto.title = snippet.getTitle();
        dto.description = snippet.getDescription();
        dto.dateCreated = snippet.getDateCreated();
        dto.flagged = snippet.isFlagged();
        dto.deleted = snippet.isDeleted();

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public LanguageDto getLanguage() {
        return language;
    }

    public void setLanguage(LanguageDto language) {
        this.language = language;
    }
}
