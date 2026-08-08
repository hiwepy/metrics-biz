package com.codahale.metrics.biz.event.listener;

import static org.junit.Assert.*;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizHistogramEvent;
import com.codahale.metrics.biz.event.BizEventPoint;

import org.junit.Before;
import org.junit.Test;

public class BizHistogramEventListenerTest {

    private BizHistogramEventListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new BizHistogramEventListener();
        listener.afterPropertiesSet();
    }

    @Test
    public void shouldResolveMetricRegistryFromSharedRegistry() {
        assertNotNull(listener.getMetricRegistry());
    }

    @Test
    public void shouldResolveMetricRegistryFromFactory() throws Exception {
        BizHistogramEventListener listenerWithFactory = new BizHistogramEventListener();
        MetricsFactory factory = new MetricsFactory();
        listenerWithFactory.setMetricsFactory(factory);
        listenerWithFactory.afterPropertiesSet();

        assertSame(factory.getRegistry(), listenerWithFactory.getMetricRegistry());
    }

    @Test
    public void shouldUpdateHistogramOnEvent() {
        BizHistogramEvent event = new BizHistogramEvent(this, "latency", 42L);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "latency");
        Histogram histogram = listener.getMetricRegistry().getHistograms().get(expectedName);
        assertNotNull("Histogram should have been registered", histogram);
        assertEquals("Histogram should have one sample", 1, histogram.getCount());
    }

    @Test
    public void shouldUpdateHistogramWithMultipleSamples() {
        listener.onApplicationEvent(new BizHistogramEvent(this, "response-size", 100L));
        listener.onApplicationEvent(new BizHistogramEvent(this, "response-size", 200L));
        listener.onApplicationEvent(new BizHistogramEvent(this, "response-size", 300L));

        String expectedName = MetricRegistry.name(this.getClass(), "response-size");
        Histogram histogram = listener.getMetricRegistry().getHistograms().get(expectedName);
        assertEquals("Histogram should have three samples", 3, histogram.getCount());
    }

    @Test
    public void shouldCreateSeparateHistogramsForDifferentNames() {
        listener.onApplicationEvent(new BizHistogramEvent(this, "hist-a", 10L));
        listener.onApplicationEvent(new BizHistogramEvent(this, "hist-b", 20L));

        String nameA = MetricRegistry.name(this.getClass(), "hist-a");
        String nameB = MetricRegistry.name(this.getClass(), "hist-b");

        assertEquals(1, listener.getMetricRegistry().getHistograms().get(nameA).getCount());
        assertEquals(1, listener.getMetricRegistry().getHistograms().get(nameB).getCount());
    }

    @Test
    public void shouldUseEventPointNameAndValue() {
        BizEventPoint point = new BizEventPoint("db-query", "DB query time", 50L);
        BizHistogramEvent event = new BizHistogramEvent(this, point);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "db-query");
        Histogram histogram = listener.getMetricRegistry().getHistograms().get(expectedName);
        assertNotNull(histogram);
        assertEquals(1, histogram.getCount());
    }

}
