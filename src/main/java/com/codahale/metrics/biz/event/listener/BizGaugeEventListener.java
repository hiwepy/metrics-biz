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

import java.util.Queue;
import java.util.SortedMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizEventPoint;
import com.codahale.metrics.biz.event.BizGaugeEvent;
import com.codahale.metrics.biz.filter.MetricNamedFilter;

/**
 * Spring listener that buffers {@link BizGaugeEvent} samples into a
 * bounded queue and registers a single
 * {@link com.codahale.metrics.Gauge} per logical metric name.
 *
 * <p>On the first event for a given name, a queue-draining gauge is
 * registered against the metric registry. Subsequent events append to
 * the same queue and {@link Gauge#getValue()} returns the head element
 * (and removes it) once more than one sample has been buffered.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Component
public class BizGaugeEventListener extends BizMetricEventListener<BizGaugeEvent> {

    /**
     * Bounded queue that stores gauge samples between events.
     */
    protected Queue<Long> queue = new LinkedBlockingDeque<Long>();

    /**
     * Single-name filter reused across dispatches to look up an existing
     * gauge without scanning unrelated metrics.
     */
    protected MetricNamedFilter filter = new MetricNamedFilter();

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
     * Resolves the target {@link MetricRegistry}. When an injected
     * {@link MetricsFactory} is available its registry is preferred;
     * otherwise the listener falls back to the shared meter registry.
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
     * Buffers the sample from the event and, if no gauge is currently
     * registered for the logical metric name, registers a queue-draining
     * gauge against the metric registry.
     *
     * @param event the gauge business event to process.
     */
    @Async
    @Override
    @SuppressWarnings("rawtypes")
    public void onApplicationEvent(BizGaugeEvent event) {

        //fetch the bound payload
        BizEventPoint data = event.getBind();

        queue.add(data.getValue());

        String metrics_key = MetricRegistry.name(event.getSource().getClass(), data.getName());
        filter.setMetrics(metrics_key);
        SortedMap<String, Gauge> gauges = getMetricRegistry().getGauges(filter);
        boolean notRegister =  (gauges == null || gauges.isEmpty());
        if(notRegister){

            //instantiate a gauge that drains the queue head
            Gauge<Long> gauge = new Gauge<Long>() {

                @Override
                public Long getValue() {
                    if(queue.size() == 1){
                        return queue.peek();
                    } else{
                        return queue.poll();
                    }
                }

            };

            //register into the registry
            getMetricRegistry().register(metrics_key, gauge);

        }


    }

}