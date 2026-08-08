package com.codahale.metrics.biz.event.listener;

import static org.junit.Assert.*;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizCountedEvent;
import com.codahale.metrics.biz.event.BizEventPoint;

import org.junit.Before;
import org.junit.Test;

public class BizCountedEventListenerTest {

    private BizCountedEventListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new BizCountedEventListener();
        listener.afterPropertiesSet();
    }

    @Test
    public void shouldResolveMetricRegistryFromSharedRegistry() {
        assertNotNull(listener.getMetricRegistry());
    }

    @Test
    public void shouldResolveMetricRegistryFromFactory() throws Exception {
        BizCountedEventListener listenerWithFactory = new BizCountedEventListener();
        MetricsFactory factory = new MetricsFactory();
        listenerWithFactory.setMetricsFactory(factory);
        listenerWithFactory.afterPropertiesSet();

        assertSame(factory.getRegistry(), listenerWithFactory.getMetricRegistry());
    }

    @Test
    public void shouldIncrementCounterOnEvent() {
        BizCountedEvent event = new BizCountedEvent(this, "test-counter");
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "test-counter");
        Counter counter = listener.getMetricRegistry().getCounters().get(expectedName);
        assertNotNull("Counter should have been registered", counter);
        assertEquals("Counter should have been incremented once", 1, counter.getCount());
    }

    @Test
    public void shouldIncrementCounterMultipleTimes() {
        BizCountedEvent event = new BizCountedEvent(this, "multi-counter");
        listener.onApplicationEvent(event);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "multi-counter");
        Counter counter = listener.getMetricRegistry().getCounters().get(expectedName);
        assertEquals("Counter should have been incremented twice", 2, counter.getCount());
    }

    @Test
    public void shouldCreateSeparateCountersForDifferentNames() {
        listener.onApplicationEvent(new BizCountedEvent(this, "counter-a"));
        listener.onApplicationEvent(new BizCountedEvent(this, "counter-b"));

        String nameA = MetricRegistry.name(this.getClass(), "counter-a");
        String nameB = MetricRegistry.name(this.getClass(), "counter-b");

        assertEquals(1, listener.getMetricRegistry().getCounters().get(nameA).getCount());
        assertEquals(1, listener.getMetricRegistry().getCounters().get(nameB).getCount());
    }

    @Test
    public void shouldUseEventPointName() {
        BizEventPoint point = new BizEventPoint("orders", "Order placed");
        BizCountedEvent event = new BizCountedEvent(this, point);
        listener.onApplicationEvent(event);

        String expectedName = MetricRegistry.name(this.getClass(), "orders");
        assertTrue(listener.getMetricRegistry().getCounters().containsKey(expectedName));
    }

}
