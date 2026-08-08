package com.codahale.metrics.biz;

import static org.junit.Assert.*;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheckRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MetricsFactoryTest {

    private MetricsFactory factory;

    @Before
    public void setUp() {
        factory = new MetricsFactory();
    }

    @After
    public void tearDown() throws Exception {
        if (factory.jmxReporter != null) {
            factory.destroy();
        }
    }

    // --- Lifecycle ---

    @Test
    public void shouldStartJmxReporterOnAfterPropertiesSet() throws Exception {
        factory.afterPropertiesSet();
        assertNotNull(factory.jmxReporter);
    }

    @Test
    public void shouldStopJmxReporterOnDestroy() throws Exception {
        factory.afterPropertiesSet();
        assertNotNull(factory.jmxReporter);
        factory.destroy();
        // Should not throw
    }

    // --- Registry accessors ---

    @Test
    public void shouldReturnDefaultRegistry() {
        assertNotNull(factory.getRegistry());
    }

    @Test
    public void shouldAllowSettingCustomRegistry() {
        MetricRegistry custom = new MetricRegistry();
        factory.setRegistry(custom);
        assertSame(custom, factory.getRegistry());
    }

    // --- Timer ---

    @Test
    public void shouldReturnTimerByName() {
        Timer timer = factory.getTimer("com.example", "requests");
        assertNotNull(timer);
    }

    @Test
    public void shouldReturnCachedTimerByName() {
        Timer t1 = factory.getTimer("com.example", "cached-timer");
        Timer t2 = factory.getTimer("com.example", "cached-timer");
        assertSame("Same name should return the same cached Timer", t1, t2);
    }

    @Test
    public void shouldReturnTimerByClassAndName() {
        Timer timer = factory.getTimer(MetricsFactoryTest.class, "latency");
        assertNotNull(timer);
    }

    @Test
    public void shouldReturnCachedTimerByClassAndName() {
        Timer t1 = factory.getTimer(MetricsFactoryTest.class, "cached-timer2");
        Timer t2 = factory.getTimer(MetricsFactoryTest.class, "cached-timer2");
        assertSame(t1, t2);
    }

    // --- Histogram ---

    @Test
    public void shouldReturnHistogramByName() {
        Histogram histogram = factory.getHistogram("com.example", "sizes");
        assertNotNull(histogram);
    }

    @Test
    public void shouldReturnCachedHistogramByName() {
        Histogram h1 = factory.getHistogram("com.example", "cached-hist");
        Histogram h2 = factory.getHistogram("com.example", "cached-hist");
        assertSame(h1, h2);
    }

    @Test
    public void shouldReturnHistogramByClassAndName() {
        Histogram histogram = factory.getHistogram(MetricsFactoryTest.class, "sizes");
        assertNotNull(histogram);
    }

    // --- Counter ---

    @Test
    public void shouldReturnCounterByName() {
        Counter counter = factory.getCounter("com.example", "errors");
        assertNotNull(counter);
    }

    @Test
    public void shouldReturnCachedCounterByName() {
        Counter c1 = factory.getCounter("com.example", "cached-counter");
        Counter c2 = factory.getCounter("com.example", "cached-counter");
        assertSame(c1, c2);
    }

    @Test
    public void shouldReturnCounterByClassAndName() {
        Counter counter = factory.getCounter(MetricsFactoryTest.class, "errors");
        assertNotNull(counter);
    }

    // --- Meter ---

    @Test
    public void shouldReturnMeterByName() {
        Meter meter = factory.getMeter("com.example", "throughput");
        assertNotNull(meter);
    }

    @Test
    public void shouldReturnCachedMeterByName() {
        Meter m1 = factory.getMeter("com.example", "cached-meter");
        Meter m2 = factory.getMeter("com.example", "cached-meter");
        assertSame(m1, m2);
    }

    @Test
    public void shouldReturnMeterByClassAndName() {
        Meter meter = factory.getMeter(MetricsFactoryTest.class, "throughput");
        assertNotNull(meter);
    }

    // --- Gauge ---

    @Test
    public void shouldRegisterGaugeViaSupplier() {
        Gauge<Long> gauge = factory.getGauge(
                () -> (Gauge<Long>) () -> 42L,
                MetricsFactoryTest.class,
                "test-gauge"
        );
        assertNotNull(gauge);
    }

    @Test
    public void shouldReturnCachedGauge() {
        Gauge<Long> g1 = factory.getGauge(
                () -> (Gauge<Long>) () -> 1L,
                MetricsFactoryTest.class,
                "cached-gauge"
        );
        Gauge<Long> g2 = factory.getGauge(
                () -> (Gauge<Long>) () -> 2L,
                MetricsFactoryTest.class,
                "cached-gauge"
        );
        assertSame("Same name should return the cached gauge", g1, g2);
    }

    // --- register / registerAll / remove ---

    @Test
    public void shouldRegisterMetric() {
        Counter counter = new Counter();
        Counter registered = factory.register("my.counter", counter);
        assertSame(counter, registered);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenRegisteringDuplicateName() {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        factory.register("dup.counter", c1);
        factory.register("dup.counter", c2);
    }

    @Test
    public void shouldRegisterAllMetrics() {
        Map<String, Metric> metrics = new HashMap<>();
        metrics.put("set.counter", new Counter());
        metrics.put("set.meter", new Meter());
        MetricSet set = () -> metrics;

        factory.registerAll(set);
        assertTrue(factory.getRegistry().getCounters().containsKey("set.counter"));
        assertTrue(factory.getRegistry().getMeters().containsKey("set.meter"));
    }

    @Test
    public void shouldRemoveMetric() {
        factory.register("removable.counter", new Counter());
        assertTrue(factory.remove("removable.counter"));
    }

    @Test
    public void shouldReturnFalseWhenRemovingNonexistentMetric() {
        assertFalse(factory.remove("nonexistent.metric"));
    }

    @Test
    public void shouldRemoveMatchingMetrics() {
        factory.register("match.a", new Counter());
        factory.register("match.b", new Counter());
        factory.register("keep.c", new Counter());

        factory.removeMatching((name, metric) -> name.startsWith("match."));

        assertFalse(factory.getRegistry().getCounters().containsKey("match.a"));
        assertFalse(factory.getRegistry().getCounters().containsKey("match.b"));
        assertTrue(factory.getRegistry().getCounters().containsKey("keep.c"));
    }

    // --- Static helpers ---

    @Test
    public void shouldReturnContextMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getContextMetricRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnContextHealthCheckRegistry() {
        HealthCheckRegistry registry = MetricsFactory.getContextHealthCheckRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnGaugeMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getGaugeMetricRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnCounterMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getCounterMetricRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnHistogramMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getHistogramMetricRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnMeterMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getMeterMetricRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnTimerMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getTimerMetricRegistry();
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnNamedMetricRegistry() {
        MetricRegistry registry = MetricsFactory.getMetricRegistry("custom-registry");
        assertNotNull(registry);
    }

    @Test
    public void shouldReturnSameRegistryForSameName() {
        MetricRegistry r1 = MetricsFactory.getMetricRegistry("same-name");
        MetricRegistry r2 = MetricsFactory.getMetricRegistry("same-name");
        assertSame(r1, r2);
    }

    // --- Static metric creation ---

    @Test
    public void shouldCreateStaticHistogram() {
        Histogram histogram = MetricsFactory.histogram(MetricsFactoryTest.class, "static-hist");
        assertNotNull(histogram);
    }

    @Test
    public void shouldCreateStaticTimer() {
        Timer timer = MetricsFactory.timer(MetricsFactoryTest.class, "static-timer");
        assertNotNull(timer);
    }

    @Test
    public void shouldCreateStaticCounter() {
        Counter counter = MetricsFactory.counter(MetricsFactoryTest.class, "static-counter");
        assertNotNull(counter);
    }

    @Test
    public void shouldCreateStaticMeter() {
        Meter meter = MetricsFactory.meter(MetricsFactoryTest.class, "static-meter");
        assertNotNull(meter);
    }

    @Test
    public void shouldCreateStaticGauge() {
        Gauge<Long> gauge = MetricsFactory.gauge(
                () -> (Gauge<Long>) () -> 99L,
                MetricsFactoryTest.class,
                "static-gauge"
        );
        assertNotNull(gauge);
    }

    // --- Servlet context constant ---

    @Test
    public void shouldHaveServletContextMetricRegistryConstant() {
        assertNotNull(MetricsFactory.SERVLET_CONTEXT_METRIC_REGISTRY);
        assertTrue(MetricsFactory.SERVLET_CONTEXT_METRIC_REGISTRY.contains("registry"));
    }

    // --- Metric caching edge cases ---

    @Test
    public void shouldCacheMetricsAcrossMultipleGetCalls() {
        // Timer
        Timer t1 = factory.getTimer("cache", "test");
        Timer t2 = factory.getTimer("cache", "test");
        assertSame(t1, t2);

        // Counter
        Counter c1 = factory.getCounter("cache", "counter");
        Counter c2 = factory.getCounter("cache", "counter");
        assertSame(c1, c2);

        // Histogram
        Histogram h1 = factory.getHistogram("cache", "hist");
        Histogram h2 = factory.getHistogram("cache", "hist");
        assertSame(h1, h2);

        // Meter
        Meter m1 = factory.getMeter("cache", "meter");
        Meter m2 = factory.getMeter("cache", "meter");
        assertSame(m1, m2);
    }

}
