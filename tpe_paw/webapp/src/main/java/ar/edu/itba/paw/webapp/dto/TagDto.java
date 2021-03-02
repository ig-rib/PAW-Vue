package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Tag;

import java.util.Objects;

public class TagDto {

    private Long id;
    private String name;
    private Boolean snippetsUsingIsEmpty;
    private Boolean isUserFollowing;

    public static TagDto fromTag(Tag tag){
        TagDto dto = new TagDto();

        dto.id = tag.getId();
        dto.name = tag.getName();

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

    public Boolean getUserFollowing() {
        return isUserFollowing;
    }

    public void setUserFollowing(Boolean userFollowing) {
        isUserFollowing = userFollowing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagDto)) return false;
        TagDto tagDto = (TagDto) o;
        return Objects.equals(id, tagDto.id) &&
                Objects.equals(name, tagDto.name) &&
                Objects.equals(snippetsUsingIsEmpty, tagDto.snippetsUsingIsEmpty) &&
                Objects.equals(isUserFollowing, tagDto.isUserFollowing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, snippetsUsingIsEmpty, isUserFollowing);
    }
}
