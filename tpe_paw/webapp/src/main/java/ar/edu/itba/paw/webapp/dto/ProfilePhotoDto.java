package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validations.File;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ar.edu.itba.paw.webapp.utility.Constants.MAX_PHOTO_UPLOAD_SIZE;

public class ProfilePhotoDto {

    @Size(max=MAX_PHOTO_UPLOAD_SIZE)
    @NotNull
    private String encodedPhoto;

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }
}
