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
package com.codahale.metrics.biz;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistry.MetricSupplier;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Central factory that wires a Dropwizard {@link MetricRegistry} into a
 * Spring-managed lifecycle and exposes convenience accessors for the
 * standard metric types.
 *
 * <p>Instances are intended to be declared as a Spring bean; the
 * {@link InitializingBean#afterPropertiesSet()} hook starts a JMX reporter
 * against the bound registry and {@link DisposableBean#destroy()} stops it
 * again. To keep the per-bean registry decoupled from the application-wide
 * metrics, this class also exposes a set of static helpers that return
 * well-known shared registries (gauges, counters, histograms, meters,
 * timers) so callers can publish metrics without holding a bean reference.
 *
 * <p>The factory caches every metric it materialises in
 * {@link #COMPLIED_METRICS} so repeated lookups for the same logical name
 * return the same Dropwizard instance; this mirrors the contract
 * Dropwizard itself enforces inside a registry but gives callers a single
 * key space across the whole application.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see MetricRegistry
 * @see HealthCheckRegistry
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class MetricsFactory implements InitializingBean, DisposableBean {

    /**
     * Attribute name under which the servlet context-scoped metric registry
     * is exposed by {@link HttpServletContextMetricsListener}.
     */
    public static final String SERVLET_CONTEXT_METRIC_REGISTRY = ServletContext.class.getCanonicalName() +  ".registry";

    /**
     * Application-wide health-check registry shared by every
     * {@code HealthCheckServlet} listener.
     */
    protected static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();

    /**
     * Application-wide metric registry used as the default when no custom
     * registry has been injected via {@link #setRegistry(MetricRegistry)}.
     */
    protected static final MetricRegistry DEFAULT_REGISTRY = new MetricRegistry();

    /**
     * The registry this factory publishes metrics into. Replaceable via
     * {@link #setRegistry(MetricRegistry)} but defaults to
     * {@link #DEFAULT_REGISTRY}.
     */
    protected MetricRegistry registry = DEFAULT_REGISTRY;

    /**
     * The JMX reporter bound during {@link #afterPropertiesSet()}; kept so
     * {@link #destroy()} can stop it.
     */
    protected JmxReporter jmxReporter;

    /**
     * Cache of every metric this factory has materialised. Keyed by the
     * Dropwizard-style dotted name so multiple requests for the same logical
     * metric share a single underlying instance.
     */
    protected ConcurrentHashMap<String, Metric> COMPLIED_METRICS = new ConcurrentHashMap<String, Metric>();

    /**
     * Logger for this factory; emits diagnostic messages during startup
     * and shutdown.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(MetricsFactory.class);

    /**
     * Default constructor. Spring uses this when the bean is declared with
     * {@code <metrics:factory/>} or {@code @Bean}.
     */
    public MetricsFactory() {

    }

    /**
     * Builds the JMX reporter against the bound registry and starts it so
     * every subsequently registered metric is exposed via JMX.
     *
     * @throws Exception propagated from {@link JmxReporter#start()} if the
     *         platform MBean server is unavailable.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        jmxReporter = JmxReporter.forRegistry(registry).build();
        jmxReporter.start();
    }

    /**
     * Stops the JMX reporter that was started in
     * {@link #afterPropertiesSet()}.
     *
     * @throws Exception propagated from {@link JmxReporter#stop()}.
     */
    @Override
    public void destroy() throws Exception {
        jmxReporter.stop();
    }

    /**
     * Replaces the registry that this factory publishes into. Typically
     * called by Spring before {@link #afterPropertiesSet()}.
     *
     * @param registry the new target registry; must not be {@code null}.
     */
    public void setRegistry(MetricRegistry registry) {
        this.registry = registry;
    }

    /**
     * Returns the registry that this factory publishes into.
     *
     * @return the active metric registry; never {@code null}.
     */
    public MetricRegistry getRegistry() {
        return registry;
    }

    /**
     * Returns a {@link Timer} registered under the supplied names. The
     * resolved name is composed via {@link MetricRegistry#name(String...)}
     * and cached so repeated calls share the same instance.
     *
     * @param names the dotted segments that compose the metric name.
     * @return the cached or newly created {@link Timer}.
     */
    public Timer getTimer(String... names) {
        return getMetric(Timer.class, null, names);
    }

    /**
     * Returns a {@link Timer} registered under the supplied names, prefixed
     * with the supplied owning class so callers can avoid hard-coding it.
     *
     * @param clazz the owning class used as the metric prefix.
     * @param names additional dotted segments.
     * @return the cached or newly created {@link Timer}.
     */
    public Timer getTimer(Class<?> clazz, String... names) {
        return getMetric(Timer.class, clazz, names);
    }

    /**
     * Returns a {@link Histogram} registered under the supplied names.
     *
     * @param names the dotted segments that compose the metric name.
     * @return the cached or newly created {@link Histogram}.
     */
    public Histogram getHistogram(String... names) {
        return getMetric(Histogram.class, null, names);
    }

    /**
     * Returns a {@link Histogram} registered under the supplied names,
     * prefixed with the supplied owning class.
     *
     * @param clazz the owning class used as the metric prefix.
     * @param names additional dotted segments.
     * @return the cached or newly created {@link Histogram}.
     */
    public Histogram getHistogram(Class<?> clazz, String... names) {
        return getMetric(Histogram.class, clazz, names);
    }

    /**
     * Returns a {@link Counter} registered under the supplied names.
     *
     * @param names the dotted segments that compose the metric name.
     * @return the cached or newly created {@link Counter}.
     */
    public Counter getCounter(String... names) {
        return getMetric(Counter.class, null, names);
    }

    /**
     * Returns a {@link Counter} registered under the supplied names,
     * prefixed with the supplied owning class.
     *
     * @param clazz the owning class used as the metric prefix.
     * @param names additional dotted segments.
     * @return the cached or newly created {@link Counter}.
     */
    public Counter getCounter(Class<?> clazz, String... names) {
        return getMetric(Counter.class, clazz, names);
    }

    /**
     * Returns a {@link Meter} registered under the supplied names.
     *
     * @param names the dotted segments that compose the metric name.
     * @return the cached or newly created {@link Meter}.
     */
    public Meter getMeter(String... names) {
        return getMetric(Meter.class, null, names);
    }

    /**
     * Returns a {@link Meter} registered under the supplied names,
     * prefixed with the supplied owning class.
     *
     * @param clazz the owning class used as the metric prefix.
     * @param names additional dotted segments.
     * @return the cached or newly created {@link Meter}.
     */
    public Meter getMeter(Class<?> clazz, String... names) {
        return getMetric(Meter.class, clazz, names);
    }

    /**
     * Generic lookup helper that consults the local cache first and, on a
     * miss, materialises a fresh metric through the underlying registry.
     *
     * @param metricClass the Dropwizard metric type to create.
     * @param clazz optional owning class used as the name prefix; may be
     *              {@code null} for a bare name.
     * @param names additional dotted name segments.
     * @param <T> the metric type.
     * @return the cached or newly created metric instance.
     */
    @SuppressWarnings("unchecked")
    private <T> T getMetric(Class<T> metricClass, Class<?> clazz, String... names) {
        String prefix = (clazz == null ? "" : "." + clazz.getName());
        String key = MetricRegistry.name(metricClass.getName() + prefix , names);
        Metric ret = COMPLIED_METRICS.get(key);
        if (ret != null) {
            return (T) ret;
        }
        if (metricClass == Histogram.class) {
            ret = this.getRegistry().histogram(MetricRegistry.name(prefix, names));
        }
        if (metricClass == Timer.class) {
            ret = this.getRegistry().timer(MetricRegistry.name(prefix, names));
        }
        if (metricClass == Meter.class) {
            ret = this.getRegistry().meter(MetricRegistry.name(prefix, names));
        }
        if (metricClass == Counter.class) {
            ret = this.getRegistry().counter(MetricRegistry.name(prefix, names));
        }
        Metric existing = COMPLIED_METRICS.putIfAbsent(key, ret);
        if (existing != null) {
            ret = existing;
        }
        return (T) ret;
     }

    /**
     * Registers a {@link Gauge} backed by the supplied supplier. The gauge
     * name is composed from the owning class and the supplied name
     * segments; the result is cached so repeated calls return the same
     * Dropwizard gauge instance.
     *
     * @param supplier the Dropwizard supplier that produces the gauge.
     * @param clazz the owning class used as the name prefix.
     * @param names additional dotted name segments.
     * @param <T> the gauge value type.
     * @return the cached or newly registered {@link Gauge}.
     */
    public <T> Gauge<T> getGauge(MetricSupplier<Gauge> supplier, Class<?> clazz, String... names) {
        String key = MetricRegistry.name(clazz , names);
        Metric ret = COMPLIED_METRICS.get(key);
        if (ret != null) {
            return (Gauge<T>) ret;
        }
        ret = this.getRegistry().gauge(key, supplier);
        Metric existing = COMPLIED_METRICS.putIfAbsent(key, ret);
        if (existing != null) {
            ret = existing;
        }
        return (Gauge<T>) ret;
    }

    /**
     * Delegates to {@link MetricRegistry#register(String, Metric)} on the
     * bound registry.
     *
     * @param name the metric name.
     * @param metric the metric instance to register.
     * @param <T> the metric type.
     * @return the metric that the registry now holds.
     * @throws IllegalArgumentException if a different metric is already
     *         registered under {@code name}.
     */
     public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
         return this.getRegistry().register(name, metric);
     }

    /**
     * Delegates to {@link MetricRegistry#registerAll(MetricSet)} on the
     * bound registry.
     *
     * @param metrics the metric set to register.
     * @throws IllegalArgumentException if any name in the set collides.
     */
     public void registerAll(MetricSet metrics) throws IllegalArgumentException {
         this.getRegistry().registerAll(metrics);
     }

    /**
     * Removes the metric registered under {@code name} from the bound
     * registry.
     *
    * @param name the metric name to remove.
     * @return {@code true} if a metric was removed, {@code false} otherwise.
     */
     public boolean remove(String name) {
         return this.getRegistry().remove(name);
     }

    /**
     * Removes every metric accepted by {@code filter} from the bound
     * registry.
     *
     * @param filter the filter that selects metrics to remove.
     */
     public void removeMatching(MetricFilter filter) {
         this.getRegistry().removeMatching(filter);
     }

    /**
     * Returns the default application-wide metric registry. This is the
     * registry used by the static helpers in this class.
     *
     * @return the shared {@link MetricRegistry}; never {@code null}.
     */
    public static MetricRegistry getContextMetricRegistry() {
        return DEFAULT_REGISTRY;
    }

    /**
     * Returns the default application-wide health-check registry.
     *
     * @return the shared {@link HealthCheckRegistry}; never {@code null}.
     */
    public static HealthCheckRegistry getContextHealthCheckRegistry() {
        return HEALTH_CHECK_REGISTRY;
    }

    /**
     * Returns the shared registry that callers use to publish gauges.
     *
     * @return the shared {@code "Gauges"} registry; never {@code null}.
     */
    public static MetricRegistry getGaugeMetricRegistry() {
        return SharedMetricRegistries.getOrCreate("Gauges");
    }

    /**
     * Returns the shared registry that callers use to publish counters.
     *
     * @return the shared {@code "Counters"} registry; never {@code null}.
     */
    public static MetricRegistry getCounterMetricRegistry() {
        return SharedMetricRegistries.getOrCreate("Counters");
    }

    /**
     * Returns the shared registry that callers use to publish histograms.
     *
     * @return the shared {@code "Histograms"} registry; never {@code null}.
     */
    public static MetricRegistry getHistogramMetricRegistry() {
        return SharedMetricRegistries.getOrCreate("Histograms");
    }

    /**
     * Returns the shared registry that callers use to publish meters.
     *
     * @return the shared {@code "Meters"} registry; never {@code null}.
     */
    public static MetricRegistry getMeterMetricRegistry() {
        return SharedMetricRegistries.getOrCreate("Meters");
    }

    /**
     * Returns the shared registry that callers use to publish timers.
     *
     * @return the shared {@code "Timers"} registry; never {@code null}.
     */
    public static MetricRegistry getTimerMetricRegistry() {
        return SharedMetricRegistries.getOrCreate("Timers");
    }

    /**
     * Returns a named shared metric registry, creating it on first access.
     *
     * @param metrics the logical registry name; must not be {@code null}.
     * @return the shared registry for {@code metrics}; never {@code null}.
     */
    public static MetricRegistry getMetricRegistry(String metrics) {
        return SharedMetricRegistries.getOrCreate(metrics);
    }

    /**
     * Materialises a {@link Histogram} on the shared histogram registry
     * without holding a bean reference to this factory.
     *
     * @param clazz the owning class used as the name prefix.
     * @param names additional dotted name segments.
     * @param <T> the owning class type.
     * @return the new {@link Histogram}.
     */
    public static <T> Histogram histogram(Class<T> clazz, String... names) {
        return getHistogramMetricRegistry().histogram(MetricRegistry.name(clazz, names));
    }

    /**
     * Materialises a {@link Timer} on the shared timer registry without
     * holding a bean reference to this factory.
     *
     * @param clazz the owning class used as the name prefix.
     * @param names additional dotted name segments.
     * @param <T> the owning class type.
     * @return the new {@link Timer}.
     */
    public static <T> Timer timer(Class<T> clazz, String... names) {
        return getTimerMetricRegistry().timer(MetricRegistry.name(clazz, names));
    }

    /**
     * Materialises a {@link Counter} on the shared counter registry without
     * holding a bean reference to this factory.
     *
     * @param clazz the owning class used as the name prefix.
     * @param names additional dotted name segments.
     * @param <T> the owning class type.
     * @return the new {@link Counter}.
     */
    public static <T> Counter counter(Class<?> clazz, String... names) {
        return getCounterMetricRegistry().counter(MetricRegistry.name(clazz, names));
    }

    /**
     * Materialises a {@link Meter} on the shared meter registry without
     * holding a bean reference to this factory.
     *
     * @param clazz the owning class used as the name prefix.
     * @param names additional dotted name segments.
     * @param <T> the owning class type.
     * @return the new {@link Meter}.
     */
    public static <T> Meter meter(Class<?> clazz, String... names) {
        return getMeterMetricRegistry().meter(MetricRegistry.name(clazz, names));
    }

    /**
     * Registers a {@link Gauge} on the shared gauge registry without
     * holding a bean reference to this factory.
     *
     * @param supplier the Dropwizard supplier that produces the gauge.
     * @param clazz the owning class used as the name prefix.
     * @param names additional dotted name segments.
     * @param <T> the gauge value type.
     * @return the registered {@link Gauge}.
     */
    public static <T> Gauge<T> gauge(MetricSupplier<Gauge> supplier, Class<?> clazz, String... names) {
        return getGaugeMetricRegistry().gauge(MetricRegistry.name(clazz , names), supplier);
    }

}