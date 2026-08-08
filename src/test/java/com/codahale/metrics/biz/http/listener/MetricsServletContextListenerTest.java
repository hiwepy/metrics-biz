package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

import org.junit.Test;

public class MetricsServletContextListenerTest {

    @Test
    public void shouldReturnDefaultMetricRegistry() {
        MetricsServletContextListener listener = new MetricsServletContextListener();

        try {
            java.lang.reflect.Method method = MetricsServletContextListener.class
                    .getDeclaredMethod("getMetricRegistry");
            method.setAccessible(true);

            MetricRegistry registry = (MetricRegistry) method.invoke(listener);

            assertNotNull("Registry must not be null", registry);
            assertSame("Should return the context metric registry",
                    MetricsFactory.getContextMetricRegistry(), registry);
        } catch (Exception e) {
            fail("Failed to invoke getMetricRegistry: " + e.getMessage());
        }
    }

    @Test
    public void shouldReturnWildcardAllowedOrigin() {
        MetricsServletContextListener listener = new MetricsServletContextListener();

        try {
            java.lang.reflect.Method method = MetricsServletContextListener.class
                    .getDeclaredMethod("getAllowedOrigin");
            method.setAccessible(true);

            String origin = (String) method.invoke(listener);

            assertEquals("*", origin);
        } catch (Exception e) {
            fail("Failed to invoke getAllowedOrigin: " + e.getMessage());
        }
    }

}
