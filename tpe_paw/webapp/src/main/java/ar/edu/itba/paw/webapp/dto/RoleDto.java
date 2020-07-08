package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Role;

public class RoleDto {

    private Long id;
    private String name;

    public static RoleDto fromRole(Role role){
        RoleDto dto = new RoleDto();

        dto.id = role.getId();
        dto.name = role.getName();

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
