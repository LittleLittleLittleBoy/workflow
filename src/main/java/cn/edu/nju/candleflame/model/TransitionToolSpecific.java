package cn.edu.nju.candleflame.model;

import org.dom4j.Element;

public class TransitionToolSpecific {

	private String tool;

	private String version;

	private LogEvent logEvent;

	public TransitionToolSpecific(Element toolspecificNode) {
		this.tool = toolspecificNode.attributeValue("tool");
		this.version = toolspecificNode.attributeValue("version");
		this.logEvent = new LogEvent(toolspecificNode.element("logevent"));
	}
}
