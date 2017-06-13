package com.codahale.metrics.biz.event;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.biz.utils.SystemClock;

/**
 * 
 * @className	： BizEventPoint
 * @description	： 业务事件源数据
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年6月9日 下午5:23:26
 * @version 	V1.0
 */
public class BizEventPoint {
	
	public static final BizEventPoint ROOT = new BizEventPoint("root" , "Event Source");
	
	/**
	 * 上一个事件
	 */
	protected BizEventPoint prev;
	/**
	 * 当前事件UID
	 */
	protected String uid;
	/**
	 * 度量名称
	 */
	protected String metric;
	/**
	 * 当前事件发生时间
	 */
	protected long timestamp;
	/**
	 * 当前事件消息
	 */
	protected String message;
	/**
	 * 当前峰值
	 */
	protected Long value;
	/**
	 * 当前事件绑定数据
	 */
	protected Map<String, Object> data;
	
	public BizEventPoint(String uid, String message) {
		this(null, uid, message);
	}
	
	public BizEventPoint(BizEventPoint prev, String uid, String message) {
		this(prev, uid, SystemClock.now(), message);
	}
	
	public BizEventPoint(String uid, long timestamp, String message) {
		this(null, uid, timestamp, message);
	}
	
	public BizEventPoint(BizEventPoint prev, String uid, long timestamp, String message) {
		this(prev, uid, timestamp, message, null);
	}
	
	public BizEventPoint(String uid, String message, Map<String, Object> data) {
		this(null, uid, message, data);
	}
	
	public BizEventPoint(BizEventPoint prev, String uid, String message, Map<String, Object> data) {
		this(prev, uid, SystemClock.now(), message, data);
	}
	
	public BizEventPoint(String uid, long timestamp, String message, Map<String, Object> data) {
		this(null, uid, SystemClock.now(), message, data);
	}
	
	public BizEventPoint(BizEventPoint prev, String uid, long timestamp, String message, Map<String, Object> data) {
		this.prev = prev;
		this.uid = uid;
		this.timestamp = timestamp;
		this.message = message;
		this.data = data == null ? new HashMap<String, Object>() : data;
	}

	public BizEventPoint getPrev() {
		return prev;
	}

	public void setPrev(BizEventPoint prev) {
		this.prev = prev;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
	
	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public void put(String key, Object value) {
		getData().put(key, value);
	}
	
}
