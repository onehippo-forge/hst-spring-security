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

package org.onehippo.forge.security.support.springsecurity.authentication;

import org.onehippo.forge.security.support.springsecurity.utils.SpringSecurityUtils;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hippo Repository based UsernamePasswordAuthenticationFilter implementation.
 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 */
public class HippoUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        if ("".equals(request.getContextPath())) {
            return uri.endsWith(getFilterProcessesUrl());
        }

        SpringSecurityUtils springSecurityUtils = new SpringSecurityUtils();
        String requestPath = request.getContextPath() + getFilterProcessesUrl();

        if (springSecurityUtils.requestComesFromCms(request)) {
            requestPath= request.getContextPath() + "/" + springSecurityUtils.getCmsPreviewPrefix() +
                    getFilterProcessesUrl();
        }

        return uri.endsWith(requestPath);
    }
}
