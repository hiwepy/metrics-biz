package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import com.codahale.metrics.MetricRegistry;

import org.junit.Before;
import org.junit.Test;

public class HttpSessionAttributeMetricsListenerTest {

    private HttpSessionAttributeMetricsListener listener;

    @Before
    public void setUp() {
        listener = new HttpSessionAttributeMetricsListener();
        // Use a fresh registry per test to avoid cross-test contamination
        listener.registry = new MetricRegistry();
    }

    @Test
    public void shouldTrackAttributeAdded() {
        HttpSession session = createMockSession("/app");
        HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, "user", "admin");

        listener.attributeAdded(event);

        String expectedName = MetricRegistry.name(
                HttpSessionAttributeMetricsListener.class, "/app", "session", "attributeAdded");
        assertTrue("Should have registered an attributeAdded meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackAttributeRemoved() {
        HttpSession session = createMockSession("/app");
        HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, "user", "admin");

        listener.attributeRemoved(event);

        String expectedName = MetricRegistry.name(
                HttpSessionAttributeMetricsListener.class, "/app", "session", "attributeRemoved");
        assertTrue("Should have registered an attributeRemoved meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackAttributeReplaced() {
        HttpSession session = createMockSession("/app");
        HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, "user", "oldAdmin");

        listener.attributeReplaced(event);

        String expectedName = MetricRegistry.name(
                HttpSessionAttributeMetricsListener.class, "/app", "session", "attributeReplaced");
        assertTrue("Should have registered an attributeReplaced meter",
                listener.registry.getMeters().containsKey(expectedName));
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
