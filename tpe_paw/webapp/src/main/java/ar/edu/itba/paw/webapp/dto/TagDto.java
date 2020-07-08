package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.Tag;

public class TagDto {

    private Long id;
    private String name;
    private Boolean snippetsUsingIsEmpty;

    public static TagDto fromRole(Tag tag){
        TagDto dto = new TagDto();

        dto.id = tag.getId();
        dto.name = tag.getName();
        dto.snippetsUsingIsEmpty = tag.getSnippetsUsingIsEmpty();

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSnippetsUsingIsEmpty() {
        return snippetsUsingIsEmpty;
    }

    public void setSnippetsUsingIsEmpty(Boolean snippetsUsingIsEmpty) {
        this.snippetsUsingIsEmpty = snippetsUsingIsEmpty;
    }
}
