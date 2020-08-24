package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.Vote;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

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
    private Collection<URI> tags;
    private long score;
    // User-specific section
    private boolean isFavorite;
    private boolean isReported;
    private Vote vote;

    public static SnippetDto fromSnippet(Snippet snippet, UriInfo uriInfo){
        SnippetDto dto = new SnippetDto();

        dto.id = snippet.getId();
        // No use in bringing uriInfo, need to create URI from scratch
        dto.owner = uriInfo.getBaseUriBuilder().path("user").path(String.valueOf(snippet.getOwner().getId())).build();
        dto.language = LanguageDto.fromLanguage(snippet.getLanguage());
        dto.code = snippet.getCode();
        dto.title = snippet.getTitle();
        dto.description = snippet.getDescription();
        dto.dateCreated = snippet.getDateCreated();
        dto.flagged = snippet.isFlagged();
        dto.deleted = snippet.isDeleted();
        dto.tags = snippet.getTags().stream().map(tag -> uriInfo.getBaseUriBuilder().path("/tags").path(String.valueOf(tag.getId())).build()).collect(Collectors.toList());
        return dto;
    }

    public static SnippetDto fromSnippetWithDetail(Snippet snippet, UriInfo uriInfo, long score, Vote vote, boolean reported, boolean favorite) {
        SnippetDto dto = SnippetDto.fromSnippet(snippet, uriInfo);
        dto.score = score;
        dto.vote = vote;
        dto.isReported = reported;
        dto.isFavorite = favorite;
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

    public Collection<URI> getTags() {
        return tags;
    }

    public void setTags(Collection<URI> tags) {
        this.tags = tags;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
