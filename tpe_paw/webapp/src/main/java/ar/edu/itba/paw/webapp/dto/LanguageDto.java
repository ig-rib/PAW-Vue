package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Language;

import java.util.Objects;

public class LanguageDto {

    private Long id;
    private String name;
    private boolean deleted;
    private boolean snippetsUsingIsEmpty;

    public static LanguageDto fromLanguage(Language language){
        final LanguageDto dto = new LanguageDto();

        dto.id = language.getId();
        dto.name = language.getName();
        dto.deleted = language.isDeleted();
        dto.snippetsUsingIsEmpty = language.getSnippetsUsing().size() == 0;

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSnippetsUsingIsEmpty() {
        return snippetsUsingIsEmpty;
    }

    public void setSnippetsUsingIsEmpty(boolean snippetsUsingIsEmpty) {
        this.snippetsUsingIsEmpty = snippetsUsingIsEmpty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LanguageDto)) return false;
        LanguageDto that = (LanguageDto) o;
        return deleted == that.deleted &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deleted);
    }

}
