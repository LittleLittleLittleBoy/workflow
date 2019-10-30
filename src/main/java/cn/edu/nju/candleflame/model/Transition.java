package cn.edu.nju.candleflame.model;

import org.dom4j.Element;

public class Transition {

	private String id;

	private Name name;

	private Graphics graphics;

	private TransitionToolSpecific transitionToolSpecific;

	public Transition(Element transitionNode) {
		this.id = transitionNode.attributeValue("id");

		this.name = new Name(transitionNode.element("name"));
		this.graphics = new Graphics(transitionNode.element("graphics"));
		this.transitionToolSpecific = new TransitionToolSpecific(transitionNode.element("toolspecific"));
	}
}
