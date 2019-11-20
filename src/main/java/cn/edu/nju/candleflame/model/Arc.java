package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.exception.XMLParseException;
import cn.edu.nju.candleflame.model.inner.ArcToolSpecific;
import org.dom4j.Element;

public class Arc {

	private String id;

	/**
	 * from node
	 */
	private String source;

	/**
	 * target node
	 */
	private String target;
	private ArcToolSpecific arcToolSpecific;

	public Arc(Element arcNode) throws XMLParseException {
		this.id = arcNode.attributeValue("id");
		this.source = arcNode.attributeValue("source");
		this.target = arcNode.attributeValue("target");
		this.arcToolSpecific = new ArcToolSpecific(arcNode.element("toolspecific"));
	}
}
