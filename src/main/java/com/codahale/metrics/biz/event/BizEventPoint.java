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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.codahale.metrics.biz.utils.SystemClock;

/**
 * Immutable-by-convention payload that carries the data of a single
 * business-level metric event.
 *
 * <p>The class models a linked-list style "previous event" pointer, a
 * unique identifier, the metric name, the timestamp at which the event
 * occurred, a human-readable message, an optional numeric value and a
 * map of arbitrary key-value data. The convenience constructors accept
 * the most common combinations and assign a timestamp via
 * {@link SystemClock#now()}.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public class BizEventPoint {

    /**
     * Sentinel root point used as the initial previous-event pointer.
     */
    public static final BizEventPoint ROOT = new BizEventPoint("root" , "Event Source");

    /**
     * The previous event-point in the chain, or {@code null} for the root.
     */
    protected BizEventPoint prev;
    /**
     * The unique identifier assigned to this event-point.
     */
    protected String uid;
    /**
     * The metric name this event-point describes.
     */
    protected String name;
    /**
     * The timestamp at which the event occurred.
     */
    protected long timestamp;
    /**
     * The human-readable message attached to this event-point.
     */
    protected String message;
    /**
     * Optional numeric value associated with the event.
     */
    protected Long value;
    /**
     * Arbitrary contextual data attached to the event-point.
     */
    protected Map<String, Object> data;

    /**
     * Creates an event-point with no previous pointer and no value.
     *
     * @param name the metric name.
     * @param message a human-readable message.
     */
    public BizEventPoint(String name, String message) {
        this(null, name, message);
    }

    /**
     * Creates an event-point with no previous pointer but carrying a
     * numeric value.
     *
     * @param name the metric name.
     * @param message a human-readable message.
     * @param value the numeric value to attach.
     */
    public BizEventPoint(String name, String message, Long value) {
        this(null, name, message);
        this.value = value;
    }

    /**
     * Creates an event-point with an explicit previous pointer.
     *
     * @param prev the previous event-point; may be {@code null}.
     * @param name the metric name.
     * @param message a human-readable message.
     */
    public BizEventPoint(BizEventPoint prev, String name, String message) {
        this(prev, name, SystemClock.now(), message);
    }

    /**
     * Creates an event-point with an explicit timestamp but no previous
     * pointer.
     *
     * @param name the metric name.
     * @param timestamp the timestamp at which the event occurred.
     * @param message a human-readable message.
     */
    public BizEventPoint(String name, long timestamp, String message) {
        this(null, name, timestamp, message);
    }

    /**
     * Creates an event-point with an explicit previous pointer and
     * timestamp.
     *
     * @param prev the previous event-point; may be {@code null}.
     * @param name the metric name.
     * @param timestamp the timestamp at which the event occurred.
     * @param message a human-readable message.
     */
    public BizEventPoint(BizEventPoint prev, String name, long timestamp, String message) {
        this(prev, name, timestamp, message, null);
    }

    /**
     * Creates an event-point with an explicit data map but no previous
     * pointer.
     *
     * @param name the metric name.
     * @param message a human-readable message.
     * @param data the data map to attach; never {@code null}.
     */
    public BizEventPoint(String name, String message, Map<String, Object> data) {
        this(null, name, message, data);
    }

    /**
     * Creates an event-point with an explicit previous pointer and data
     * map.
     *
     * @param prev the previous event-point; may be {@code null}.
     * @param name the metric name.
     * @param message a human-readable message.
     * @param data the data map to attach; never {@code null}.
     */
    public BizEventPoint(BizEventPoint prev, String name, String message, Map<String, Object> data) {
        this(prev, name, SystemClock.now(), message, data);
    }

    /**
     * Creates an event-point with an explicit timestamp and data map but
     * no previous pointer.
     *
     * @param name the metric name.
     * @param timestamp the timestamp at which the event occurred.
     * @param message a human-readable message.
     * @param data the data map to attach; never {@code null}.
     */
    public BizEventPoint(String name, long timestamp, String message, Map<String, Object> data) {
        this(null, name, SystemClock.now(), message, data);
    }

    /**
     * Fully-specified constructor.
     *
     * @param prev the previous event-point; may be {@code null}.
     * @param name the metric name.
     * @param timestamp the timestamp at which the event occurred.
     * @param message a human-readable message.
     * @param data the data map to attach; if {@code null} an empty map is
     *             substituted.
     */
    public BizEventPoint(BizEventPoint prev, String name, long timestamp, String message, Map<String, Object> data) {
        this.prev = prev;
        this.name = name;
        this.uid = UUID.randomUUID().toString();
        this.timestamp = timestamp;
        this.message = message;
        this.data = data == null ? new HashMap<String, Object>() : data;
    }

    /**
     * Returns the previous event-point in the chain.
     *
     * @return the previous event-point, or {@code null} for the root.
     */
    public BizEventPoint getPrev() {
        return prev;
    }

    /**
     * Replaces the previous event-point.
     *
     * @param prev the new previous event-point; may be {@code null}.
     */
    public void setPrev(BizEventPoint prev) {
        this.prev = prev;
    }

    /**
     * Returns the unique identifier of this event-point.
     *
     * @return the unique identifier; never {@code null}.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Replaces the unique identifier. Mainly intended for deserialisation.
     *
     * @param uid the new unique identifier.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Returns the metric name this event-point describes.
     *
     * @return the metric name; may be {@code null}.
     */
    public String getName() {
        return name;
    }

    /**
     * Replaces the metric name.
     *
     * @param name the new metric name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the timestamp at which the event occurred.
     *
     * @return the event timestamp in milliseconds since the epoch.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Replaces the timestamp at which the event occurred.
     *
     * @param timestamp the new timestamp in milliseconds since the epoch.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the human-readable message attached to this event-point.
     *
     * @return the message; may be {@code null}.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Replaces the human-readable message.
     *
     * @param message the new message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the optional numeric value attached to this event-point.
     *
     * @return the value, or {@code null} if none was provided.
     */
    public Long getValue() {
        return value;
    }

    /**
     * Replaces the optional numeric value.
     *
     * @param value the new numeric value.
     */
    public void setValue(Long value) {
        this.value = value;
    }

    /**
     * Returns the data map attached to this event-point. The map is
     * always non-{@code null}; constructors substitute an empty map when
     * the caller passes {@code null}.
     *
     * @return the data map; never {@code null}.
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Replaces the data map attached to this event-point.
     *
     * @param data the new data map; may be {@code null} in which case
     *             subsequent {@link #put(String, Object)} calls would
     *             trigger a {@link NullPointerException}.
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Stores a single entry in the {@link #getData()} map.
     *
     * @param key the entry key.
     * @param value the entry value.
     */
    public void put(String key, Object value) {
        getData().put(key, value);
    }

}