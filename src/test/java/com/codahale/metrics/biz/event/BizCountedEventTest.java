package com.codahale.metrics.biz.event;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BizCountedEventTest {

    @Test
    public void shouldCreateEventWithExplicitEventPoint() {
        BizEventPoint point = new BizEventPoint("orders", "Order placed");
        BizCountedEvent event = new BizCountedEvent(this, point);

        assertSame(this, event.getSource());
        assertSame(point, event.getBind());
        assertEquals("orders", event.getBind().getName());
    }

    @Test
    public void shouldCreateEventWithName() {
        BizCountedEvent event = new BizCountedEvent(this, "login");

        assertNotNull(event.getBind());
        assertEquals("login", event.getBind().getName());
        assertNull(event.getBind().getMessage());
    }

    @Test
    public void shouldCreateEventWithNameAndMessage() {
        BizCountedEvent event = new BizCountedEvent(this, "login", "User logged in");

        assertEquals("login", event.getBind().getName());
        assertEquals("User logged in", event.getBind().getMessage());
    }

    @Test
    public void shouldCreateEventWithNameMessageAndData() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 123L);

        BizCountedEvent event = new BizCountedEvent(this, "login", "User logged in", data);

        assertEquals("login", event.getBind().getName());
        assertEquals("User logged in", event.getBind().getMessage());
        assertEquals(123L, event.getBind().getData().get("userId"));
    }

}
