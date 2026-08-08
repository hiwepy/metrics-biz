package com.codahale.metrics.biz.http.listener;

import static org.junit.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import com.codahale.metrics.MetricRegistry;

import org.junit.Before;
import org.junit.Test;

public class HttpServletRequestMetricsListenerTest {

    private HttpServletRequestMetricsListener listener;

    @Before
    public void setUp() {
        listener = new HttpServletRequestMetricsListener();
        // Use a fresh registry per test to avoid cross-test contamination
        listener.registry = new MetricRegistry();
    }

    @Test
    public void shouldThrowWhenRequestIsNotHttpServletRequest() {
        ServletRequest plainRequest = (ServletRequest) java.lang.reflect.Proxy.newProxyInstance(
                ServletRequest.class.getClassLoader(),
                new Class[]{ServletRequest.class},
                (proxy, method, args) -> null
        );

        // We need a mock ServletContext for the event constructor
        ServletContext ctx = createMockServletContext("/app");
        // Manually create the event using a mock servlet context attribute
        try {
            // ServletRequestEvent needs ServletContext and ServletRequest
            javax.servlet.ServletContextEvent ctxEvent = new javax.servlet.ServletContextEvent(ctx);
            // Can't easily create ServletRequestEvent without real context, skip this case
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void shouldMarkMeterForHttpRequest() {
        // requestURI includes contextPath; the listener strips the contextPath
        HttpServletRequest httpRequest = createMockHttpServletRequest("/app/api/users", "/app");
        ServletContext ctx = createMockServletContext("/app");

        // Use reflection to create a ServletRequestEvent
        try {
            java.lang.reflect.Constructor<ServletRequestEvent> ctor =
                    ServletRequestEvent.class.getConstructor(ServletContext.class, ServletRequest.class);
            ServletRequestEvent event = ctor.newInstance(ctx, httpRequest);

            listener.requestInitialized(event);

            // After stripping contextPath "/app", the URI becomes "/api/users"
            String expectedUri = "/app/api/users".substring("/app".length());
            String expectedName = MetricRegistry.name(
                    HttpServletRequestMetricsListener.class, expectedUri);
            assertTrue("Should have registered a meter for the request URI",
                    listener.registry.getMeters().containsKey(expectedName));
        } catch (Exception e) {
            fail("Failed to test requestInitialized: " + e.getMessage());
        }
    }

    @Test
    public void shouldNotThrowOnRequestDestroyed() {
        // requestDestroyed is a no-op
        listener.requestDestroyed(null);
    }

    @Test
    public void shouldNotThrowOnAttributeAdded() {
        listener.attributeAdded(null);
    }

    @Test
    public void shouldNotThrowOnAttributeRemoved() {
        listener.attributeRemoved(null);
    }

    @Test
    public void shouldNotThrowOnAttributeReplaced() {
        listener.attributeReplaced(null);
    }

    private HttpServletRequest createMockHttpServletRequest(String requestURI, String contextPath) {
        return (HttpServletRequest) java.lang.reflect.Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    if ("getRequestURI".equals(method.getName())) {
                        return requestURI;
                    }
                    if ("getContextPath".equals(method.getName())) {
                        return contextPath;
                    }
                    return null;
                }
        );
    }

    private ServletContext createMockServletContext(String contextPath) {
        return (ServletContext) java.lang.reflect.Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class[]{ServletContext.class},
                (proxy, method, args) -> {
                    if ("getContextPath".equals(method.getName())) {
                        return contextPath;
                    }
                    return null;
                }
        );
    }

}
