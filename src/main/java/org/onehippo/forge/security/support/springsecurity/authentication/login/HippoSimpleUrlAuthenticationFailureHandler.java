package org.onehippo.forge.security.support.springsecurity.authentication.login;

import org.onehippo.forge.security.support.springsecurity.utils.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Hippo Repository based SimpleUrlAuthenticationFailureHandler extension.
 * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 *
 */
public class HippoSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(HippoSimpleUrlAuthenticationFailureHandler.class);

    private String defaultFailureUrl;

    @Override
    public void setDefaultFailureUrl(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if (defaultFailureUrl == null) {
            logger.debug("No failure URL set, sending 401 Unauthorized error");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        } else {
            saveException(request, exception);

            // Use the DefaultSavedRequest URL
            SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();
            String redirectUrl = springSecurityUtils.buildRedirectUrl(defaultFailureUrl, request);

            if (logger.isInfoEnabled()) {
                logger.info("Redirecting to defaultFailure Url: " + redirectUrl);
            }

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
