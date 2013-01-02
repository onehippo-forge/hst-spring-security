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
 *
 * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 *
 */
public class HippoSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private Logger logger = LoggerFactory.getLogger(HippoSimpleUrlAuthenticationFailureHandler.class);

  private String defaultFailureUrl;
  private boolean useReferer = false;
  private String indicatingFailedLogin;



  @Override
  public void setDefaultFailureUrl(String defaultFailureUrl) {
    this.defaultFailureUrl = defaultFailureUrl;
  }

  /**
   * If set to <tt>true</tt> the <tt>Referer</tt> header will be used (if available). Defaults to <tt>false</tt>.
   */
  public void setUseReferer(boolean useReferer) {
    this.useReferer = useReferer;
  }


  /**
   * @param indicatingFailedLogin the indication to set
   */
  public void setIndicatingFailedLogin(String indicatingFailedLogin) {
    this.indicatingFailedLogin = indicatingFailedLogin;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

    if (defaultFailureUrl == null) {
      logger.debug("No failure URL set, sending 401 Unauthorized error");

      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
    } else {
      saveException(request, exception);

      SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();

      if (useReferer) {
        defaultFailureUrl = addParameterIndicatingFailedLogin(request.getHeader("Referer"));
        logger.info("Using Referer header: " + defaultFailureUrl);
      }


      // Use the DefaultSavedRequest URL
      String redirectUrl = springSecurityUtils.buildRedirectUrl(defaultFailureUrl, request);

      if (logger.isInfoEnabled()) {
        logger.info("Redirecting to the Url: " + redirectUrl);
      }

      getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
  }

  private String addParameterIndicatingFailedLogin(String referer) {
    return referer + indicatingFailedLogin;
  }
}
