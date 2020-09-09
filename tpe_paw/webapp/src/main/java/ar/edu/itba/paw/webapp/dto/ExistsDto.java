package ar.edu.itba.paw.webapp.dto;

public class ExistsDto {

    private boolean exists;

    public static ExistsDto of(boolean exists) {
        ExistsDto existsDto = new ExistsDto();
        existsDto.setExists(exists);
        return existsDto;
    }

    public boolean getExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
