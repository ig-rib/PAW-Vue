package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Tag;

import java.util.Collection;

public class SnippetCreateDto {
    private String title;
    private long languageId;
    private String description;
    private String code;
    private Collection<Tag> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }
}
