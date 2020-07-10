package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.annotations.Secured;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

// Partly taken from StackOverflow and other
// tutorials from the inet

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtRequestFilter implements ContainerRequestFilter {

    private static final String REALM = "snippit";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        String authHeader = crc.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Extract the token from the Authorization header

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authHeader)) {
            abortWithUnauthorized(crc);
            return;
        }

        String token = authHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // TODO handle token data appropriately
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            String payload = jwt.getPayload();
            String username = jwt.getClaim("username").asString();
            Boolean isAdmin = jwt.getClaim("isAdmin").asBoolean();

        } catch (Exception e) {
            abortWithUnauthorized(crc);
        }

        // Setting up the security context in order to access current user

        final SecurityContext securityContext = crc.getSecurityContext();
        crc.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> "fuelLane";
            }

            @Override
            public boolean isUserInRole(String s) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return securityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return AUTHENTICATION_SCHEME;
            }
        });
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }
}
