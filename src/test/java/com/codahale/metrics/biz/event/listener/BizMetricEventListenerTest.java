package com.codahale.metrics.biz.event.listener;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.biz.MetricsFactory;

import org.junit.Test;

public class BizMetricEventListenerTest {

    /**
     * Concrete subclass for testing the abstract base.
     */
    private static class TestableBizMetricEventListener extends BizMetricEventListener<org.springframework.context.ApplicationEvent> {

        public org.springframework.context.ApplicationEvent lastEvent;

        @Override
        public void onApplicationEvent(org.springframework.context.ApplicationEvent event) {
            this.lastEvent = event;
        }
    }

    @Test
    public void shouldHaveDefaultInitialDelay() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        assertEquals(0L, listener.getInitialDelay());
    }

    @Test
    public void shouldHaveDefaultPeriod() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        assertEquals(1L, listener.getPeriod());
    }

    @Test
    public void shouldHaveDefaultUnit() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        assertEquals(TimeUnit.SECONDS, listener.getUnit());
    }

    @Test
    public void shouldAllowSettingInitialDelay() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        listener.setInitialDelay(10L);
        assertEquals(10L, listener.getInitialDelay());
    }

    @Test
    public void shouldAllowSettingPeriod() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        listener.setPeriod(5L);
        assertEquals(5L, listener.getPeriod());
    }

    @Test
    public void shouldAllowSettingUnit() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        listener.setUnit(TimeUnit.MINUTES);
        assertEquals(TimeUnit.MINUTES, listener.getUnit());
    }

    @Test
    public void shouldReturnNullMetricsFactoryByDefault() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        assertNull(listener.getMetricsFactory());
    }

    @Test
    public void shouldAllowSettingMetricsFactory() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();
        MetricsFactory factory = new MetricsFactory();

        listener.setMetricsFactory(factory);
        assertSame(factory, listener.getMetricsFactory());
    }

    @Test
    public void shouldAllowNullMetricsFactory() {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        listener.setMetricsFactory(null);
        assertNull(listener.getMetricsFactory());
    }

    @Test
    public void shouldCompleteAfterPropertiesSetWithoutError() throws Exception {
        TestableBizMetricEventListener listener = new TestableBizMetricEventListener();

        // Should not throw
        listener.afterPropertiesSet();
    }

}
