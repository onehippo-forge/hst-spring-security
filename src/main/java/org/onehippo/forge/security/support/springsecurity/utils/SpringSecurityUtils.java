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

import javax.servlet.http.HttpServletRequest;

import com.sun.istack.internal.Nullable;

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

/**
 * Spring security helper class used to retrieve some information about the status of the request within Hippo.
 */
public class SpringSecurityUtils {

    private static final Logger log = LoggerFactory.getLogger(SpringSecurityUtils.class);


    @Nullable
    public String getCmsPreviewPrefix() {
        final VirtualHosts virtualHosts = getVirtualHosts();

        if (virtualHosts == null) {
            return null;
        }

        return virtualHosts.getCmsPreviewPrefix();
    }

    public boolean requestComesFromCms(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();

        String prefix = getCmsPreviewPrefix();

        return prefix != null && servletPath.contains(prefix);

    }

    public String buildRedirectUrl(final String redirectUrl, final HttpServletRequest request) {

        // The redirectUrl is already fully qualified. No need to create the URL
        if (StringUtils.startsWith(redirectUrl, "http")) {
            return redirectUrl;
        }

        final ResolvedMount resolvedMount = getResolvedMount(request);

        final VirtualHosts virtualHosts = getVirtualHosts();

        final boolean isPreview = requestComesFromCms(request);

        final String baseURL = resolvedMount.getMount().getVirtualHost().getBaseURL(request);

        final StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(baseURL);

        if (!StringUtils.contains(redirectUrl, request.getContextPath())) {
            if (resolvedMount.getMount().getVirtualHost().isContextPathInUrl() || isPreview) {
                urlBuilder.append(request.getContextPath());
            }

            urlBuilder.append(resolvedMount.getResolvedMountPath());

            if (isPreview) {
                urlBuilder.append("/");
                urlBuilder.append(virtualHosts.getCmsPreviewPrefix());
            }
        }

        urlBuilder.append(redirectUrl);

        if (log.isInfoEnabled()) {
            log.info("url redirect = " + urlBuilder.toString());
        }

        return urlBuilder.toString();
    }


    private ResolvedMount getResolvedMount(final HttpServletRequest req) {
        final HstManager hstSitesManager = (HstManager) getBean(HstManager.class.getName());

        final String hostName = HstRequestUtils.getFarthestRequestHost(req);

        try {

            String servletPath = req.getServletPath();

            if (servletPath == null) {
                servletPath = "/";
            }

            // Check if the

            if (hstSitesManager != null && StringUtils.isNotEmpty(servletPath)) {
                return hstSitesManager.getVirtualHosts().matchMount(hostName, req.getContextPath(), servletPath);
            }
        } catch (final RepositoryNotAvailableException e) {
            log.error("Unable to identify the resolved mount.", e);
        }

        return null;
    }


    /**
     * @return the list of virtual hosts.
     */
    private
    @Nullable
    VirtualHosts getVirtualHosts() {
        final HstManager hstSitesManager = (HstManager) getBean(HstManager.class.getName());

        if (hstSitesManager != null) {
            try {
                return hstSitesManager.getVirtualHosts();
            } catch (final RepositoryNotAvailableException e) {
                return null;
            }
        }

        return null;
    }

    private Object getBean(final String name) {

        // Check if the user can access to this host
        final ComponentManager componentManager = HstServices.getComponentManager();

        if (componentManager == null) {
            log.error("Component Manager is null!!!!!! WE HAVE A BIG ISSUE");

            return null;
        }

        return componentManager.getComponent(name);
    }

}
