package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.model.inner.Graphics;
import lombok.Data;
import org.dom4j.Element;

@Data
public class Place {

	private String id;

	private String name;

	private Graphics graphics;

	public Place(Element placeNode){
		this.id = placeNode.attributeValue("id");
		this.graphics = new Graphics(placeNode.element("graphics"));
		this.name = placeNode.element("name").element("text").getText();
	}
}
