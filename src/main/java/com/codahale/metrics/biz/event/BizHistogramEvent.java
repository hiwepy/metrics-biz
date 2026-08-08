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
package com.codahale.metrics.biz.event;

/**
 * Spring {@link org.springframework.context.ApplicationEvent} that
 * represents a single sample submitted to a
 * {@link com.codahale.metrics.Histogram}.
 *
 * <p>Consumed by
 * {@link com.codahale.metrics.biz.event.listener.BizHistogramEventListener}
 * which forwards the sample to the underlying histogram via
 * {@code Histogram#update(long)}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see BizEvent
 * @see BizEventPoint
 */
@SuppressWarnings("serial")
public class BizHistogramEvent extends BizEvent<BizEventPoint> {

    /**
     * Creates a histogram event carrying an explicit {@link BizEventPoint}.
     *
     * @param source the originating publisher bean.
     * @param bind the event-point payload.
     */
    public BizHistogramEvent(Object source, BizEventPoint bind) {
        super(source, bind);
    }

    /**
     * Creates a histogram event with a name and value.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     * @param value the histogram sample value.
     */
    public BizHistogramEvent(Object source, String name, Long value) {
        super(source, new BizEventPoint(name, null, value));
    }

    /**
     * Creates a histogram event with a name, message and value.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     * @param message a human-readable message.
     * @param value the histogram sample value.
     */
    public BizHistogramEvent(Object source, String name, String message, Long value) {
        super(source, new BizEventPoint(name, message, value));
    }

}