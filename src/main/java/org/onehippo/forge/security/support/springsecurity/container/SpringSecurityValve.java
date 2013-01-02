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
package org.onehippo.forge.security.support.springsecurity.container;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.container.ContainerConstants;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.Valve;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.security.TransientRole;
import org.hippoecm.hst.security.TransientUser;
import org.hippoecm.hst.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSecurityValve implements Valve {

  private static Logger log = LoggerFactory.getLogger(SpringSecurityValve.class);

  private boolean storeSubjectRepositoryCredentials = true;

  public boolean isStoreSubjectRepositoryCredentials() {
    return storeSubjectRepositoryCredentials;
  }

  public void setStoreSubjectRepositoryCredentials(boolean storeSubjectRepositoryCredentials) {
    this.storeSubjectRepositoryCredentials = storeSubjectRepositoryCredentials;
  }

  public void initialize() throws ContainerException {
  }

  public void destroy() {
  }

  public void invoke(ValveContext context) throws ContainerException {
    HttpServletRequest request = context.getServletRequest();
    Principal userPrincipal = request.getUserPrincipal();

    if (userPrincipal == null) {
      if (log.isDebugEnabled()) {
        log.debug("No user principal found. Skipping SpringSecurityValve...");
      }
      context.invokeNext();
      return;
    }

    HttpSession session = request.getSession(false);
    Subject subject = (session != null ? (Subject) session.getAttribute(ContainerConstants.SUBJECT_ATTR_NAME) : null);

    if (subject != null) {
      if (log.isDebugEnabled()) {
        log.debug("Already subject has been created somewhere before. Skipping SpringSecurityValve...");
      }
      context.invokeNext();
      return;
    }

    SecurityContext securityContext = SecurityContextHolder.getContext();

    if (securityContext == null) {
      if (log.isDebugEnabled()) {
        log.debug("Spring Security hasn't establish security context. Skipping SpringSecurityValve...");
      }
      context.invokeNext();
      return;
    }

    Authentication authentication = securityContext.getAuthentication();

    if (authentication == null) {
      if (log.isWarnEnabled()) {
        log.warn("Spring Security hasn't establish security context with authentication object. Skipping SpringSecurityValve...");
      }
      context.invokeNext();
      return;
    }

    Object springSecurityPrincipal = authentication.getPrincipal();

    if (!(springSecurityPrincipal instanceof UserDetails)) {
      if (log.isWarnEnabled()) {
        log.warn("Spring Security hasn't establish security context with UserDetails object. We don't support non UserDetails authentication. Skipping SpringSecurityValve...");
      }
      context.invokeNext();
      return;
    }

    UserDetails userDetails = (UserDetails) springSecurityPrincipal;

    User user = new TransientUser(userPrincipal.getName());

    Set<Principal> principals = new HashSet<Principal>();
    principals.add(userPrincipal);
    principals.add(user);

    for (GrantedAuthority authority : userDetails.getAuthorities()) {
      String authorityName = authority.getAuthority();
      if (!StringUtils.isEmpty(authorityName)) {
        principals.add(new TransientRole(authorityName));
      }
    }

    Set<Object> pubCred = new HashSet<Object>();
    Set<Object> privCred = new HashSet<Object>();

    if (storeSubjectRepositoryCredentials) {
      Credentials subjectRepoCreds = new SimpleCredentials(userDetails.getUsername(), userDetails.getPassword().toCharArray());
      privCred.add(subjectRepoCreds);
    }

    subject = new Subject(true, principals, pubCred, privCred);
    request.getSession(true).setAttribute(ContainerConstants.SUBJECT_ATTR_NAME, subject);

    context.invokeNext();
  }
}
