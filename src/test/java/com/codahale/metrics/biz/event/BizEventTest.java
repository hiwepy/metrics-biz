package com.codahale.metrics.biz.event;

import static org.junit.Assert.*;

import org.junit.Test;

public class BizEventTest {

    @Test
    public void shouldHoldSourceAndBindPayload() {
        Object source = "testSource";
        String payload = "testPayload";

        BizEvent<String> event = new BizEvent<>(source, payload);

        assertEquals(source, event.getSource());
        assertEquals(payload, event.getBind());
    }

    @Test
    public void shouldAcceptNullBind() {
        Object source = "testSource";

        BizEvent<String> event = new BizEvent<>(source, null);

        assertEquals(source, event.getSource());
        assertNull(event.getBind());
    }

    @Test
    public void shouldReturnBindViaGetter() {
        BizEventPoint point = new BizEventPoint("metric", "message");
        BizEvent<BizEventPoint> event = new BizEvent<>(this, point);

        assertSame(point, event.getBind());
    }

    @Test
    public void shouldExposeSourceFromSuperclass() {
        Object source = new Object();
        BizEvent<Integer> event = new BizEvent<>(source, 42);

        assertSame(source, event.getSource());
    }

}
