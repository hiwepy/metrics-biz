package com.codahale.metrics.biz.event;

import static org.junit.Assert.*;

import org.junit.Test;

public class BizHistogramEventTest {

    @Test
    public void shouldCreateEventWithExplicitEventPoint() {
        BizEventPoint point = new BizEventPoint("latency", "Request latency", 42L);
        BizHistogramEvent event = new BizHistogramEvent(this, point);

        assertSame(this, event.getSource());
        assertSame(point, event.getBind());
        assertEquals("latency", event.getBind().getName());
        assertEquals(Long.valueOf(42L), event.getBind().getValue());
    }

    @Test
    public void shouldCreateEventWithNameAndValue() {
        BizHistogramEvent event = new BizHistogramEvent(this, "responseSize", 1024L);

        assertNotNull(event.getBind());
        assertEquals("responseSize", event.getBind().getName());
        assertEquals(Long.valueOf(1024L), event.getBind().getValue());
        assertNull(event.getBind().getMessage());
    }

    @Test
    public void shouldCreateEventWithNameMessageAndValue() {
        BizHistogramEvent event = new BizHistogramEvent(this, "responseSize", "Response payload size", 2048L);

        assertEquals("responseSize", event.getBind().getName());
        assertEquals("Response payload size", event.getBind().getMessage());
        assertEquals(Long.valueOf(2048L), event.getBind().getValue());
    }

}
