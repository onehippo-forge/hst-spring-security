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

package org.onehippo.forge.security.support.springsecurity.authentication.rememberme;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 * Hippo Repository based TokenBasedRememberMeServices extension.
 * @see org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
 */
public class HippoTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

    @Override
    protected String retrievePassword(Authentication authentication) {
        UserDetails userDetails = super.getUserDetailsService().loadUserByUsername(authentication.getName());

        return userDetails.getPassword();
    }
}
