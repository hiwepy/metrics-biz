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

import java.net.HttpURLConnection;
import java.net.URL;

import com.codahale.metrics.health.HealthCheck;

/**
 * Dropwizard {@link HealthCheck} that confirms a remote HTTP endpoint
 * answers with a {@code 200 OK} response.
 *
 * <p>The check opens an {@link HttpURLConnection}, calls the protected
 * {@link #onPreHandle(HttpURLConnection)} hook to allow subclasses to
 * customise headers and timeouts, and returns the appropriate
 * {@link Result} based on the HTTP response code. Any thrown exception
 * (DNS failure, timeout, ...) is converted into an unhealthy result
 * carrying the original error.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see <a href="http://blog.csdn.net/paullmq/article/details/9032631">Original CSDN post</a>
 */
public class HttpServerOnlineCheck extends HealthCheck {

    /**
     * The HTTP URL probed by this health check. Configurable via
     * {@link #setHttpURL(String)} or {@link #HttpServerOnlineCheck(String)}.
     */
    protected String httpURL;

    /**
     * Default constructor for reflective instantiation by Dropwizard's
     * {@code HealthCheckRegistry}. Callers must subsequently call
     * {@link #setHttpURL(String)}.
     */
    public HttpServerOnlineCheck() {
    }

    /**
     * Creates a check that probes {@code httpURL}.
     *
     * @param httpURL the HTTP URL to probe.
     */
    public HttpServerOnlineCheck(String httpURL) {
        this.httpURL = httpURL;
    }

    /**
     * Executes the HTTP probe.
     *
     * @return {@link Result#healthy()} when the remote endpoint answers
     *         with HTTP 200; {@link Result#unhealthy(String)} carrying the
     *         response message when it answers otherwise; and
     *         {@link Result#unhealthy(Throwable)} when the connection
     *         itself fails.
     * @throws Exception propagated from
     *         {@link HttpURLConnection#connect()} or
     *         {@link #onPreHandle(HttpURLConnection)}.
     */
    @Override
    protected Result check() throws Exception {
        try {
            URL url = new URL(getHttpURL());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            this.onPreHandle(conn);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                return HealthCheck.Result.healthy();
            }
            return HealthCheck.Result.unhealthy(conn.getResponseMessage());
        } catch (Exception error) {
            return HealthCheck.Result.unhealthy(error);
        }
    }

    /**
     * Configures the supplied connection with sensible defaults: a five
     * second connect timeout, a three second read timeout, a {@code GET}
     * method, input enabled and a UTF-8 {@code Charset} header.
     * Subclasses may override this hook to add headers or change
     * timeouts.
     *
     * @param conn the freshly opened connection to configure.
     * @throws Exception propagated from any of the setter calls.
     */
    protected void onPreHandle(HttpURLConnection conn) throws Exception {
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        //configure the request charset
        conn.setRequestProperty("Charset", "UTF-8");
    }

    /**
     * Returns the URL probed by this health check.
     *
     * @return the configured URL; may be {@code null}.
     */
    public String getHttpURL() {
        return httpURL;
    }

    /**
     * Replaces the URL probed by this health check.
     *
     * @param httpURL the new URL.
     */
    public void setHttpURL(String httpURL) {
        this.httpURL = httpURL;
    }

}