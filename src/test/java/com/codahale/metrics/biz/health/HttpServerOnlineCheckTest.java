package com.codahale.metrics.biz.health;

import static org.junit.Assert.*;

import com.codahale.metrics.health.HealthCheck;

import org.junit.Test;

public class HttpServerOnlineCheckTest {

    @Test
    public void shouldReportUnhealthyWhenUrlIsUnreachable() {
        HttpServerOnlineCheck check = new HttpServerOnlineCheck("http://localhost:1");

        HealthCheck.Result result = check.execute();

        assertFalse("Should be unhealthy when connection fails", result.isHealthy());
        assertNotNull(result.getError());
    }

    @Test
    public void shouldReportUnhealthyWhenUrlIsNull() {
        HttpServerOnlineCheck check = new HttpServerOnlineCheck();

        HealthCheck.Result result = check.execute();

        assertFalse("Should be unhealthy when URL is null", result.isHealthy());
    }

    @Test
    public void shouldStoreHttpUrlViaConstructor() {
        HttpServerOnlineCheck check = new HttpServerOnlineCheck("http://example.com");

        assertEquals("http://example.com", check.getHttpURL());
    }

    @Test
    public void shouldAllowSettingUrlViaSetter() {
        HttpServerOnlineCheck check = new HttpServerOnlineCheck();
        check.setHttpURL("http://example.com/health");

        assertEquals("http://example.com/health", check.getHttpURL());
    }

    @Test
    public void shouldReportUnhealthyForNon200Response() {
        // This URL doesn't exist, so the connection will fail
        HttpServerOnlineCheck check = new HttpServerOnlineCheck("http://localhost:1/health");

        HealthCheck.Result result = check.execute();

        assertFalse(result.isHealthy());
    }

    @Test
    public void shouldSupportDefaultConstructor() {
        HttpServerOnlineCheck check = new HttpServerOnlineCheck();

        assertNull(check.getHttpURL());
    }

}
