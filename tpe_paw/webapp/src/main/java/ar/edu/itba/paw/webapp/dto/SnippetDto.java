package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;

import java.time.Instant;

public class SnippetDto {

    private Long id;
    private User owner;
    private String code;
    private String title;
    private String description;
    private Instant dateCreated;
    private boolean flagged;
    private boolean deleted;

    public static SnippetDto fromSnippet(Snippet snippet){
        SnippetDto dto = new SnippetDto();

        dto.id = snippet.getId();
        dto.owner = snippet.getOwner();
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
}
