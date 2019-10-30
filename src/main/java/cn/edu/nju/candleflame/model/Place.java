package cn.edu.nju.candleflame.model;

import org.dom4j.Element;

public class Place {

	private String id;

	private Name name;

	private Graphics graphics;

	public Place(Element placeNode){
		this.id = placeNode.attributeValue("id");
		this.graphics = new Graphics(placeNode.element("graphics"));
		this.name = new Name(placeNode.element("name"));
	}
}
