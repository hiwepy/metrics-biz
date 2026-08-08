package com.codahale.metrics.biz.health;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServiceOnlineCheckTest {

    @Test
    public void shouldReturnNullFromCheck() {
        ServiceOnlineCheck check = new ServiceOnlineCheck();

        assertNull("ServiceOnlineCheck is a placeholder; check() must return null", check.execute());
    }

    @Test
    public void shouldInstantiateWithoutErrors() {
        ServiceOnlineCheck check = new ServiceOnlineCheck();
        assertNotNull(check);
    }

}
