package com.codahale.metrics.biz.health;

import static org.junit.Assert.*;

import org.junit.Test;

public class TengineHealthStatusCheckTest {

    @Test
    public void shouldReturnNullFromCheck() {
        TengineHealthStatusCheck check = new TengineHealthStatusCheck();

        assertNull("TengineHealthStatusCheck is a placeholder; check() must return null", check.execute());
    }

    @Test
    public void shouldInstantiateWithoutErrors() {
        TengineHealthStatusCheck check = new TengineHealthStatusCheck();
        assertNotNull(check);
    }

}
