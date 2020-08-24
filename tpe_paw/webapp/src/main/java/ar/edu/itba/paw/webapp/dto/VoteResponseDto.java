package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Vote;

import java.util.Optional;

public class VoteResponseDto {

    private long snippetScore;
    private long ownerReputation;
    private VoteDto vote;

    public static VoteResponseDto createVoteResponse(long voteBalance, long ownerReputation, Vote vote) {
        VoteResponseDto vrDto = new VoteResponseDto();
        vrDto.setSnippetScore(voteBalance);
        vrDto.setOwnerReputation(ownerReputation);
        if (vote != null)
            vrDto.setVote(VoteDto.fromVote(vote));
        return vrDto;
    }

    public long getSnippetScore() {
        return snippetScore;
    }

    public void setSnippetScore(long snippetScore) {
        this.snippetScore = snippetScore;
    }

    public VoteDto getVote() {
        return vote;
    }

    public void setVote(VoteDto vote) {
        this.vote = vote;
    }

    public long getOwnerReputation() {
        return ownerReputation;
    }

    public void setOwnerReputation(long ownerReputation) {
        this.ownerReputation = ownerReputation;
    }
}
