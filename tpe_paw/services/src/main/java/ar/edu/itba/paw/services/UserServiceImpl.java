package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.dao.UserDao;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    private final int FLAGGED_SNIPPET_REP_VALUE = 10;

    @Override
    public long createUser(String username, String password, String email, String description, int reputation, String dateJoined) {
        return userDao.createUser(username, password, email, description, reputation, dateJoined);
    }

    @Override
    public long register(String username, String password, String email, String dateJoined) {
        long userId = createUser(username, password, email, "", 0, dateJoined);
        this.emailService.sendRegistrationEmail(email, username);
        return userId;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public Optional<User> findUserById(long id) {
        return this.userDao.findUserById(id);
    }

    @Override
    public void updateDescription(String username, String newDescription) {
        userDao.updateDescription(username, newDescription);
    }

    // TODO, should this ask for the current password?
    @Override
    public void changePassword(String email, String password) {
        userDao.changePassword(email, password);
    }

    @Override
    public boolean isEmailUnique(String email) {
        return !userDao.findUserByEmail(email).isPresent();
    }

    @Override
    public boolean isUsernameUnique(String username) {
        return !userDao.findUserByUsername(username).isPresent();
    }

    @Override
    public void changeProfilePhoto(long userId, byte[] photo) {
        this.userDao.changeProfilePhoto(userId, photo);
    }

    @Override
    public void changeDescription(final long userId, final String description) {
        this.userDao.changeDescription(userId, description);
    }

    @Override
    public boolean isAdmin(final User user) {
        return user.getUsername().compareTo("admin") == 0;
    }

    @Override
    public void changeReputationForFlaggedSnippet(long userId, boolean add) {
        userDao.changeReputation(userId, FLAGGED_SNIPPET_REP_VALUE, add);
    }

    public Collection<User> getAllUsers() {
        return this.userDao.getAllUsers();
    }
}
