package cn.edu.nju.candleflame.model.inner;

import org.dom4j.Element;

public class Position {

	private int x;
	private int y;

	public Position(Element positionNode) {
		this.x = Integer.parseInt(positionNode.attributeValue("x"));
		this.y = Integer.parseInt(positionNode.attributeValue("y"));
	}
}
