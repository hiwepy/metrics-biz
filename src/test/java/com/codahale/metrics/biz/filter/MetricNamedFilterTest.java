package com.codahale.metrics.biz.filter;

import static org.junit.Assert.*;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import org.junit.Test;

public class MetricNamedFilterTest {

    @Test
    public void shouldMatchExactMetricName() {
        MetricNamedFilter filter = new MetricNamedFilter();
        filter.setMetrics("com.example.MyClass.orders");

        assertTrue(filter.matches("com.example.MyClass.orders", new Counter()));
    }

    @Test
    public void shouldNotMatchDifferentMetricName() {
        MetricNamedFilter filter = new MetricNamedFilter();
        filter.setMetrics("com.example.MyClass.orders");

        assertFalse(filter.matches("com.example.MyClass.errors", new Counter()));
    }

    @Test
    public void shouldNotMatchNullNameWhenFilterIsNull() {
        MetricNamedFilter filter = new MetricNamedFilter();
        // metrics is null by default

        assertFalse(filter.matches("any.name", new Counter()));
    }

    @Test
    public void shouldReturnNullMetricsByDefault() {
        MetricNamedFilter filter = new MetricNamedFilter();

        assertNull(filter.getMetrics());
    }

    @Test
    public void shouldUpdateMetricsViaSetter() {
        MetricNamedFilter filter = new MetricNamedFilter();
        filter.setMetrics("com.example.requests");

        assertEquals("com.example.requests", filter.getMetrics());
    }

    @Test
    public void shouldMatchRegardlessOfMetricInstance() {
        MetricNamedFilter filter = new MetricNamedFilter();
        filter.setMetrics("test.metric");

        Counter counter = new Counter();
        assertTrue(filter.matches("test.metric", counter));

        // Different metric type should still match (name-based only)
        assertTrue(filter.matches("test.metric", null));
    }

    @Test
    public void shouldAllowChangingFilterName() {
        MetricNamedFilter filter = new MetricNamedFilter();
        filter.setMetrics("first.name");

        assertTrue(filter.matches("first.name", new Counter()));
        assertFalse(filter.matches("second.name", new Counter()));

        filter.setMetrics("second.name");

        assertFalse(filter.matches("first.name", new Counter()));
        assertTrue(filter.matches("second.name", new Counter()));
    }

    @Test
    public void shouldFilterGaugesInRegistry() {
        MetricRegistry registry = new MetricRegistry();
        registry.register("target.gauge", (com.codahale.metrics.Gauge<Long>) () -> 42L);
        registry.register("other.gauge", (com.codahale.metrics.Gauge<Long>) () -> 99L);

        MetricNamedFilter filter = new MetricNamedFilter();
        filter.setMetrics("target.gauge");

        assertEquals(1, registry.getGauges(filter).size());
        assertTrue(registry.getGauges(filter).containsKey("target.gauge"));
    }

}
