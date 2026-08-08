package com.codahale.metrics.biz.http.filter;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstrumentedFilterTest {

    @Test
    public void shouldInstantiateWithoutErrors() {
        InstrumentedFilter filter = new InstrumentedFilter();
        assertNotNull(filter);
    }

}
