package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Tag;
import ar.edu.itba.paw.webapp.validations.FieldExists;
import ar.edu.itba.paw.webapp.validations.NotBlankWithSpaces;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Collection;

public class SnippetCreateDto {

    @Size(min=5, max=50)
    @NotBlankWithSpaces
    private String title;
    @Min(value=1)
    @FieldExists(fieldName = "Language")
    private long languageId;
    @Size(max=500)
    private String description;
    @Size(min=5, max=30000)
    @NotBlankWithSpaces
    private String code;
    private Collection<Long> tags;

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

    public Collection<Long> getTags() {
        return tags;
    }

    public void setTags(Collection<Long> tags) {
        this.tags = tags;
    }
}
