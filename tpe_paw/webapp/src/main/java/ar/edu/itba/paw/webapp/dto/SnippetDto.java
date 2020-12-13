package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vote;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class SnippetDto {

    private Long id;
    private URI owner;
    private URI language;
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
    private boolean reportedDismissed;
    private VoteDto vote;

    public static SnippetDto fromSnippet(Snippet snippet, UriInfo uriInfo){
        SnippetDto dto = new SnippetDto();

        dto.id = snippet.getId();
        // No use in bringing uriInfo, need to create URI from scratch
        dto.owner = uriInfo.getBaseUriBuilder().path("user").path(String.valueOf(snippet.getOwner().getId())).build();
        dto.language = uriInfo.getBaseUriBuilder().path("languages").path(String.valueOf(snippet.getLanguage().getId())).build();
        dto.code = snippet.getCode();
        dto.title = snippet.getTitle();
        dto.description = snippet.getDescription();
        dto.dateCreated = snippet.getDateCreated();
        dto.flagged = snippet.isFlagged();
        dto.deleted = snippet.isDeleted();
        dto.tags = snippet.getTags().stream().map(tag -> uriInfo.getBaseUriBuilder().path("/tags").path(String.valueOf(tag.getId())).build()).collect(Collectors.toList());
        return dto;
    }

    public static SnippetDto fromSnippet(Snippet snippet, UriInfo uriInfo, User loggedInUser) {
        SnippetDto dto = SnippetDto.fromSnippet(snippet, uriInfo);
        if (loggedInUser != null)
            dto.isFavorite = loggedInUser.getFavorites().contains(snippet);
        return dto;
    }

    public static SnippetDto fromSnippetWithDetail(Snippet snippet, UriInfo uriInfo, long score, Vote vote, boolean reported, boolean favorite, boolean reportedDismissed) {
        SnippetDto dto = SnippetDto.fromSnippet(snippet, uriInfo);
        dto.score = score;
        if (vote != null)
            dto.vote = VoteDto.fromVote(vote);
        dto.isReported = reported;
        dto.reportedDismissed = reportedDismissed;
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

    public VoteDto getVote() {
        return vote;
    }

    public void setVote(VoteDto vote) {
        this.vote = vote;
    }

    public URI getLanguage() {
        return language;
    }

    public void setLanguage(URI language) {
        this.language = language;
    }

    public boolean isReportedDismissed() {
        return reportedDismissed;
    }

    public void setReportedDismissed(boolean reportedDismissed) {
        this.reportedDismissed = reportedDismissed;
    }
    //    @Override
//    public int hashCode() {
//        int sum = this.id.hashCode();
//        sum += 19 * new Boolean(this.isFavorite).hashCode();
//        sum += 43 * new Boolean(this.isReported).hashCode();
//        sum += 23 * new Boolean(this.flagged).hashCode();
//        sum += 17 * new Boolean(this.deleted).hashCode();
//        sum += this.getVote() != null ? 29 * this.getVote().hashCode() : 0;
//        sum += 31 * (this.score >= 0 ? this.score : 37 * -this.score);
//        sum += 31 * this.dateCreated.hashCode();
//        sum += 31 * this.description.hashCode();
//        sum += 31 * this.title.hashCode();
//        sum += 31 * this.code.hashCode();
//        sum += 31 * this.language.hashCode();
//        sum += 31 * this.owner.hashCode();
//        sum += 31 * this.tags.hashCode();
//        return sum;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SnippetDto)) return false;
        SnippetDto that = (SnippetDto) o;
        return flagged == that.flagged &&
                deleted == that.deleted &&
                score == that.score &&
                isFavorite == that.isFavorite &&
                isReported == that.isReported &&
                reportedDismissed == that.reportedDismissed &&
                Objects.equals(id, that.id) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(language, that.language) &&
                Objects.equals(code, that.code) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(dateCreated, that.dateCreated) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(vote, that.vote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, language, code, title, description, dateCreated, flagged, deleted, tags, score, isFavorite, isReported, vote, reportedDismissed);
    }
}
