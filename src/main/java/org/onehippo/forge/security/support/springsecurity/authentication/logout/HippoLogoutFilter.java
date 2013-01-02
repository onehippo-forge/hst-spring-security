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
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hippo Repository based LogoutFilter extension.
 *
 * @see org.springframework.security.web.authentication.logout.LogoutFilter
 *      <p/>
 *      This class has been overrides to allow the user to logout within the Channel Manager
 */
public class HippoLogoutFilter extends LogoutFilter {


  public HippoLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
    super(logoutSuccessHandler, handlers);
  }

  public HippoLogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
    super(logoutSuccessUrl, handlers);
  }

  @Override
  protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {

    String uri = request.getRequestURI();
    int pathParamIndex = uri.indexOf(';');

    if (pathParamIndex > 0) {
      // strip everything from the first semi-colon
      uri = uri.substring(0, pathParamIndex);
    }

    int queryParamIndex = uri.indexOf('?');

    if (queryParamIndex > 0) {
      // strip everything from the first question mark
      uri = uri.substring(0, queryParamIndex);
    }

    if ("".equals(request.getContextPath())) {
      return uri.endsWith(getFilterProcessesUrl());
    }

    SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();
    String requestPath = request.getServletPath();

    if (!StringUtils.contains(requestPath, getFilterProcessesUrl())) {
      return false;
    }


    if (springSecurityUtils.requestComesFromCms(request)) {
      requestPath = request.getServletPath() + "/" + springSecurityUtils.getCmsPreviewPrefix();
    }

    return uri.endsWith(requestPath);
  }
}
