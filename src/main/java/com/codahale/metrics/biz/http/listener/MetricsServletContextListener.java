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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * Dropwizard servlet {@link MetricsServlet.ContextListener} that
 * exposes the application-wide metric registry owned by
 * {@link MetricsFactory#getContextMetricRegistry()} and allows any
 * origin via {@code Access-Control-Allow-Origin: *}.
 *
 * <p>Wiring this listener into a {@code web.xml} file is sufficient to
 * publish the application metrics at the default {@code /metrics}
 * endpoint.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class MetricsServletContextListener extends MetricsServlet.ContextListener {

    /**
     * Returns the metric registry that the {@code MetricsServlet}
     * should serialise.
     *
     * @return the application-wide metric registry.
     */
    @Override
    protected MetricRegistry getMetricRegistry() {
        return MetricsFactory.getContextMetricRegistry();
    }

    /**
     * Returns the {@code Access-Control-Allow-Origin} value used by the
     * {@code MetricsServlet}; this implementation always returns
     * {@code "*"} so any browser may scrape the endpoint.
     *
     * @return the wildcard origin.
     */
    @Override
    protected String getAllowedOrigin() {
        return "*";
    }

}