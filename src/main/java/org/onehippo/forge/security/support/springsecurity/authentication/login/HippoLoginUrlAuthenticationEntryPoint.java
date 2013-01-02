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
package org.onehippo.forge.security.support.springsecurity.authentication.login;

import org.onehippo.forge.security.support.springsecurity.utils.SpringSecurityUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hippo Repository based LoginUrlAuthenticationEntryPoint extension.
 *
 * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
 *      <p/>
 *      This class has been overrides to allow the user to login within the Channel Manager
 */
public class HippoLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  @Override
  protected String buildRedirectUrlToLoginPage(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    String loginForm = determineUrlToUseForThisRequest(request, response, authException);

    SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();
    return springSecurityUtils.buildRedirectUrl(loginForm, request);
  }

}
