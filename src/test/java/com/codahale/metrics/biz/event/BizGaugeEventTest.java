package com.codahale.metrics.biz.event;

import static org.junit.Assert.*;

import org.junit.Test;

public class BizGaugeEventTest {

    @Test
    public void shouldCreateEventWithExplicitEventPoint() {
        BizEventPoint point = new BizEventPoint("cpu", "CPU usage", 75L);
        BizGaugeEvent event = new BizGaugeEvent(this, point);

        assertSame(this, event.getSource());
        assertSame(point, event.getBind());
        assertEquals("cpu", event.getBind().getName());
        assertEquals(Long.valueOf(75L), event.getBind().getValue());
    }

    @Test
    public void shouldCreateEventWithNameAndValue() {
        BizGaugeEvent event = new BizGaugeEvent(this, "memory", 1024L);

        assertNotNull(event.getBind());
        assertEquals("memory", event.getBind().getName());
        assertEquals(Long.valueOf(1024L), event.getBind().getValue());
        assertNull(event.getBind().getMessage());
    }

    @Test
    public void shouldCreateEventWithNameMessageAndValue() {
        BizGaugeEvent event = new BizGaugeEvent(this, "memory", "Heap usage", 2048L);

        assertEquals("memory", event.getBind().getName());
        assertEquals("Heap usage", event.getBind().getMessage());
        assertEquals(Long.valueOf(2048L), event.getBind().getValue());
    }

}
