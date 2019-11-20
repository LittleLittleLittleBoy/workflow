package cn.edu.nju.candleflame.model.inner;

import org.dom4j.Element;

public class End {
	private int x;
	private int y;

	public End(Element endNode) {
		this.x = Integer.parseInt(endNode.attributeValue("x"));
		this.y = Integer.parseInt(endNode.attributeValue("y"));
	}
}
