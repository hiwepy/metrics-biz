package com.codahale.metrics.biz;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Singleton class to manage metrics.
 */
public class MetricsFactory implements InitializingBean, DisposableBean {

	public static final String SERVLET_CONTEXT_METRIC_REGISTRY = "http-servlet-context-metric-registry";
	
	public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();
	public static final MetricRegistry DEFAULT_REGISTRY = new MetricRegistry();

	protected MetricRegistry registry = DEFAULT_REGISTRY;
	protected JmxReporter jmxReporter;
	protected ConcurrentHashMap<String, Metric> hashMap;
	protected static final Logger LOG = LoggerFactory.getLogger(MetricsFactory.class);
	public MetricsFactory() {
		
	}

	public void afterPropertiesSet() throws Exception {
		
		hashMap = new ConcurrentHashMap<String, Metric>();
		jmxReporter = JmxReporter.forRegistry(registry).build();
		jmxReporter.start();
		
	}

	public void destroy() throws Exception {
		jmxReporter.stop();
	}

	public void setRegistry(MetricRegistry registry) {
		this.registry = registry;
	}

	public MetricRegistry getRegistry() {
		return registry;
	}

	/**
	 * 实例化一个Timer
	 */
	public Timer getTimer(Class<?> clazz, String name) {
		return getMetric(Timer.class, clazz, name);
	}

	/**
	 * 实例化一个Histograms
	 */
	public Histogram getHistogram(Class<?> clazz, String name) {
		return getMetric(Histogram.class, clazz, name);
	}

	/**
	 * 实例化一个counter,同样可以通过如下方式进行实例化再注册进去 Counter jobs = new Counter();
	 * metrics.register(MetricRegistry.name(TestCounter.class, "jobs"), jobs);
	 */
	public Counter getCounter(Class<?> clazz, String name) {
		return getMetric(Counter.class, clazz, name);
	}

	/**
	 * 实例化一个Meter
	 */
	public Meter getMeter(Class<?> clazz, String name) {
		return getMetric(Meter.class, clazz, name);
	}

	@SuppressWarnings("unchecked")
	private <T> T getMetric(Class<T> metricClass, Class<?> clazz, String name) {
		String key = metricClass.getName() + clazz.getName() + name;
		Metric metric = hashMap.get(key);
		if (metric == null) {
			if (metricClass == Histogram.class) {
				metric = this.getRegistry().histogram(MetricRegistry.name(clazz, name));
			}
			if (metricClass == Timer.class) {
				metric = this.getRegistry().timer(MetricRegistry.name(clazz, name));
			}
			if (metricClass == Meter.class) {
				metric = this.getRegistry().meter(MetricRegistry.name(clazz, name));
			}
			if (metricClass == Counter.class) {
				metric = this.getRegistry().counter(MetricRegistry.name(clazz, name));
			}
			hashMap.put(key, metric);
		}
		return (T) metric;
	}

	/**
	 * 实例化一个专用于ServletContext的registry
	 */
	public static MetricRegistry getServletContextMetricRegistry() {
		return SharedMetricRegistries.getOrCreate(SERVLET_CONTEXT_METRIC_REGISTRY);
	}

	/**
	 * 实例化一个专用于仪表的registry
	 */
	public static MetricRegistry getGaugeMetricRegistry() {
		return SharedMetricRegistries.getOrCreate("Gauges");
	}

	/**
	 * 实例化一个专用于计数器的registry
	 */
	public static MetricRegistry getCounterMetricRegistry() {
		return SharedMetricRegistries.getOrCreate("Counters");
	}

	/**
	 * 实例化一个专用于直方图的registry
	 */
	public static MetricRegistry getHistogramMetricRegistry() {
		return SharedMetricRegistries.getOrCreate("Histograms");
	}

	/**
	 * 实例化一个专用于速率的registry
	 */
	public static MetricRegistry getMeterMetricRegistry() {
		return SharedMetricRegistries.getOrCreate("Meters");
	}

	/**
	 * 实例化一个专用于计时器的registry
	 */
	public static MetricRegistry getTimerMetricRegistry() {
		return SharedMetricRegistries.getOrCreate("Timers");
	}

	/**
	 * 实例化一个registry，最核心的一个模块，相当于一个应用程序的metrics系统的容器，维护一个Map
	 */
	public static MetricRegistry getMetricRegistry(String metrics) {
		return SharedMetricRegistries.getOrCreate(metrics);
	}
	
	/**
	 * 实例化一个Histograms
	 */
	public static <T> Histogram histogram(Class<T> clazz, String name) {
		return getHistogramMetricRegistry().histogram(MetricRegistry.name(clazz, name));
	}

	/**
	 * 实例化一个Timer
	 */
	public static <T> Timer timer(Class<T> clazz, String name) {
		return getTimerMetricRegistry().timer(MetricRegistry.name(clazz, name));
	}


	/**
	 * 实例化一个counter,同样可以通过如下方式进行实例化再注册进去 Counter jobs = new Counter();
	 * metrics.register(MetricRegistry.name(TestCounter.class, "jobs"), jobs);
	 */
	public static <T> Counter counter(Class<?> clazz, String name) {
		return getCounterMetricRegistry().counter(MetricRegistry.name(clazz, name));
	}

	/**
	 * 实例化一个Meter
	 */
	public static <T> Meter meter(Class<?> clazz, String name) {
		return getMeterMetricRegistry().meter(MetricRegistry.name(clazz, name));
	}

}
