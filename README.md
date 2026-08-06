# metrics-biz

[Overview](#1-project-overview) | [Features](#2-features--status) | [Requirements](#3-requirements--compatibility) | [Architecture](#4-architecture--modules) | [Installation](#5-installation) | [Quick Start](#6-quick-start) | [Configuration](#7-configuration) | [Core Usage](#8-core-usage--api) | [Testing & Build](#9-testing--build) | [Versioning](#10-versioning--branches) | [License](#11-contributing--license)

> **Status**: maintained on the `feature/1.0.x` line (JDK 8). Artifacts are not yet published to Maven Central; they are distributed through the project's private repository and GitHub Releases.

## 1. Project Overview

`metrics-biz` is a business-oriented helper layer on top of [Dropwizard Metrics](https://metrics.dropwizard.io/) (metrics-core 4.1.1). It centralizes `MetricRegistry` access through a `MetricsFactory` (Spring `InitializingBean`/`DisposableBean`, with a built-in JMX reporter), bridges metrics into Spring `ApplicationEvent`s (`BizMeterEvent`, `BizGaugeEvent`, ...), provides ready-made health checks (database, connection, HTTP service online, Nginx/Tengine) and HTTP-layer instrumented listeners for servlet containers.

What it is:

- `MetricsFactory` — one-stop registry access: per-type registries (`getMeterMetricRegistry()`, `getTimerMetricRegistry()`, ...), typed factories (`meter`, `timer`, `counter`, `histogram`, `gauge`), registration/unregistration helpers and an auto-started `JmxReporter`;
- Spring event bridge — `BizEvent<T>` (a Spring `ApplicationEvent`) with `BizEventPoint` payloads, and `@Async` listener base classes that mark meters/gauges/counters/histograms on events;
- Health checks — `DatabaseHealthCheck`, `ConnectionHealthCheck`, `HttpServerOnlineCheck`, `ServiceOnlineCheck`, `NginxHealthCheck`, `TengineHealthStatusCheck`;
- HTTP instrumentation — servlet context listeners for the Metrics servlet and health-check servlet, plus request/session/session-attribute/session-activation/session-binding metrics listeners.

What it is not:

- Not a reporter plugin — reporting (console, Kafka, InfluxDB in the samples) stays the caller's choice;
- Not a metrics library replacement — it wraps `io.dropwizard.metrics` 4.1.x.

Typical scenarios:

| Scenario | What to use |
| :--- | :--- |
| One registry for the whole application | `MetricsFactory` (Spring bean or static accessors) |
| Meter a business operation via Spring events | Publish `BizMeterEvent`; `BizMeterEventListener` marks the meter |
| Periodic JMX exposure | `MetricsFactory` starts a `JmxReporter` on `afterPropertiesSet` |
| Database liveness in admin endpoints | `DatabaseHealthCheck` (`Database.ping()`), `ConnectionHealthCheck` |
| Metrics servlet in a servlet container | `MetricsServletContextListener`, `HealthCheckServletContextListener` |

## 2. Features & Status

| Capability | Status | Notes |
| :--- | :--- | :--- |
| Central registry factory | Implemented | `MetricsFactory` (Spring lifecycle + static accessors + JMX reporter) |
| Per-type registries | Implemented | `getGauge/Counter/Histogram/Meter/TimerMetricRegistry()`, `getMetricRegistry(String)` |
| Typed metric factories | Implemented | `MetricsFactory.timer(...)`, `meter(...)`, `counter(...)`, `histogram(...)`, `gauge(...)` |
| Spring event bridge | Implemented | `BizEvent<T>`, `BizCounted/Meter/Gauge/HistogramEvent`, `@Async` listeners |
| Health checks | Implemented | database / connection / HTTP-online / service-online / Nginx / Tengine |
| Servlet instrumentation | Implemented | Metrics + health servlet context listeners, request/session metrics listeners |
| `SystemClock` utility | Implemented | `com.codahale.metrics.biz.utils.SystemClock` (now / nowDate) |
| `InstrumentedFilter` | Stub | `http.filter.InstrumentedFilter` is currently an empty placeholder class |
| Tests | Present | JUnit tests for meters / timers / histograms / counters / gauges / health checks + reporter samples (`KafkaReporterSample`, `InfluxdbTest`) |

## 3. Requirements & Compatibility

| Item | Requirement |
| :--- | :--- |
| JDK | 8+ |
| Maven | 3.0+ (Maven Wrapper `mvnw` included) |
| Dependencies | metrics-core 4.1.1, spring-context, javax.servlet-api, slf4j-api 2.0.18, lombok (provided); junit 4.13.2 (test) |

Version lines:

| Branch | JDK | Version pattern |
| :--- | :---: | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. Architecture & Modules

```text
Application / Spring context
        |
        +--> MetricsFactory (registry + JmxReporter)
        |       |
        |       +--> MetricRegistry (global or per-type)
        |
        +--> Biz*Event (Spring ApplicationEvent) --> @Async Biz*EventListener
        |                                                 |--> metrics-core 4.1.1
        |
        +--> HealthCheck (Database / Connection / Online / Nginx / Tengine)
        |       `--> metrics-healthchecks
        |
        `--> Servlet listeners (MetricsServlet, HealthCheckServlet,
              request / session metrics) --> javax.servlet
```

Single-module jar. Packages under `com.codahale.metrics.biz`:

| Package | Contents |
| :--- | :--- |
| `com.codahale.metrics.biz` | `MetricsFactory` |
| `com.codahale.metrics.biz.event` | `BizEvent<T>`, `BizEventPoint`, `BizCountedEvent`, `BizMeterEvent`, `BizGaugeEvent`, `BizHistogramEvent` |
| `com.codahale.metrics.biz.event.listener` | `BizMetricEventListener`, `BizCounted/Meter/Gauge/HistogramEventListener` (`@Async`, `@Component`) |
| `com.codahale.metrics.biz.health` | `DatabaseHealthCheck`, `ConnectionHealthCheck`, `HttpServerOnlineCheck`, `ServiceOnlineCheck`, `NginxHealthCheck`, `TengineHealthStatusCheck` |
| `com.codahale.metrics.biz.http` | `MetricsServletContextListener`, `HealthCheckServletContextListener`, request/session metrics listeners, `InstrumentedFilter` (stub) |
| `com.codahale.metrics.biz.utils` | `SystemClock` |
| `com.codahale.metrics.biz.filter` | `MetricNamedFilter` |

## 5. Installation

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>metrics-biz</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:metrics-biz:1.0.x.20260630-SNAPSHOT'
```

The snapshot is served from the project's private repository (see `distributionManagement` in the pom). No Maven Central release is available yet.

## 6. Quick Start

Get a typed metric through the static factories and use a Spring event to meter a business action:

```java
import com.codahale.metrics.Meter;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizMeterEvent;

// registry-backed meter (per-type registry: getMeterMetricRegistry)
Meter requests = MetricsFactory.meter(MetricsSpringTest.class, "request");
requests.mark();

// or publish a Spring event; BizMeterEventListener marks the meter asynchronously
applicationEventPublisher.publishEvent(new BizMeterEvent(source, "order.created", "order 10001 created"));
```

Declare `MetricsFactory` as a Spring bean (`afterPropertiesSet` starts the `JmxReporter`; `destroy` stops it), or use the static `get*MetricRegistry()` accessors directly.

## 7. Configuration

No property-file configuration. Options are set in code:

- `MetricsFactory.setRegistry(MetricRegistry)` to bind a custom registry;
- the JMX reporter is started automatically for the configured registry (started in `afterPropertiesSet`, stopped in `destroy`);
- `BizEventPoint` carries `name`, `message`, `value`, `timestamp` (from `SystemClock`) and a `data` map, so listeners can be customized per event type;
- `MetricNamedFilter` filters metrics by name for `removeMatching`.

## 8. Core Usage / API

### 8.1 Health checks

```java
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.biz.health.DatabaseHealthCheck;

HealthCheckRegistry healthChecks = new HealthCheckRegistry();
healthChecks.register("database", new DatabaseHealthCheck(() -> true)); // Database.ping()

// connection / service-online variants follow the same HealthCheck contract:
// Result.healthy() / Result.unhealthy(message)
```

### 8.2 Servlet metrics listeners

```xml
<listener>
    <listener-class>com.codahale.metrics.biz.http.listener.MetricsServletContextListener</listener-class>
</listener>
<listener>
    <listener-class>com.codahale.metrics.biz.http.listener.HealthCheckServletContextListener</listener-class>
</listener>
```

`MetricsServletContextListener` serves the Metrics servlet from `MetricsFactory.getContextMetricRegistry()`; request / session listeners (`HttpServletRequestMetricsListener`, `HttpSessionMetricsListener`, ...) instrument servlet lifecycle events.

## 9. Testing & Build

```bash
./mvnw clean verify
```

The build is configured with:

- JUnit 4 + Maven Surefire; tests cover meters, timers, histograms, counters, gauges and health checks (`MetricsMetersTest`, `MetricsTimersTest`, `MetricsHistogramsTest`, `MetricsCounterTest`, `MetricsGaugesTest`, `MetricsHealthCheckTest`), plus reporter samples (`KafkaReporterSample`, `MetricsKafkaConsumerSample`, `InfluxdbTest`);
- JaCoCo coverage reporting plus a line-coverage check rule with a 90% minimum target (`haltOnFailure=false`);
- Source and Javadoc jars attached at package time;
- a `central` release profile (GPG signing + Central publishing) reserved for official releases.

## 10. Versioning & Branches

Three parallel version lines, each bound to a JDK baseline:

| Branch | JDK | Version pattern | Maintenance |
| :--- | :---: | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | Current development line |
| `feature/2.0.x` | 17 | `2.0.x.*` | Maintained in parallel |
| `feature/3.0.x` | 21 | `3.0.x.*` | Maintained in parallel |

Snapshots on this branch are versioned `1.0.x.20260630-SNAPSHOT`.

## 11. Contributing & License

Contributions are welcome — open an issue or pull request on GitHub. All source files are licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).
