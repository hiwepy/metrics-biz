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
package com.codahale.metrics.biz.http.servlet;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

/**
 * Dropwizard servlet {@code InstrumentedFilterContextListener} that
 * exposes the application-wide metric registry owned by
 * {@link MetricsFactory#getContextMetricRegistry()}.
 *
 * <p>Wiring this listener into a {@code web.xml} file publishes the
 * metrics collected by the Dropwizard {@code InstrumentedFilter} under
 * the same registry as the rest of the metrics-biz components.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class InstrumentedFilterContextListener extends com.codahale.metrics.servlet.InstrumentedFilterContextListener {

    /**
     * Returns the metric registry that the
     * {@code InstrumentedFilter} should publish into.
     *
     * @return the application-wide metric registry.
     */
    @Override
    protected MetricRegistry getMetricRegistry() {
        return MetricsFactory.getContextMetricRegistry();
    }

}