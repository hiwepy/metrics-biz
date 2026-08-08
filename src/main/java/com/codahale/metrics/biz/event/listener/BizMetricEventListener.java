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

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.codahale.metrics.biz.MetricsFactory;

/**
 * Abstract base class for Spring {@link ApplicationListener} beans that
 * translate business events into Dropwizard metric updates.
 *
 * <p>Subclasses resolve a {@link com.codahale.metrics.MetricRegistry} in
 * {@link #afterPropertiesSet()} (typically preferring the registry bound
 * to an injected {@link MetricsFactory} over a shared one), then
 * implement {@link #onApplicationEvent(ApplicationEvent)} to publish
 * counters, meters, histograms or gauges.</p>
 *
 * <p>This base class also exposes the scheduling parameters
 * {@link #initialDelay}, {@link #period} and {@link #unit} so subclasses
 * that need a periodic refresh can configure them declaratively.</p>
 *
 * @param <E> the application event type consumed by this listener.
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public abstract class BizMetricEventListener<E extends ApplicationEvent> implements ApplicationListener<E>, InitializingBean {

    /**
     * Optional {@link MetricsFactory} injected by Spring; when non-null
     * subclasses use the factory's bound registry instead of a shared one.
     */
    @Resource
    protected MetricsFactory metricsFactory;

    /**
     * Initial delay before the periodic refresh task runs, in
     * {@link #unit}. Defaults to zero so the first refresh happens
     * immediately.
     */
    protected long initialDelay = 0;

    /**
     * Period between refreshes, in {@link #unit}. Defaults to one.
     */
    protected long period = 1;

    /**
     * Time unit used for {@link #initialDelay} and {@link #period}.
     * Defaults to seconds.
     */
    protected TimeUnit unit = TimeUnit.SECONDS;

    /**
     * Default {@link InitializingBean} hook. Subclasses override this to
     * resolve their target {@link com.codahale.metrics.MetricRegistry}.
     *
     * @throws Exception propagated from subclass implementations.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * Returns the injected {@link MetricsFactory}, which may be
     * {@code null} when Spring was unable to resolve it.
     *
     * @return the bound metrics factory, or {@code null}.
     */
    public MetricsFactory getMetricsFactory() {
        return metricsFactory;
    }

    /**
     * Replaces the injected {@link MetricsFactory}. Mostly used in tests.
     *
     * @param metricsFactory the new factory; may be {@code null}.
     */
    public void setMetricsFactory(MetricsFactory metricsFactory) {
        this.metricsFactory = metricsFactory;
    }

    /**
     * Returns the initial delay before the periodic refresh task.
     *
     * @return the initial delay in {@link #unit}.
     */
    public long getInitialDelay() {
        return initialDelay;
    }

    /**
     * Replaces the initial delay before the periodic refresh task.
     *
     * @param initialDelay the new initial delay in {@link #unit}.
     */
    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    /**
     * Returns the period between refreshes.
     *
     * @return the period in {@link #unit}.
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Replaces the period between refreshes.
     *
     * @param period the new period in {@link #unit}.
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * Returns the time unit used for scheduling parameters.
     *
     * @return the active {@link TimeUnit}.
     */
    public TimeUnit getUnit() {
        return unit;
    }

    /**
     * Replaces the time unit used for scheduling parameters.
     *
     * @param unit the new time unit.
     */
    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

}