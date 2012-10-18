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

package org.onehippo.forge.security.support.springsecurity.utils;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.hosting.VirtualHosts;
import org.hippoecm.hst.configuration.model.HstManager;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.core.container.RepositoryNotAvailableException;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.hst.util.HstRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring security helper class used to retrieve some information about the status of the request within Hippo.
 */
public class SpringSecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(SpringSecurityUtils.class);


    public String getCmsPreviewPrefix() {
        VirtualHosts virtualHosts = getVirtualHosts();
        return virtualHosts.getCmsPreviewPrefix();
    }

    public boolean requestComesFromCms(HttpServletRequest request) {
        final String servletPath = request.getServletPath();

        return servletPath.contains(getCmsPreviewPrefix());
    }


    /**
     * Spring Security redirects all requests when it is used. This create an issue within the Channel Manager in production.
     * When tomcat is placed behind apache, the hostname received by Spring Security is equals to 127.0.0.1.
     * Also, the CMS prefix is not added to the request when the user accesses the site within the Channel Manager.
     *
     * This method is used to recreate the URL.

     * @param redirectUrl the redirect URL.
     * @param request the hst Request
     * @return the new redirect URL
     */
    public String buildRedirectUrl(String redirectUrl, HttpServletRequest request) {
        final ResolvedMount resolvedMount = getResolvedMount(request);

        VirtualHosts virtualHosts = getVirtualHosts();

        boolean isPreview = requestComesFromCms(request);

        String baseURL = resolvedMount.getMount().getVirtualHost().getBaseURL(request);

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(baseURL);

        if (resolvedMount.getMount().getVirtualHost().isContextPathInUrl() || isPreview) {
            urlBuilder.append(request.getContextPath());
        }

        if (isPreview) {
            urlBuilder.append("/");
            urlBuilder.append(virtualHosts.getCmsPreviewPrefix());
        }

        urlBuilder.append(redirectUrl);

        if (log.isInfoEnabled()) {
            log.info("url redirect = " + urlBuilder.toString());
        }

        return urlBuilder.toString();
    }


    private ResolvedMount getResolvedMount(HttpServletRequest req) {
        HstManager hstSitesManager = getBean(HstManager.class.getName());

        String hostName = HstRequestUtils.getFarthestRequestHost(req);

        try {

            String pathInfo = req.getPathInfo();

            if (req.getPathInfo() == null) {
                pathInfo = "/";
            }

            if ((hstSitesManager != null) && StringUtils.isNotEmpty(pathInfo)) {
                return hstSitesManager.getVirtualHosts().matchMount(hostName, req.getContextPath(), pathInfo);
            }
        } catch (RepositoryNotAvailableException e) {
            log.error("Unable to identify the resolved mount.", e);
        }

        return null;
    }


    /**
     * @return the list of virtual hosts.
     */
    private VirtualHosts getVirtualHosts() {
        HstManager hstSitesManager = getBean(HstManager.class.getName());

        if (hstSitesManager != null) {
            try {
                return hstSitesManager.getVirtualHosts();
            } catch (RepositoryNotAvailableException e) {
                return null;
            }
        }

        return null;
    }

    private <T> T getBean(String name) {

        // Check if the user can access to this host
        ComponentManager componentManager = HstServices.getComponentManager();

        if (componentManager == null) {
            log.error("Component Manager is null!!!!!! WE HAVE A BIG ISSUE");

            return null;
        }

        return componentManager.getComponent(name);
    }

}
