package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.exception.XMLParseException;
import cn.edu.nju.candleflame.model.inner.ArcToolSpecific;
import lombok.Data;
import org.dom4j.Element;

@Data
public class Arc {

	private String id;

	/**
	 * from id
	 */
	private String source;

	/**
	 * target id
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
