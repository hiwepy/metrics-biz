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

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

/**
 * Servlet listener that marks Dropwizard meters for HTTP session
 * passivation and activation events.
 *
 * <p>The metrics are keyed by the listener class, the servlet context
 * path and the {@code willPassivate} / {@code didActivate} dimension so
 * consumers can plot per-application passivation rates.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class HttpSessionActivationMetricsListener implements HttpSessionActivationListener {

    /**
     * Metric registry used by this listener; resolved from
     * {@link MetricsFactory#getMetricRegistry(String)} with the logical
     * name {@code "http-session-activation"}.
     */
    protected MetricRegistry registry = MetricsFactory.getMetricRegistry("http-session-activation");

    /**
     * Marks the {@code willPassivate} meter for the current context
     * path.
     *
     * @param event the HTTP session event.
     */
    @Override
    public void sessionWillPassivate(HttpSessionEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getSession().getServletContext().getContextPath(), "session", "willPassivate" );
        registry.meter(prefix).mark();

    }

    /**
     * Marks the {@code didActivate} meter for the current context path.
     *
     * @param event the HTTP session event.
     */
    @Override
    public void sessionDidActivate(HttpSessionEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getSession().getServletContext().getContextPath(), "session", "didActivate" );
        registry.meter(prefix).mark();

    }

}