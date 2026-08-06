# metrics-biz

[English](./README.md) | [简体中文](./README.zh-CN.md)

> **项目状态**：`feature/2.0.x` 版本线维护中（JDK 17）。制品尚未发布到 Maven Central，通过项目私服与 GitHub Releases 分发。

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 功能与状态](#2-功能与状态)
- [3. 环境要求与兼容性](#3-环境要求与兼容性)
- [4. 架构与模块](#4-架构与模块)
- [5. 安装](#5-安装)
- [6. 快速开始](#6-快速开始)
- [7. 配置](#7-配置)
- [8. 核心用法 / API](#8-核心用法--api)
- [9. 测试与构建](#9-测试与构建)
- [10. 版本与分支](#10-版本与分支)
- [11. 贡献与许可](#11-贡献与许可)

## 1. 项目概述

`metrics-biz` 是基于 [Dropwizard Metrics](https://metrics.dropwizard.io/)（metrics-core 4.1.1）的业务向辅助层。它通过 `MetricsFactory`（Spring `InitializingBean`/`DisposableBean`，内置 JMX reporter）集中管理 `MetricRegistry`，将指标桥接为 Spring `ApplicationEvent`（`BizMeterEvent`、`BizGaugeEvent` 等），并提供现成的健康检查（数据库、连接、HTTP 服务在线、Nginx/Tengine）与 Servlet 容器的 HTTP 层插桩监听器。

是什么：

- `MetricsFactory`——一站式注册表访问：按类型注册表（`getMeterMetricRegistry()`、`getTimerMetricRegistry()` 等）、类型化工厂（`meter`、`timer`、`counter`、`histogram`、`gauge`）、注册/注销辅助与自动启动的 `JmxReporter`；
- Spring 事件桥——`BizEvent<T>`（Spring `ApplicationEvent`）与 `BizEventPoint` 载荷，以及 `@Async` 监听器基类，事件到达时驱动 meter/gauge/counter/histogram；
- 健康检查——`DatabaseHealthCheck`、`ConnectionHealthCheck`、`HttpServerOnlineCheck`、`ServiceOnlineCheck`、`NginxHealthCheck`、`TengineHealthStatusCheck`；
- HTTP 插桩——Metrics servlet 与健康检查 servlet 的上下文监听器，以及 request/session/session-attribute/session-activation/session-binding 指标监听器。

不是什么：

- 不是 reporter 插件——上报（示例中的 console、Kafka、InfluxDB）由调用方自行选择；
- 不是指标库替代品——它包装 `io.dropwizard.metrics` 4.1.x。

典型场景：

| 场景 | 使用 |
| :--- | :--- |
| 整个应用共用一个注册表 | `MetricsFactory`（Spring Bean 或静态访问器） |
| 通过 Spring 事件计量业务操作 | 发布 `BizMeterEvent`；`BizMeterEventListener` 标记 meter |
| 周期性 JMX 暴露 | `MetricsFactory` 在 `afterPropertiesSet` 时启动 `JmxReporter` |
| 管理端点中的数据库存活检查 | `DatabaseHealthCheck`（`Database.ping()`）、`ConnectionHealthCheck` |
| Servlet 容器中的 Metrics servlet | `MetricsServletContextListener`、`HealthCheckServletContextListener` |

## 2. 功能与状态

| 能力 | 状态 | 说明 |
| :--- | :--- | :--- |
| 中央注册表工厂 | 已实现 | `MetricsFactory`（Spring 生命周期 + 静态访问器 + JMX reporter） |
| 按类型注册表 | 已实现 | `getGauge/Counter/Histogram/Meter/TimerMetricRegistry()`、`getMetricRegistry(String)` |
| 类型化指标工厂 | 已实现 | `MetricsFactory.timer(...)`、`meter(...)`、`counter(...)`、`histogram(...)`、`gauge(...)` |
| Spring 事件桥 | 已实现 | `BizEvent<T>`、`BizCounted/Meter/Gauge/HistogramEvent`、`@Async` 监听器 |
| 健康检查 | 已实现 | 数据库 / 连接 / HTTP 在线 / 服务在线 / Nginx / Tengine |
| Servlet 插桩 | 已实现 | Metrics + 健康检查 servlet 上下文监听器、request/session 指标监听器 |
| `SystemClock` 工具 | 已实现 | `com.codahale.metrics.biz.utils.SystemClock`（now / nowDate） |
| `InstrumentedFilter` | 占位 | `http.filter.InstrumentedFilter` 目前为空占位类 |
| 测试 | 已有 | meter / timer / histogram / counter / gauge / 健康检查的 JUnit 测试 + 上报示例（`KafkaReporterSample`、`InfluxdbTest`） |

## 3. 环境要求与兼容性

| 项目 | 要求 |
| :--- | :--- |
| JDK | 17+ |
| Maven | 3.0+（内置 Maven Wrapper `mvnw`） |
| 依赖 | metrics-core 4.1.1、spring-context、javax.servlet-api、slf4j-api 2.0.18、lombok（provided）；junit 4.13.2（测试） |

版本线：

| 分支 | JDK | 版本模式 |
| :--- | :---: | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` |
| `feature/2.0.x` | 17 | `2.0.x.*` |
| `feature/3.0.x` | 21 | `3.0.x.*` |

## 4. 架构与模块

```text
应用 / Spring 上下文
        |
        +--> MetricsFactory (注册表 + JmxReporter)
        |       |
        |       +--> MetricRegistry (全局或按类型)
        |
        +--> Biz*Event (Spring ApplicationEvent) --> @Async Biz*EventListener
        |                                                 |--> metrics-core 4.1.1
        |
        +--> HealthCheck (Database / Connection / Online / Nginx / Tengine)
        |       `--> metrics-healthchecks
        |
        `--> Servlet 监听器 (MetricsServlet, HealthCheckServlet,
              request / session 指标) --> javax.servlet
```

单模块 jar。`com.codahale.metrics.biz` 下的包结构：

| 包 | 内容 |
| :--- | :--- |
| `com.codahale.metrics.biz` | `MetricsFactory` |
| `com.codahale.metrics.biz.event` | `BizEvent<T>`、`BizEventPoint`、`BizCountedEvent`、`BizMeterEvent`、`BizGaugeEvent`、`BizHistogramEvent` |
| `com.codahale.metrics.biz.event.listener` | `BizMetricEventListener`、`BizCounted/Meter/Gauge/HistogramEventListener`（`@Async`、`@Component`） |
| `com.codahale.metrics.biz.health` | `DatabaseHealthCheck`、`ConnectionHealthCheck`、`HttpServerOnlineCheck`、`ServiceOnlineCheck`、`NginxHealthCheck`、`TengineHealthStatusCheck` |
| `com.codahale.metrics.biz.http` | `MetricsServletContextListener`、`HealthCheckServletContextListener`、request/session 指标监听器、`InstrumentedFilter`（占位） |
| `com.codahale.metrics.biz.utils` | `SystemClock` |
| `com.codahale.metrics.biz.filter` | `MetricNamedFilter` |

## 5. 安装

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>metrics-biz</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:metrics-biz:2.0.x.x.20260630-SNAPSHOT'
```

快照版本由项目私服提供（见 pom 中 `distributionManagement`）。尚未发布 Maven Central 正式版。

## 6. 快速开始

通过静态工厂获取类型化指标，并用 Spring 事件计量业务操作：

```java
import com.codahale.metrics.Meter;
import com.codahale.metrics.biz.MetricsFactory;
import com.codahale.metrics.biz.event.BizMeterEvent;

// 注册表支撑的 meter（按类型注册表：getMeterMetricRegistry）
Meter requests = MetricsFactory.meter(MetricsSpringTest.class, "request");
requests.mark();

// 或发布 Spring 事件；BizMeterEventListener 异步标记 meter
applicationEventPublisher.publishEvent(new BizMeterEvent(source, "order.created", "order 10001 created"));
```

将 `MetricsFactory` 声明为 Spring Bean（`afterPropertiesSet` 启动 `JmxReporter`；`destroy` 停止），或直接使用静态 `get*MetricRegistry()` 访问器。

## 7. 配置

无属性文件配置。选项在代码中设置：

- `MetricsFactory.setRegistry(MetricRegistry)` 绑定自定义注册表；
- JMX reporter 对配置的注册表自动启动（`afterPropertiesSet` 启动，`destroy` 停止）；
- `BizEventPoint` 携带 `name`、`message`、`value`、`timestamp`（来自 `SystemClock`）与 `data` map，可针对每种事件类型定制监听器；
- `MetricNamedFilter` 按名称过滤指标，供 `removeMatching` 使用。

## 8. 核心用法 / API

### 8.1 健康检查

```java
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.biz.health.DatabaseHealthCheck;

HealthCheckRegistry healthChecks = new HealthCheckRegistry();
healthChecks.register("database", new DatabaseHealthCheck(() -> true)); // Database.ping()

// 连接 / 服务在线等变体遵循同一 HealthCheck 契约：
// Result.healthy() / Result.unhealthy(message)
```

### 8.2 Servlet 指标监听器

```xml
<listener>
    <listener-class>com.codahale.metrics.biz.http.listener.MetricsServletContextListener</listener-class>
</listener>
<listener>
    <listener-class>com.codahale.metrics.biz.http.listener.HealthCheckServletContextListener</listener-class>
</listener>
```

`MetricsServletContextListener` 从 `MetricsFactory.getContextMetricRegistry()` 提供 Metrics servlet；request / session 监听器（`HttpServletRequestMetricsListener`、`HttpSessionMetricsListener` 等）对 servlet 生命周期事件插桩。

## 9. 测试与构建

```bash
./mvnw clean verify
```

构建配置：

- JUnit 4 + Maven Surefire；测试覆盖 meters、timers、histograms、counters、gauges 与健康检查（`MetricsMetersTest`、`MetricsTimersTest`、`MetricsHistogramsTest`、`MetricsCounterTest`、`MetricsGaugesTest`、`MetricsHealthCheckTest`），另有上报示例（`KafkaReporterSample`、`MetricsKafkaConsumerSample`、`InfluxdbTest`）；
- JaCoCo 覆盖率报告 + 行覆盖率检查规则，最低目标 90%（`haltOnFailure=false`）；
- package 阶段附加源码包与 Javadoc 包；
- 提供 `central` 发布 profile（GPG 签名 + Central 发布插件），仅用于正式发布。

## 10. 版本与分支

三条并行版本线，各自绑定一个 JDK 基线：

| 分支 | JDK | 版本模式 | 维护状态 |
| :--- | :---: | :--- | :--- |
| `feature/1.0.x` | 8 | `1.0.x.*` | 当前开发线 |
| `feature/2.0.x` | 17 | `2.0.x.*` | 并行维护 |
| `feature/3.0.x` | 21 | `3.0.x.*` | 并行维护 |

本分支快照版本为 `2.0.x.x.20260630-SNAPSHOT`。

## 11. 贡献与许可

欢迎通过 GitHub Issue 或 Pull Request 参与贡献。所有源码基于 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) 许可。
