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

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

/**
 * Servlet listener that marks Dropwizard meters for HTTP session value
 * bind and unbind events.
 *
 * <p>The metrics are keyed by the listener class, the servlet context
 * path and the {@code valueBound} / {@code valueUnbound} dimension so
 * consumers can plot per-application session value churn.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public class HttpSessionBindingMetricsListener implements HttpSessionBindingListener {

    /**
     * Metric registry used by this listener; resolved from
     * {@link MetricsFactory#getMetricRegistry(String)} with the logical
     * name {@code "http-session-binding"}.
     */
    protected MetricRegistry registry = MetricsFactory.getMetricRegistry("http-session-binding");

    /**
     * Marks the {@code valueBound} meter for the current context path.
     *
     * @param event the HTTP session binding event.
     */
    @Override
    public void valueBound(HttpSessionBindingEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getSession().getServletContext().getContextPath(), "session", "valueBound" );
        registry.meter(prefix).mark();

    }

    /**
     * Marks the {@code valueUnbound} meter for the current context path.
     *
     * @param event the HTTP session binding event.
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getSession().getServletContext().getContextPath(), "session", "valueUnbound" );
        registry.meter(prefix).mark();

    }

}