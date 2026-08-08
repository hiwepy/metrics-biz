package com.codahale.metrics.biz.event.listener;

import static org.junit.Assert.*;

import java.util.SortedMap;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizGaugeEvent;
import com.codahale.metrics.biz.event.BizEventPoint;

import org.junit.Before;
import org.junit.Test;

public class BizGaugeEventListenerTest {

    private BizGaugeEventListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new BizGaugeEventListener();
        listener.afterPropertiesSet();
    }

    @Test
    public void shouldResolveMetricRegistryFromSharedRegistry() {
        assertNotNull(listener.getMetricRegistry());
    }

    @Test
    public void shouldResolveMetricRegistryFromFactory() throws Exception {
        BizGaugeEventListener listenerWithFactory = new BizGaugeEventListener();
        MetricsFactory factory = new MetricsFactory();
        listenerWithFactory.setMetricsFactory(factory);
        listenerWithFactory.afterPropertiesSet();

        assertSame(factory.getRegistry(), listenerWithFactory.getMetricRegistry());
    }

    @Test
    public void shouldRegisterGaugeOnFirstEvent() {
        BizGaugeEvent event = new BizGaugeEvent(this, "cpu", 75L);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "cpu");
        SortedMap<String, Gauge> gauges = listener.getMetricRegistry().getGauges();
        assertTrue("Gauge should have been registered", gauges.containsKey(expectedName));
    }

    @Test
    public void shouldNotRegisterDuplicateGauge() {
        listener.onApplicationEvent(new BizGaugeEvent(this, "cpu", 75L));
        listener.onApplicationEvent(new BizGaugeEvent(this, "cpu", 80L));

        String expectedName = MetricRegistry.name(this.getClass(), "cpu");
        SortedMap<String, Gauge> gauges = listener.getMetricRegistry().getGauges();

        // Should only have one gauge with this name
        long count = gauges.keySet().stream()
                .filter(k -> k.equals(expectedName))
                .count();
        assertEquals("Should have exactly one gauge for this name", 1, count);
    }

    @Test
    public void shouldBufferValueInQueue() {
        BizGaugeEvent event = new BizGaugeEvent(this, "memory", 1024L);
        listener.onApplicationEvent(event);

        assertEquals(1, listener.queue.size());
        assertEquals(Long.valueOf(1024L), listener.queue.peek());
    }

    @Test
    public void shouldReturnPeekWhenSingleElementInQueue() {
        listener.onApplicationEvent(new BizGaugeEvent(this, "single", 42L));

        String expectedName = MetricRegistry.name(this.getClass(), "single");
        Gauge<Long> gauge = (Gauge<Long>) listener.getMetricRegistry().getGauges().get(expectedName);
        assertNotNull(gauge);
        assertEquals(Long.valueOf(42L), gauge.getValue());
    }

    @Test
    public void shouldReturnPolledValueWhenMultipleElementsInQueue() {
        listener.onApplicationEvent(new BizGaugeEvent(this, "multi", 10L));
        listener.onApplicationEvent(new BizGaugeEvent(this, "multi", 20L));

        String expectedName = MetricRegistry.name(this.getClass(), "multi");
        Gauge<Long> gauge = (Gauge<Long>) listener.getMetricRegistry().getGauges().get(expectedName);
        assertNotNull(gauge);
        // First getValue() should poll (remove) the head
        assertEquals(Long.valueOf(10L), gauge.getValue());
    }

    @Test
    public void shouldUseEventPointNameAndValue() {
        BizEventPoint point = new BizEventPoint("disk", "Disk usage", 500L);
        BizGaugeEvent event = new BizGaugeEvent(this, point);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "disk");
        assertTrue(listener.getMetricRegistry().getGauges().containsKey(expectedName));
    }

}
