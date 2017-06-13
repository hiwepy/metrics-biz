package com.codahale.metrics.biz.event;

/**
 * 
 * @className	： BizStatusEvent
 * @description	：业务运行状态事件对象
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年6月9日 下午5:23:57
 * @version 	V1.0
 */
@SuppressWarnings("serial")
public class BizMeterEvent extends BizEvent<BizEventPoint> {

	public BizMeterEvent(Object source, BizEventPoint bind) {
		super(source, bind);
	}
	
}
