package org.keycloak.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.keycloak.adapters.spi.HttpFacade;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author <a href="mailto:scranen@gmail.com">Sjoerd Cranen</a>
 */
public class CookieTokenStoreTest {

    @Mock
    private KeycloakDeployment deployment;

    @Mock
    private HttpFacade httpFacade;

    @Mock
    private HttpFacade.Request request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(httpFacade.getRequest()).thenReturn(request);
    }

    @Test
    public void testGetContextPath() {
        // The context path should be / if there is no path component
        when(request.getURI()).thenReturn("http://domain/");
        assertEquals("/", CookieTokenStore.getContextPath(httpFacade));
        // In the following case, the path component of the KeycloakUriBuilder is null
        when(request.getURI()).thenReturn("http://domain");
        assertEquals("/", CookieTokenStore.getContextPath(httpFacade));
        // When there is a path component, the first part should be returned
        // XXX: Would it not be more appropriate to say that the context path is / in this case?
        // XXX: CookieTokenStoreAdapterTest relies on the current behaviour
        when(request.getURI()).thenReturn("http://domain/contextPath");
        assertEquals("/contextPath", CookieTokenStore.getContextPath(httpFacade));
        // Trailing slashes should be removed
        when(request.getURI()).thenReturn("http://domain/contextPath/");
        assertEquals("/contextPath", CookieTokenStore.getContextPath(httpFacade));
        // Trailing paths should be removed
        when(request.getURI()).thenReturn("http://domain/contextPath/path");
        assertEquals("/contextPath", CookieTokenStore.getContextPath(httpFacade));
    }

    @Test
    public void testGetCookiePath() {
        when(request.getURI()).thenReturn("http://domain/contextPath/path");
        // When an absolute path is given as the cookie path, then that path should be used
        when(deployment.getAdapterStateCookiePath()).thenReturn("/absolute");
        assertEquals("/absolute", CookieTokenStore.getCookiePath(deployment, httpFacade));
        // When a relative path is given, it is taken to be relative to the context path
        when(deployment.getAdapterStateCookiePath()).thenReturn("relative");
        assertEquals("/contextPath/relative", CookieTokenStore.getCookiePath(deployment, httpFacade));
        // The empty string is a special case of a relative path
        // XXX: No slashes should be added, because the context path is not necessarily prefix (see
        // XXX: also the XXX comments in testGetContextPath)
        when(deployment.getAdapterStateCookiePath()).thenReturn("");
        assertEquals("/contextPath", CookieTokenStore.getCookiePath(deployment, httpFacade));
    }

}
