/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.codahale.metrics.biz.event.listener;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.codahale.metrics.MetricRegistry;

public abstract class BizMetricEventListener<E extends ApplicationEvent> implements ApplicationListener<E>, InitializingBean {

	/**
     * 实例化一个registry，最核心的一个模块，相当于一个应用程序的metrics系统的容器，维护一个Map
     */
	protected MetricRegistry registry;
	
	protected long initialDelay = 0; 
	
	protected long period = 1;
	
	protected TimeUnit unit = TimeUnit.SECONDS;
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	public MetricRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(MetricRegistry registry) {
		this.registry = registry;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
	
}
