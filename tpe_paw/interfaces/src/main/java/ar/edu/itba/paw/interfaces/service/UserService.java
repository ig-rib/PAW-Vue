package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.models.User;

import java.util.Date;
import java.util.Optional;

public interface UserService {
    User createUser(String username, String password, String email, String description, int reputation, Date dateJoined);

    User register(String username, String password, String email, Date dateJoined);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserById(final long id);

    Optional<User> findUserByEmail(String email);

    void updateDescription(String username, String newDescription);

    void changePassword(String email, String password);

    boolean isEmailUnique(String email);

    boolean isUsernameUnique(String username);

    boolean emailExists(String email);

    void changeProfilePhoto(final long userId, final byte[] photo);

    void changeDescription(final long userId, final String description);
}
