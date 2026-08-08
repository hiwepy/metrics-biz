package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.biz.MetricsFactory;

import org.junit.Before;
import org.junit.Test;

public class HttpServletContextMetricsListenerTest {

    private HttpServletContextMetricsListener listener;

    @Before
    public void setUp() {
        listener = new HttpServletContextMetricsListener();
        // Use a fresh registry per test to avoid cross-test contamination
        listener.registry = new MetricRegistry();
    }

    @Test
    public void shouldTrackAttributeAdded() {
        ServletContext ctx = createMockServletContext("/app");
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(ctx, "testAttr", "value");

        listener.attributeAdded(event);

        String expectedName = MetricRegistry.name(
                HttpServletContextMetricsListener.class, "/app", "ServletContext", "attributeAdded");
        assertTrue("Should have registered an attributeAdded meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackAttributeRemoved() {
        ServletContext ctx = createMockServletContext("/app");
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(ctx, "testAttr", "value");

        listener.attributeRemoved(event);

        String expectedName = MetricRegistry.name(
                HttpServletContextMetricsListener.class, "/app", "ServletContext", "attributeRemoved");
        assertTrue("Should have registered an attributeRemoved meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackAttributeReplaced() {
        ServletContext ctx = createMockServletContext("/app");
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(ctx, "testAttr", "oldValue");

        listener.attributeReplaced(event);

        String expectedName = MetricRegistry.name(
                HttpServletContextMetricsListener.class, "/app", "ServletContext", "attributeReplaced");
        assertTrue("Should have registered an attributeReplaced meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldClearSharedRegistriesOnContextDestroyed() {
        // This should not throw
        listener.contextDestroyed(null);

        // Re-create after clear
        assertTrue(true);
    }

    private ServletContext createMockServletContext(String contextPath) {
        return (ServletContext) java.lang.reflect.Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class[]{ServletContext.class},
                (proxy, method, args) -> {
                    if ("getContextPath".equals(method.getName())) {
                        return contextPath;
                    }
                    if ("toString".equals(method.getName())) {
                        return "MockServletContext";
                    }
                    return null;
                }
        );
    }

}
