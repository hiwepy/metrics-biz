package com.codahale.metrics.biz.http.listener;

import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener{

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return MetricsFactory.HEALTH_CHECK_REGISTRY;
    }
    
}
