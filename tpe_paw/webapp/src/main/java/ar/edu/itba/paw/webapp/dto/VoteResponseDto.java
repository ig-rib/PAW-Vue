package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Vote;

import java.util.Optional;

public class VoteResponseDto {

    private long snippetScore;
    private VoteDto vote;

    public static VoteResponseDto createVoteResponse(int voteBalance, Vote vote) {
        VoteResponseDto vrDto = new VoteResponseDto();
        vrDto.setSnippetScore(voteBalance);
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
}
