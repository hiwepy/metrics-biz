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
package com.codahale.metrics.biz.health;

import com.codahale.metrics.health.HealthCheck;

/**
 * Dropwizard {@link HealthCheck} that delegates the connectivity probe
 * to a caller-supplied {@link Database} abstraction.
 *
 * <p>This keeps the health check free of any particular JDBC or
 * connection-pool dependency: applications wire whatever "ping" they
 * have (Druid, HikariCP, raw {@code java.sql.Connection}, ...) into a
 * {@link Database} implementation and this class turns the boolean
 * result into the appropriate Dropwizard {@link Result}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class DatabaseHealthCheck extends HealthCheck {

    /**
     * The delegated connectivity probe; injected via the constructor.
     */
    private final Database database;

    /**
     * Creates a new database health check.
     *
     * @param database the delegated connectivity probe.
     */
    public DatabaseHealthCheck(Database database) {
        this.database = database;
    }

    /**
     * Executes the connectivity probe.
     *
     * @return {@link Result#healthy()} when the database reports a
     *         successful ping; otherwise
     *         {@link Result#unhealthy(String)} with a fixed message.
     * @throws Exception if the delegated probe propagates an error.
     */
    @Override
    protected Result check() throws Exception {
        if (database.ping()) {
            return Result.healthy();
        }
        return Result.unhealthy("Can't ping database");
    }

    /**
     * Abstraction that lets applications inject any connectivity check
     * (Druid, Hikari, raw JDBC, etc.) into a {@link DatabaseHealthCheck}.
     */
    public static interface Database {

        /**
         * Probes the underlying database.
         *
         * @return {@code true} if the database is reachable.
         */
        boolean ping();

    }

}