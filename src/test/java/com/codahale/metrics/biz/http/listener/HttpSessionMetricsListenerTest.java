package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import com.codahale.metrics.MetricRegistry;

import org.junit.Before;
import org.junit.Test;

public class HttpSessionMetricsListenerTest {

    private HttpSessionMetricsListener listener;

    @Before
    public void setUp() {
        listener = new HttpSessionMetricsListener();
        // Use a fresh registry per test to avoid cross-test contamination
        listener.registry = new MetricRegistry();
    }

    @Test
    public void shouldTrackSessionCreated() {
        HttpSession session = createMockSession("/app");
        HttpSessionEvent event = new HttpSessionEvent(session);

        listener.sessionCreated(event);

        String expectedName = MetricRegistry.name(
                HttpSessionMetricsListener.class, "/app", "session", "created");
        assertTrue("Should have registered a created meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackSessionDestroyed() {
        HttpSession session = createMockSession("/app");
        HttpSessionEvent event = new HttpSessionEvent(session);

        listener.sessionDestroyed(event);

        String expectedName = MetricRegistry.name(
                HttpSessionMetricsListener.class, "/app", "session", "destroyed");
        assertTrue("Should have registered a destroyed meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldMarkSessionCreationMeterMultipleTimes() {
        HttpSession session = createMockSession("/app");
        HttpSessionEvent event = new HttpSessionEvent(session);

        listener.sessionCreated(event);
        listener.sessionCreated(event);

        String expectedName = MetricRegistry.name(
                HttpSessionMetricsListener.class, "/app", "session", "created");
        assertEquals("Meter should have been marked twice", 2,
                listener.registry.getMeters().get(expectedName).getCount());
    }

    private HttpSession createMockSession(String contextPath) {
        ServletContext ctx = (ServletContext) java.lang.reflect.Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class[]{ServletContext.class},
                (proxy, method, args) -> {
                    if ("getContextPath".equals(method.getName())) {
                        return contextPath;
                    }
                    return null;
                }
        );

        return (HttpSession) java.lang.reflect.Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class[]{HttpSession.class},
                (proxy, method, args) -> {
                    if ("getServletContext".equals(method.getName())) {
                        return ctx;
                    }
                    return null;
                }
        );
    }

}
