package cn.edu.nju.candleflame.model.inner;

import org.dom4j.Element;

public class Graphics {

	private Position position;

	private Dimension dimension;

	public Graphics(Element graphicsNode) {
		this.position = new Position(graphicsNode.element("position"));
		this.dimension = new Dimension(graphicsNode.element("dimension"));
	}
}
