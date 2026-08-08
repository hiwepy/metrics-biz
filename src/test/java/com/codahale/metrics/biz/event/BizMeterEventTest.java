package com.codahale.metrics.biz.event;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BizMeterEventTest {

    @Test
    public void shouldCreateEventWithExplicitEventPoint() {
        BizEventPoint point = new BizEventPoint("requests", "Request received");
        BizMeterEvent event = new BizMeterEvent(this, point);

        assertSame(this, event.getSource());
        assertSame(point, event.getBind());
        assertEquals("requests", event.getBind().getName());
    }

    @Test
    public void shouldCreateEventWithName() {
        BizMeterEvent event = new BizMeterEvent(this, "errors");

        assertNotNull(event.getBind());
        assertEquals("errors", event.getBind().getName());
        assertNull(event.getBind().getMessage());
    }

    @Test
    public void shouldCreateEventWithNameAndMessage() {
        BizMeterEvent event = new BizMeterEvent(this, "errors", "Error occurred");

        assertEquals("errors", event.getBind().getName());
        assertEquals("Error occurred", event.getBind().getMessage());
    }

    @Test
    public void shouldCreateEventWithNameMessageAndData() {
        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", 500);

        BizMeterEvent event = new BizMeterEvent(this, "errors", "Server error", data);

        assertEquals("errors", event.getBind().getName());
        assertEquals("Server error", event.getBind().getMessage());
        assertEquals(500, event.getBind().getData().get("statusCode"));
    }

}
