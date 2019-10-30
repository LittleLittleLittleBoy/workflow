package cn.edu.nju.candleflame.model;


import org.dom4j.Element;

public class LogEvent {

	private String name;

	private String type;

	public LogEvent(Element logeventNode) {
		this.name = logeventNode.elementText("name");
		this.type = logeventNode.elementText("type");
	}
}
