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
package com.codahale.metrics.biz.event.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizEventPoint;
import com.codahale.metrics.biz.event.BizMeterEvent;

/**
 * Spring listener that converts {@link BizMeterEvent}s into
 * {@link com.codahale.metrics.Meter#mark()} calls on the bound metric
 * registry.
 *
 * <p>The target registry is resolved in {@link #afterPropertiesSet()}.
 * When no {@link MetricsFactory} is injected the listener falls back to
 * the shared meter registry.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Component
public class BizMeterEventListener extends BizMetricEventListener<BizMeterEvent> {

    /**
     * The metric registry this listener publishes into; populated in
     * {@link #afterPropertiesSet()}.
     */
    protected MetricRegistry metricRegistry;

    /**
     * Returns the metric registry this listener publishes into.
     *
     * @return the active registry; never {@code null} after
     *         {@link #afterPropertiesSet()} has run.
     */
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    /**
     * Resolves the target {@link MetricRegistry}.
     *
     * @throws Exception propagated from subclass implementations.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if(getMetricsFactory() != null){
            metricRegistry = getMetricsFactory().getRegistry();
        } else {
            metricRegistry = MetricsFactory.getMeterMetricRegistry();
        }
    }

    /**
     * Marks the meter identified by
     * {@code source-class + event-name} once per event.
     *
     * @param event the meter business event to process.
     */
    @Async
    @Override
    public void onApplicationEvent(BizMeterEvent event) {

        //fetch the bound payload
        BizEventPoint data = event.getBind();
        //compose the unique metric name from the publisher class and the event name
        String name = MetricRegistry.name(event.getSource().getClass(), data.getName());
        //mark the meter once
        getMetricRegistry().meter(name).mark();

    }


}