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

import java.util.Map;

/**
 * Spring {@link org.springframework.context.ApplicationEvent} that
 * represents the occurrence of a business-level meter "mark".
 *
 * <p>Consumed by
 * {@link com.codahale.metrics.biz.event.listener.BizMeterEventListener}
 * which translates the event into a
 * {@link com.codahale.metrics.Meter#mark()} call. The constructor
 * variants mirror those of {@link BizCountedEvent} so publishers can
 * attach arbitrary context data to the event.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BizEvent
 * @see BizEventPoint
 */
@SuppressWarnings("serial")
public class BizMeterEvent extends BizEvent<BizEventPoint> {

    /**
     * Creates a meter event carrying an explicit {@link BizEventPoint}.
     *
     * @param source the originating publisher bean.
     * @param bind the event-point payload.
     */
    public BizMeterEvent(Object source, BizEventPoint bind) {
        super(source, bind);
    }

    /**
     * Creates a meter event with just a name.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     */
    public BizMeterEvent(Object source, String name) {
        super(source, new BizEventPoint(name, null));
    }

    /**
     * Creates a meter event with a name and message.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     * @param message a human-readable message.
     */
    public BizMeterEvent(Object source, String name, String message) {
        super(source, new BizEventPoint(name, message));
    }

    /**
     * Creates a meter event with a name, message and structured payload.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     * @param message a human-readable message.
     * @param data arbitrary contextual data attached to the event-point.
     */
    public BizMeterEvent(Object source, String name, String message, Map<String, Object> data) {
        super(source, new BizEventPoint(name, message, data));
    }

}