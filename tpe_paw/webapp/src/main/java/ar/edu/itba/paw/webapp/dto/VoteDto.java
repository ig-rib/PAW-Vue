package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Vote;

public class VoteDto {

    private boolean positive;
    private boolean selected;

    public static VoteDto fromVote(Vote vote) {
        VoteDto voteDto = new VoteDto();
        voteDto.positive = vote.isPositive();
        return voteDto;
    }

    public boolean getPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
