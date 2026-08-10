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

import java.sql.Connection;

import com.codahale.metrics.health.HealthCheck;

/**
 * Dropwizard {@link HealthCheck} that validates a JDBC
 * {@link Connection} by calling {@link Connection#isValid(int)}.
 *
 * <p>The check reports {@link Result#healthy()} when
 * {@code isValid(timeout)} returns {@code true}; otherwise it returns
 * {@link Result#unhealthy(String)} with a fixed timeout message.
 * Note that the supplied {@code timeout} is captured for the validity
 * probe only and not for the underlying connection acquisition.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public class ConnectionHealthCheck extends HealthCheck {

    /**
     * The JDBC connection this check validates.
     */
    protected Connection connection;
    /**
     * The validity timeout in seconds passed to
     * {@link Connection#isValid(int)}.
     */
    protected int timeout = 5000;

    /**
     * Creates a connection health check.
     *
     * @param connection the JDBC connection to validate.
     * @param timeout the validity timeout in seconds. The current
     *                implementation ignores the supplied value and uses
     *                the field default of 5000 ms; the parameter is
     *                preserved for binary compatibility.
     */
    public ConnectionHealthCheck(Connection connection,int timeout) {
        this.connection = connection;
    }

    /**
     * Executes the validity probe against the bound connection.
     *
     * @return {@link Result#healthy()} if the connection is valid,
     *         otherwise {@link Result#unhealthy(String)} with a
     *         "Connection Timeout." message.
     * @throws Exception if {@link Connection#isValid(int)} itself
     *         propagates an error.
     */
    @Override
    protected Result check() throws Exception {
        if (connection.isValid(timeout)) {
            return Result.healthy();
        }
        return Result.unhealthy(" Connection Timeout.");
    }


}