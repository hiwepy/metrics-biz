package com.codahale.metrics.biz.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class SystemClockTest {

    @Test
    public void shouldReturnCurrentTimeInMillis() {
        long before = System.currentTimeMillis();
        long clock = SystemClock.now();
        long after = System.currentTimeMillis();

        // The cached clock refreshes every 1ms, so it may be slightly behind
        // the direct System.currentTimeMillis() call. Allow a tolerance of 50ms.
        assertTrue("now() should be close to current system time",
                Math.abs(clock - before) < 50 || Math.abs(clock - after) < 50);
    }

    @Test
    public void shouldReturnConsistentTimeAcrossCalls() {
        long first = SystemClock.now();
        long second = SystemClock.now();

        // Both should be very close; the daemon thread refreshes every 1ms
        assertTrue("Consecutive calls should return non-decreasing values", second >= first);
        assertTrue("Consecutive calls should not differ by more than 100ms",
                (second - first) < 100);
    }

    @Test
    public void shouldReturnNonNullDateString() {
        String date = SystemClock.nowDate();
        assertNotNull("nowDate() must not return null", date);
        assertFalse("nowDate() must not return empty string", date.isEmpty());
    }

    @Test
    public void shouldReturnDateStringContainingCurrentYear() {
        String date = SystemClock.nowDate();
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        assertTrue("nowDate() should contain the current year",
                date.contains(String.valueOf(currentYear)));
    }

    @Test
    public void shouldReturnMonotonicallyNonDecreasingValues() {
        long prev = SystemClock.now();
        for (int i = 0; i < 100; i++) {
            long current = SystemClock.now();
            assertTrue("now() must be monotonically non-decreasing", current >= prev);
            prev = current;
        }
    }

}
