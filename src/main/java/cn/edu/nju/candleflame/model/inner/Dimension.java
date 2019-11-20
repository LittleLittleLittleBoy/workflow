package cn.edu.nju.candleflame.model.inner;

import org.dom4j.Element;

public class Dimension {

	private int x;

	private int y;

	public Dimension(Element dimensionNode) {
		this.x = Integer.parseInt(dimensionNode.attributeValue("x"));
		this.y = Integer.parseInt(dimensionNode.attributeValue("y"));
	}
}
