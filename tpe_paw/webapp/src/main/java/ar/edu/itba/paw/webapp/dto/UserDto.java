package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

public class UserDto {
    private long id;
    private String username;

    public static UserDto fromUser(User user){
        final UserDto dto = new UserDto();

        dto.id = user.getId();
        dto.username = user.getUsername();

        return dto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
