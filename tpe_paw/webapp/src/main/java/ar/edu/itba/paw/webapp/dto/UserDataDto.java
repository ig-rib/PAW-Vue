package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.Size;

import static ar.edu.itba.paw.webapp.utility.Constants.MAX_PHOTO_UPLOAD_SIZE;

public class UserDataDto {

    @Size(max=MAX_PHOTO_UPLOAD_SIZE)
    private String encodedPhoto;

    @Size(max=300, message = "{Size.profileForm.description}")
    private String description;

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
