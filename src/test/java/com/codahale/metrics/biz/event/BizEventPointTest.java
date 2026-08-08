package com.codahale.metrics.biz.event;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BizEventPointTest {

    @Test
    public void shouldCreatePointWithNameAndMessage() {
        BizEventPoint point = new BizEventPoint("orders", "Order placed");

        assertEquals("orders", point.getName());
        assertEquals("Order placed", point.getMessage());
        assertNotNull(point.getUid());
        assertTrue(point.getTimestamp() > 0);
        assertNotNull(point.getData());
        assertTrue(point.getData().isEmpty());
        assertNull(point.getPrev());
        assertNull(point.getValue());
    }

    @Test
    public void shouldCreatePointWithNameMessageAndValue() {
        BizEventPoint point = new BizEventPoint("latency", "Request latency", 150L);

        assertEquals("latency", point.getName());
        assertEquals("Request latency", point.getMessage());
        assertEquals(Long.valueOf(150L), point.getValue());
    }

    @Test
    public void shouldCreatePointWithPreviousPointer() {
        BizEventPoint prev = new BizEventPoint("root", "root msg");
        BizEventPoint point = new BizEventPoint(prev, "child", "child msg");

        assertSame(prev, point.getPrev());
        assertEquals("child", point.getName());
    }

    @Test
    public void shouldCreatePointWithExplicitTimestamp() {
        long ts = 1700000000000L;
        BizEventPoint point = new BizEventPoint("metric", ts, "msg");

        assertEquals("metric", point.getName());
        assertEquals(ts, point.getTimestamp());
    }

    @Test
    public void shouldCreatePointWithPreviousAndTimestamp() {
        BizEventPoint prev = new BizEventPoint("root", "root msg");
        long ts = 1700000000000L;
        BizEventPoint point = new BizEventPoint(prev, "metric", ts, "msg");

        assertSame(prev, point.getPrev());
        assertEquals(ts, point.getTimestamp());
    }

    @Test
    public void shouldCreatePointWithDataMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");
        BizEventPoint point = new BizEventPoint("metric", "msg", data);

        assertSame(data, point.getData());
        assertEquals("value", point.getData().get("key"));
    }

    @Test
    public void shouldCreatePointWithPreviousAndDataMap() {
        BizEventPoint prev = new BizEventPoint("root", "root msg");
        Map<String, Object> data = new HashMap<>();
        data.put("region", "us-east-1");
        BizEventPoint point = new BizEventPoint(prev, "metric", "msg", data);

        assertSame(prev, point.getPrev());
        assertEquals("us-east-1", point.getData().get("region"));
    }

    @Test
    public void shouldCreatePointWithTimestampAndDataMap() {
        // Note: this constructor delegates to SystemClock.now() for the timestamp
        Map<String, Object> data = new HashMap<>();
        data.put("count", 42);
        BizEventPoint point = new BizEventPoint("metric", 1700000000000L, "msg", data);

        assertTrue("Timestamp should be set from SystemClock.now()", point.getTimestamp() > 0);
        assertEquals(42, point.getData().get("count"));
    }

    @Test
    public void shouldCreateFullySpecifiedPoint() {
        BizEventPoint prev = new BizEventPoint("root", "root msg");
        Map<String, Object> data = new HashMap<>();
        data.put("k", "v");
        long ts = 1700000000000L;

        BizEventPoint point = new BizEventPoint(prev, "metric", ts, "msg", data);

        assertSame(prev, point.getPrev());
        assertEquals("metric", point.getName());
        assertEquals(ts, point.getTimestamp());
        assertEquals("msg", point.getMessage());
        assertSame(data, point.getData());
        assertNotNull(point.getUid());
    }

    @Test
    public void shouldSubstituteEmptyMapWhenDataIsNull() {
        BizEventPoint point = new BizEventPoint(null, "metric", 0L, "msg", null);

        assertNotNull(point.getData());
        assertTrue(point.getData().isEmpty());
    }

    @Test
    public void shouldSupportSetters() {
        BizEventPoint point = new BizEventPoint("old", "old msg");

        point.setName("new");
        point.setMessage("new msg");
        point.setTimestamp(999L);
        point.setValue(42L);
        point.setUid("custom-uid");

        BizEventPoint prev = new BizEventPoint("prev", "prev msg");
        point.setPrev(prev);

        assertEquals("new", point.getName());
        assertEquals("new msg", point.getMessage());
        assertEquals(999L, point.getTimestamp());
        assertEquals(Long.valueOf(42L), point.getValue());
        assertEquals("custom-uid", point.getUid());
        assertSame(prev, point.getPrev());
    }

    @Test
    public void shouldPutEntryIntoDataMap() {
        BizEventPoint point = new BizEventPoint("metric", "msg");

        point.put("host", "localhost");
        point.put("port", 8080);

        assertEquals("localhost", point.getData().get("host"));
        assertEquals(8080, point.getData().get("port"));
    }

    @Test
    public void shouldHaveUniqueUidPerInstance() {
        BizEventPoint p1 = new BizEventPoint("a", "msg");
        BizEventPoint p2 = new BizEventPoint("b", "msg");

        assertNotNull(p1.getUid());
        assertNotNull(p2.getUid());
        assertNotEquals(p1.getUid(), p2.getUid());
    }

    @Test
    public void shouldExposeRootSentinel() {
        assertNotNull(BizEventPoint.ROOT);
        assertEquals("root", BizEventPoint.ROOT.getName());
    }

}
