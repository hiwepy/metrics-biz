/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codahale.metrics.biz.http.listener;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

/**
 * Servlet listener that marks a Dropwizard meter for every incoming
 * HTTP request.
 *
 * <p>Only requests that implement {@link HttpServletRequest} are
 * recorded; any other servlet request triggers an
 * {@link IllegalArgumentException}. The metric is keyed by the
 * listener class and the request URI (with the servlet context path
 * stripped) so that downstream consumers can plot per-URI rates.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class HttpServletRequestMetricsListener implements ServletRequestListener,ServletRequestAttributeListener {

    /**
     * Metric registry used by this listener; resolved from
     * {@link MetricsFactory#getMetricRegistry(String)} with the logical
     * name {@code "http-request"}.
     */
    protected MetricRegistry registry = MetricsFactory.getMetricRegistry("http-request");


    /**
     * Marks a meter for the incoming request URI (with the context path
     * stripped).
     *
     * @param requestEvent the servlet request event.
     * @throws IllegalArgumentException when the request is not an
     *         {@link HttpServletRequest}.
     */
    @Override
    public void requestInitialized(ServletRequestEvent requestEvent) {

        if (!(requestEvent.getServletRequest() instanceof HttpServletRequest)) {
            throw new IllegalArgumentException( "Request is not an HttpServletRequest: " + requestEvent.getServletRequest());
        }
        HttpServletRequest oRequest = (HttpServletRequest) requestEvent.getServletRequest();

        //strip the web context from the URI
        String uri = oRequest.getRequestURI().substring(oRequest.getContextPath().length());
        String prefix = MetricRegistry.name(this.getClass(), uri );
        /*
         * mark a meter
         */
        registry.meter(prefix).mark();

    }

    /**
     * Placeholder hook for {@link ServletRequestAttributeListener}; the
     * metrics-biz module does not currently track per-request attribute
     * mutations.
     *
     * @param srae the servlet request attribute event.
     */
    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        // TODO Auto-generated method stub

    }

    /**
     * Placeholder hook for {@link ServletRequestAttributeListener}; the
     * metrics-biz module does not currently track per-request attribute
     * mutations.
     *
     * @param srae the servlet request attribute event.
     */
    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        // TODO Auto-generated method stub

    }

    /**
     * Placeholder hook for {@link ServletRequestAttributeListener}; the
     * metrics-biz module does not currently track per-request attribute
     * mutations.
     *
     * @param srae the servlet request attribute event.
     */
    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        // TODO Auto-generated method stub

    }

    /**
     * No-op hook for request destruction. Kept to satisfy the
     * {@link ServletRequestListener} interface.
     *
     * @param event the servlet request event.
     */
    @Override
    public void requestDestroyed(ServletRequestEvent event) {

    }

}