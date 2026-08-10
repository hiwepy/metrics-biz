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

import org.springframework.context.ApplicationEvent;

/**
 * Base class for every business-level Spring {@link ApplicationEvent}
 * used by the metrics-biz module.
 *
 * <p>Spring's {@code ApplicationEvent} already carries the originating
 * {@code source}; this subclass adds a strongly-typed {@link #bind}
 * payload so that listeners can publish metrics without re-reading the
 * event source.</p>
 *
 * @param <T> the type of the bound payload.
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see ApplicationEvent
 */
@SuppressWarnings("serial")
public class BizEvent<T> extends ApplicationEvent {

    /**
     * The strongly-typed payload attached to this event.
     */
    protected T bind;

    /**
     * Creates a new business event.
     *
     * @param source the originating object (typically the publisher bean).
     * @param bind the strongly-typed payload; may be {@code null}.
     */
    public BizEvent(Object source, T bind) {
        super(source);
        this.bind = bind;
    }

    /**
     * Returns the strongly-typed payload attached to this event.
     *
     * @return the bound payload, possibly {@code null}.
     */
    public T getBind() {
        return bind;
    }

}