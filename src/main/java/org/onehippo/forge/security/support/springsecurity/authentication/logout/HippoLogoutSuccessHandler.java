/*
 *  Copyright 2011 Hippo.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.onehippo.forge.security.support.springsecurity.authentication.logout;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.security.support.springsecurity.utils.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Hippo Repository based LogoutSuccessHandler implementation.
 *
 * @see org.springframework.security.web.authentication.logout.LogoutSuccessHandler
 */
public class HippoLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

  protected final Logger logger = LoggerFactory.getLogger(getClass());


  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    String targetUrl = determineTargetUrl(request, response);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();

    // Don't put a slash at the end of _cmsinternal.
    if (springSecurityUtils.requestComesFromCms(request) && StringUtils.equals(targetUrl, "/")) {
      targetUrl = "";
    }

    response.sendRedirect(springSecurityUtils.buildRedirectUrl(targetUrl, request));
  }
}
