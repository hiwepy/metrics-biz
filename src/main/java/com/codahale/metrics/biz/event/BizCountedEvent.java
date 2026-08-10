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
 * represents the occurrence of a business-level counter increment.
 *
 * <p>{@link com.codahale.metrics.biz.event.listener.BizCountedEventListener}
 * consumes this event and translates it into a
 * {@link com.codahale.metrics.Counter#inc()} call. The constructor
 * variants let publishers pick the level of detail they want to
 * associate with the event &mdash; from a bare name up to a fully
 * populated event-point with a custom data map.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see BizEvent
 * @see BizEventPoint
 */
@SuppressWarnings("serial")
public class BizCountedEvent extends BizEvent<BizEventPoint> {

    /**
     * Creates a counted event carrying an explicit {@link BizEventPoint}.
     *
     * @param source the originating publisher bean.
     * @param bind the event-point payload; must not be {@code null}.
     */
    public BizCountedEvent(Object source, BizEventPoint bind) {
        super(source, bind);
    }

    /**
     * Creates a counted event with just a name.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     */
    public BizCountedEvent(Object source, String name) {
        super(source, new BizEventPoint(name, null));
    }

    /**
     * Creates a counted event with a name and human-readable message.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     * @param message a human-readable description.
     */
    public BizCountedEvent(Object source, String name, String message) {
        super(source, new BizEventPoint(name, message));
    }

    /**
     * Creates a counted event with a name, message and structured payload.
     *
     * @param source the originating publisher bean.
     * @param name the metric name.
     * @param message a human-readable description.
     * @param data arbitrary contextual data attached to the event-point.
     */
    public BizCountedEvent(Object source, String name, String message, Map<String, Object> data) {
        super(source, new BizEventPoint(name, message, data));
    }

}