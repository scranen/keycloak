package org.keycloak.adapters.springsecurity.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.constants.AdapterConstants;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Matches a request if it contains a {@value AdapterConstants#KEYCLOAK_ADAPTER_STATE_COOKIE}
 * cookie.
 *
 * @author <a href="mailto:scranen@gmail.com">Sjoerd Cranen</a>
 */
public class AdapterStateCookieRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return false;
        }
        for (Cookie cookie: request.getCookies()) {
            if (AdapterConstants.KEYCLOAK_ADAPTER_STATE_COOKIE.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }
}
