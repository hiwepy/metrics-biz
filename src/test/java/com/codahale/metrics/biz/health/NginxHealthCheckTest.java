package com.codahale.metrics.biz.health;

import static org.junit.Assert.*;

import org.junit.Test;

public class NginxHealthCheckTest {

    @Test
    public void shouldReturnNullFromCheck() {
        NginxHealthCheck check = new NginxHealthCheck();

        assertNull("NginxHealthCheck is a placeholder; check() must return null", check.execute());
    }

    @Test
    public void shouldInstantiateWithoutErrors() {
        NginxHealthCheck check = new NginxHealthCheck();
        assertNotNull(check);
    }

}
