package cn.edu.nju.candleflame.model;

import org.dom4j.Element;

public class Point {

	private int x;
	private int y;

	public Point(Element pointNode) {
		this.x = Integer.parseInt(pointNode.attributeValue("x"));
		this.y = Integer.parseInt(pointNode.attributeValue("y"));
	}
}
