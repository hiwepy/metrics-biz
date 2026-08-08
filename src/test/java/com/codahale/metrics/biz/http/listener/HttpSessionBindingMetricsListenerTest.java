package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import com.codahale.metrics.MetricRegistry;

import org.junit.Before;
import org.junit.Test;

public class HttpSessionBindingMetricsListenerTest {

    private HttpSessionBindingMetricsListener listener;

    @Before
    public void setUp() {
        listener = new HttpSessionBindingMetricsListener();
        // Use a fresh registry per test to avoid cross-test contamination
        listener.registry = new MetricRegistry();
    }

    @Test
    public void shouldTrackValueBound() {
        HttpSession session = createMockSession("/app");
        HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, "user", "admin");

        listener.valueBound(event);

        String expectedName = MetricRegistry.name(
                HttpSessionBindingMetricsListener.class, "/app", "session", "valueBound");
        assertTrue("Should have registered a valueBound meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackValueUnbound() {
        HttpSession session = createMockSession("/app");
        HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, "user", "admin");

        listener.valueUnbound(event);

        String expectedName = MetricRegistry.name(
                HttpSessionBindingMetricsListener.class, "/app", "session", "valueUnbound");
        assertTrue("Should have registered a valueUnbound meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldMarkMeterMultipleTimes() {
        HttpSession session = createMockSession("/app");
        HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, "user", "admin");

        listener.valueBound(event);
        listener.valueBound(event);
        listener.valueBound(event);

        String expectedName = MetricRegistry.name(
                HttpSessionBindingMetricsListener.class, "/app", "session", "valueBound");
        assertEquals("Meter should have been marked three times", 3,
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
