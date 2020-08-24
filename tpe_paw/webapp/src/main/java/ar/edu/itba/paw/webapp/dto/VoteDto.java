package ar.edu.itba.paw.webapp.dto;

public class VoteDto {

    private boolean isPositive;
    private boolean selected;

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        this.isPositive = positive;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
