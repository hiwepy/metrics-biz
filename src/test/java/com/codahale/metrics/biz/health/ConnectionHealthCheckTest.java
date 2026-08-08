package com.codahale.metrics.biz.health;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.sql.Connection;

import com.codahale.metrics.health.HealthCheck;

import org.junit.Test;

public class ConnectionHealthCheckTest {

    @Test
    public void shouldReportHealthyWhenConnectionIsValid() throws Exception {
        Connection mockConnection = createMockConnection(true);
        ConnectionHealthCheck check = new ConnectionHealthCheck(mockConnection, 5);

        HealthCheck.Result result = check.execute();

        assertTrue("Should be healthy when isValid returns true", result.isHealthy());
    }

    @Test
    public void shouldReportUnhealthyWhenConnectionIsInvalid() throws Exception {
        Connection mockConnection = createMockConnection(false);
        ConnectionHealthCheck check = new ConnectionHealthCheck(mockConnection, 5);

        HealthCheck.Result result = check.execute();

        assertFalse("Should be unhealthy when isValid returns false", result.isHealthy());
        assertEquals(" Connection Timeout.", result.getMessage());
    }

    /**
     * Creates a minimal Connection proxy that delegates isValid() to a flag.
     */
    private Connection createMockConnection(boolean valid) throws Exception {
        // Use a dynamic proxy to avoid needing a real database
        return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if ("isValid".equals(method.getName())) {
                        return valid;
                    }
                    if ("close".equals(method.getName())) {
                        return null;
                    }
                    if ("toString".equals(method.getName())) {
                        return "MockConnection";
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );
    }

}
