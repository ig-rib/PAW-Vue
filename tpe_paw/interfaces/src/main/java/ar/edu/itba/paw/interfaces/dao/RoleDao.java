package ar.edu.itba.paw.interfaces.dao;


import ar.edu.itba.paw.models.Role;

import java.util.Collection;

public interface RoleDao {
    Collection<Role> getAllRoles();
    Collection<String> getUserRoles(final long userId);
    void assignUserRole(final long userId);
    void assignAdminRole(final long userId);

    String getAdminRoleName();
    String getUserRoleName();
}