/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codahale.metrics.biz.http.listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

/**
 * Dropwizard servlet {@link HealthCheckServlet.ContextListener} that
 * exposes the application-wide {@link HealthCheckRegistry} owned by
 * {@link MetricsFactory#getContextHealthCheckRegistry()}.
 *
 * <p>The listener also overrides {@link #getExecutorService()} to
 * supply a {@link Executors#newScheduledThreadPool(int) scheduled
 * thread pool} so that the {@code HealthCheckServlet} can dispatch
 * background probes without blocking the request thread.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see <a href="http://blog.csdn.net/bairrfhoinn/article/details/16848785">Original CSDN post</a>
 */
public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener{

    /**
     * Returns the application-wide {@link HealthCheckRegistry} that the
     * Dropwizard servlet should serve.
     *
     * @return the registry from {@link MetricsFactory#getContextHealthCheckRegistry()}.
     */
    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return MetricsFactory.getContextHealthCheckRegistry();
    }

    /**
     * Returns a scheduled executor service that the
     * {@code HealthCheckServlet} uses to run health checks concurrently.
     *
     * @return a ten-thread scheduled executor service.
     */
    @Override
    protected ExecutorService getExecutorService() {
        return Executors.newScheduledThreadPool(10);
    }

}