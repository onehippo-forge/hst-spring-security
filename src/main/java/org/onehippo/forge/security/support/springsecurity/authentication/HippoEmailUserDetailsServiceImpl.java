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

/**
 * Hippo Repository based UserDetailsService implementation.
 *
 * @see org.springframework.security.core.userdetails.UserDetailsService
 *      <p/>
 *      This implementation allows to use an email as username.
 */
public class HippoEmailUserDetailsServiceImpl extends HippoUserDetailsServiceImpl {

  private static final String QUERY_USER_EXISTS = "//hippo:configuration/hippo:users/*[ @hipposys:email = ''{0}'']";


  @Override
  public String getUserQuery() {
    return QUERY_USER_EXISTS;
  }
}
