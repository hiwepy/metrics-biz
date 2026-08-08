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
package com.codahale.metrics.biz.filter;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

/**
 * Dropwizard {@link MetricFilter} that accepts metrics whose name is
 * exactly equal to the configured value.
 *
 * <p>The filter is stateful but designed to be reused across multiple
 * lookups: callers set {@link #setMetrics(String)} before invoking
 * {@link com.codahale.metrics.MetricRegistry#getGauges(MetricFilter)} or
 * similar APIs to obtain a sub-map containing only the matching name.
 * Equality is performed via {@link String#equals(Object)}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class MetricNamedFilter implements MetricFilter {

    /**
     * The metric name this filter matches; {@code null} means no metric
     * name matches.
     */
    protected String metrics;

    /**
     * Tests whether the supplied metric name equals the configured
     * filter name.
     *
     * @param name the metric name being tested.
     * @param metric the metric being tested; ignored by this filter.
     * @return {@code true} when {@code name.equals(getMetrics())}.
     */
    @Override
    public boolean matches(String name, Metric metric) {
        return name.equals(getMetrics());
    }

    /**
     * Returns the metric name this filter currently matches.
     *
     * @return the configured metric name; may be {@code null}.
     */
    public String getMetrics() {
        return metrics;
    }

    /**
     * Replaces the metric name this filter matches. Callers must invoke
     * this setter before calling
     * {@link com.codahale.metrics.MetricRegistry#getGauges(MetricFilter)}.
     *
     * @param metrics the new metric name; may be {@code null}.
     */
    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

}