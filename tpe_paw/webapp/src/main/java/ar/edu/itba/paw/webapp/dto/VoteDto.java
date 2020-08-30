package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Vote;

import java.util.Objects;

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

//    @Override
//    public int hashCode() {
//        int sum = 31 * new Boolean(positive).hashCode();
//        sum = 29 * new Boolean(selected).hashCode();
//        return sum;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteDto)) return false;
        VoteDto voteDto = (VoteDto) o;
        return positive == voteDto.positive &&
                selected == voteDto.selected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positive, selected);
    }
}
