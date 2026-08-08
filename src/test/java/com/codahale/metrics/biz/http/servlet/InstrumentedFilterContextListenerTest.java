package com.codahale.metrics.biz.http.servlet;

import static org.junit.Assert.*;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

import org.junit.Test;

public class InstrumentedFilterContextListenerTest {

    @Test
    public void shouldReturnDefaultMetricRegistry() {
        InstrumentedFilterContextListener listener = new InstrumentedFilterContextListener();

        MetricRegistry registry = listener.getMetricRegistry();

        assertNotNull("Registry must not be null", registry);
        assertSame("Should return the default context registry",
                MetricsFactory.getContextMetricRegistry(), registry);
    }

}
