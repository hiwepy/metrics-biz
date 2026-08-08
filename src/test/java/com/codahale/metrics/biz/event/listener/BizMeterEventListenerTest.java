package com.codahale.metrics.biz.event.listener;

import static org.junit.Assert.*;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizMeterEvent;
import com.codahale.metrics.biz.event.BizEventPoint;

import org.junit.Before;
import org.junit.Test;

public class BizMeterEventListenerTest {

    private BizMeterEventListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new BizMeterEventListener();
        listener.afterPropertiesSet();
    }

    @Test
    public void shouldResolveMetricRegistryFromSharedRegistry() {
        assertNotNull(listener.getMetricRegistry());
    }

    @Test
    public void shouldResolveMetricRegistryFromFactory() throws Exception {
        BizMeterEventListener listenerWithFactory = new BizMeterEventListener();
        MetricsFactory factory = new MetricsFactory();
        listenerWithFactory.setMetricsFactory(factory);
        listenerWithFactory.afterPropertiesSet();

        assertSame(factory.getRegistry(), listenerWithFactory.getMetricRegistry());
    }

    @Test
    public void shouldMarkMeterOnEvent() {
        BizMeterEvent event = new BizMeterEvent(this, "requests");
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "requests");
        Meter meter = listener.getMetricRegistry().getMeters().get(expectedName);
        assertNotNull("Meter should have been registered", meter);
        assertEquals("Meter should have been marked once", 1, meter.getCount());
    }

    @Test
    public void shouldMarkMeterMultipleTimes() {
        BizMeterEvent event = new BizMeterEvent(this, "errors");
        listener.onApplicationEvent(event);
        listener.onApplicationEvent(event);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "errors");
        Meter meter = listener.getMetricRegistry().getMeters().get(expectedName);
        assertEquals("Meter should have been marked three times", 3, meter.getCount());
    }

    @Test
    public void shouldCreateSeparateMetersForDifferentNames() {
        listener.onApplicationEvent(new BizMeterEvent(this, "meter-a"));
        listener.onApplicationEvent(new BizMeterEvent(this, "meter-b"));

        String nameA = MetricRegistry.name(this.getClass(), "meter-a");
        String nameB = MetricRegistry.name(this.getClass(), "meter-b");

        assertEquals(1, listener.getMetricRegistry().getMeters().get(nameA).getCount());
        assertEquals(1, listener.getMetricRegistry().getMeters().get(nameB).getCount());
    }

    @Test
    public void shouldUseEventPointName() {
        BizEventPoint point = new BizEventPoint("api-calls", "API call made");
        BizMeterEvent event = new BizMeterEvent(this, point);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "api-calls");
        assertTrue(listener.getMetricRegistry().getMeters().containsKey(expectedName));
    }

}
