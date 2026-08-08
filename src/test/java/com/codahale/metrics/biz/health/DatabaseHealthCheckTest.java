package com.codahale.metrics.biz.health;

import static org.junit.Assert.*;

import com.codahale.metrics.health.HealthCheck;

import org.junit.Test;

public class DatabaseHealthCheckTest {

    @Test
    public void shouldReportHealthyWhenPingSucceeds() {
        DatabaseHealthCheck.Database db = () -> true;
        DatabaseHealthCheck check = new DatabaseHealthCheck(db);

        HealthCheck.Result result = check.execute();

        assertTrue("Should be healthy when ping returns true", result.isHealthy());
    }

    @Test
    public void shouldReportUnhealthyWhenPingFails() {
        DatabaseHealthCheck.Database db = () -> false;
        DatabaseHealthCheck check = new DatabaseHealthCheck(db);

        HealthCheck.Result result = check.execute();

        assertFalse("Should be unhealthy when ping returns false", result.isHealthy());
        assertEquals("Can't ping database", result.getMessage());
    }

    @Test
    public void shouldPropagateExceptionFromPing() {
        DatabaseHealthCheck.Database db = () -> {
            throw new RuntimeException("Connection refused");
        };
        DatabaseHealthCheck check = new DatabaseHealthCheck(db);

        HealthCheck.Result result = check.execute();

        assertFalse("Should be unhealthy when ping throws", result.isHealthy());
        assertNotNull(result.getError());
    }

    @Test
    public void shouldAcceptDatabaseInterface() {
        DatabaseHealthCheck.Database db = new DatabaseHealthCheck.Database() {
            @Override
            public boolean ping() {
                return true;
            }
        };
        DatabaseHealthCheck check = new DatabaseHealthCheck(db);

        assertNotNull(check);
        assertTrue(check.execute().isHealthy());
    }

}
