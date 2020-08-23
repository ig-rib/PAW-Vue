package ar.edu.itba.paw.webapp.dto;

import java.util.List;

public class TagCreateDto {

    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
