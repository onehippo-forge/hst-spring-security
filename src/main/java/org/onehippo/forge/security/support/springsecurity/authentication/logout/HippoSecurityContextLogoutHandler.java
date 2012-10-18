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

import org.onehippo.forge.security.support.springsecurity.utils.SpringSecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hippo Repository based SecurityContextLogoutHandler extension.
 * @see org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
 *
 * This class has been overrides to allow the user to logout within the Channel Manager
 * and to clear the remember me cookie.
 */
public class HippoSecurityContextLogoutHandler extends SecurityContextLogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // By default the cookie is not removed. So the user is still logged-in even if the user clicks on the logout link.
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookieItem : cookies) {
                if (cookieItem.getName().equals("SPRING_SECURITY_REMEMBER_ME_COOKIE")) {
                    cookieItem.setMaxAge(0);
                    response.addCookie(cookieItem);
                }
            }
        }

        SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();

        // When the user click on the logout link within the channel manager, only the security context should be clear.
        // The session should not be deleted.
        if (springSecurityUtils.requestComesFromCms(request)) {
            setInvalidateHttpSession(false);
            removeAttributeFromSession(request);

        }

        super.logout(request, response, authentication);
    }

    /**
     * Useful when the user is logged-out within the Channel Manager. In this case, the session is not invalidated and
     * the session's attributes must be removed manually.
     *
     * @param request http request
     */
    protected void removeAttributeFromSession(HttpServletRequest request) {
    }
}
