package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.security.Principal;
import java.util.Optional;

@Component
public class LoginAuthentication {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthentication.class);

    public String getLoggedInUsername() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Optional<User> getLoggedInUser() {
        final String username = this.getLoggedInUsername();
        if (username == null) {
            return Optional.empty();
        }
        return this.findUser(username);
    }

    private Optional<User> findUser(String username) {
        final Optional<User> user = userService.findUserByUsername(username);
        if (!user.isPresent()) {
            LOGGER.warn("Logged user {} not found in the database", username);
        }
        return user;
    }

}
