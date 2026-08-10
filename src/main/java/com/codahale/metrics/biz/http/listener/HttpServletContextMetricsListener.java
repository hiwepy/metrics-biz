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

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.biz.MetricsFactory;

/**
 * Servlet listener that publishes Dropwizard {@link com.codahale.metrics.Meter}s
 * for every servlet-context attribute mutation.
 *
 * <p>The listener binds the application-wide metric registry into the
 * {@link javax.servlet.ServletContext} under
 * {@link MetricsFactory#SERVLET_CONTEXT_METRIC_REGISTRY} so other
 * components can look it up by attribute name. Three meter dimensions
 * are tracked &mdash; {@code attributeAdded}, {@code attributeRemoved}
 * and {@code attributeReplaced} &mdash; each scoped by the context
 * path.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public class HttpServletContextMetricsListener implements ServletContextAttributeListener, ServletContextListener {

    /**
     * Metric registry used by this listener; populated in
     * {@link #contextInitialized(ServletContextEvent)} from
     * {@link MetricsFactory#getContextMetricRegistry()}.
     */
    protected MetricRegistry registry = null;

    /**
     * Binds the application-wide metric registry into the servlet
     * context under {@link MetricsFactory#SERVLET_CONTEXT_METRIC_REGISTRY}.
     *
     * @param event the servlet context initialisation event.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        this.registry = MetricsFactory.getContextMetricRegistry();
        event.getServletContext().setAttribute(MetricsFactory.SERVLET_CONTEXT_METRIC_REGISTRY, this.registry);
    }

    /**
     * Marks the {@code attributeAdded} meter for the current context path.
     *
     * @param event the servlet context attribute event.
     */
    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getServletContext().getContextPath(), "ServletContext", "attributeAdded" );
        registry.meter(prefix).mark();

    }

    /**
     * Marks the {@code attributeRemoved} meter for the current context
     * path.
     *
     * @param event the servlet context attribute event.
     */
    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getServletContext().getContextPath(), "ServletContext", "attributeRemoved" );
        registry.meter(prefix).mark();

    }

    /**
     * Marks the {@code attributeReplaced} meter for the current context
     * path.
     *
     * @param event the servlet context attribute event.
     */
    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {

        String prefix = MetricRegistry.name(this.getClass(), event.getServletContext().getContextPath(), "ServletContext", "attributeReplaced" );
        registry.meter(prefix).mark();

    }

    /**
     * Clears every shared metric registry. Intended to release static
     * state when the web application shuts down.
     *
     * @param sce the servlet context destruction event.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //clear the shared registries
        SharedMetricRegistries.clear();
    }

}