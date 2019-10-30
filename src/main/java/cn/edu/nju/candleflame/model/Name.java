package cn.edu.nju.candleflame.model;

import org.dom4j.Element;

public class Name {

	private String text;

	public Name(Element nameNode) {
		this.text = nameNode.elementText("text");
	}
}
