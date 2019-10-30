package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.exception.XMLParseException;
import org.dom4j.Element;

public class ArcToolSpecific {

	private String tool;
	private String version;
	private Spline spline;

	public ArcToolSpecific(Element toolspecificNode) throws XMLParseException {
		this.tool = toolspecificNode.attributeValue("tool");
		this.version = toolspecificNode.attributeValue("version");
		this.spline = new Spline(toolspecificNode.element("spline"));
	}
}
