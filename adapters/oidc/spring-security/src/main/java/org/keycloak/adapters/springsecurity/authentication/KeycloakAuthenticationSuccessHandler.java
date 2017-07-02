package org.keycloak.adapters.springsecurity.authentication;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Wrapper for an authentication success handler that sends a redirect if a redirect URL was set in
 * a cookie.
 *
 * @author <a href="mailto:scranen@gmail.com">Sjoerd Cranen</a>
 * 
 * @see KeycloakCookieBasedRedirect
 * @see KeycloakAuthenticationEntryPoint#commenceLoginRedirect
 */
public class KeycloakAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakAuthenticationSuccessHandler.class);

    private final AuthenticationSuccessHandler fallback;

    public KeycloakAuthenticationSuccessHandler(AuthenticationSuccessHandler fallback) {
        this.fallback = fallback;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String location = KeycloakCookieBasedRedirect.getRedirectUrlFromCookie(request);
        if (location == null) {
            if (fallback != null) {
                fallback.onAuthenticationSuccess(request, response, authentication);
            }
        } else {
            try {
                response.addCookie(KeycloakCookieBasedRedirect.createCookieFromRedirectUrl(""));
                response.sendRedirect(location);
            } catch (IOException e) {
                LOG.warn("Unable to redirect user after login", e);
            }
        }
    }
}
