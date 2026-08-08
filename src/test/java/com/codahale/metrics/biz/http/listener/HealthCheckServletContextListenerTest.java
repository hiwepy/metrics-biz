package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;

import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.health.HealthCheckRegistry;

import org.junit.Test;

public class HealthCheckServletContextListenerTest {

    @Test
    public void shouldReturnHealthCheckRegistry() {
        HealthCheckServletContextListener listener = new HealthCheckServletContextListener();

        // Access the protected method via reflection
        try {
            java.lang.reflect.Method method = HealthCheckServletContextListener.class
                    .getDeclaredMethod("getHealthCheckRegistry");
            method.setAccessible(true);

            HealthCheckRegistry registry = (HealthCheckRegistry) method.invoke(listener);

            assertNotNull("Registry must not be null", registry);
            assertSame("Should return the context health check registry",
                    MetricsFactory.getContextHealthCheckRegistry(), registry);
        } catch (Exception e) {
            fail("Failed to invoke getHealthCheckRegistry: " + e.getMessage());
        }
    }

    @Test
    public void shouldReturnExecutorService() {
        HealthCheckServletContextListener listener = new HealthCheckServletContextListener();

        try {
            java.lang.reflect.Method method = HealthCheckServletContextListener.class
                    .getDeclaredMethod("getExecutorService");
            method.setAccessible(true);

            ExecutorService executor = (ExecutorService) method.invoke(listener);

            assertNotNull("ExecutorService must not be null", executor);
            assertFalse("ExecutorService must not be shutdown", executor.isShutdown());

            // Clean up
            executor.shutdown();
        } catch (Exception e) {
            fail("Failed to invoke getExecutorService: " + e.getMessage());
        }
    }

}
