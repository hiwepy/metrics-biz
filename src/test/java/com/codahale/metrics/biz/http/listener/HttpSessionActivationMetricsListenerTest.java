package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import com.codahale.metrics.MetricRegistry;

import org.junit.Before;
import org.junit.Test;

public class HttpSessionActivationMetricsListenerTest {

    private HttpSessionActivationMetricsListener listener;

    @Before
    public void setUp() {
        listener = new HttpSessionActivationMetricsListener();
        // Use a fresh registry per test to avoid cross-test contamination
        listener.registry = new MetricRegistry();
    }

    @Test
    public void shouldTrackSessionWillPassivate() {
        HttpSession session = createMockSession("/app");
        HttpSessionEvent event = new HttpSessionEvent(session);

        listener.sessionWillPassivate(event);

        String expectedName = MetricRegistry.name(
                HttpSessionActivationMetricsListener.class, "/app", "session", "willPassivate");
        assertTrue("Should have registered a willPassivate meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldTrackSessionDidActivate() {
        HttpSession session = createMockSession("/app");
        HttpSessionEvent event = new HttpSessionEvent(session);

        listener.sessionDidActivate(event);

        String expectedName = MetricRegistry.name(
                HttpSessionActivationMetricsListener.class, "/app", "session", "didActivate");
        assertTrue("Should have registered a didActivate meter",
                listener.registry.getMeters().containsKey(expectedName));
    }

    @Test
    public void shouldMarkMeterMultipleTimes() {
        HttpSession session = createMockSession("/app");
        HttpSessionEvent event = new HttpSessionEvent(session);

        listener.sessionWillPassivate(event);
        listener.sessionWillPassivate(event);

        String expectedName = MetricRegistry.name(
                HttpSessionActivationMetricsListener.class, "/app", "session", "willPassivate");
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
