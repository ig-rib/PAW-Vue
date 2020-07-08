package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Language;

public class LanguageDto {

    private Long id;
    private String name;
    private boolean deleted;

    public static LanguageDto fromLanguage(Language language){
        final LanguageDto dto = new LanguageDto();

        dto.id = language.getId();
        dto.name = language.getName();
        dto.deleted = language.isDeleted();

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
}