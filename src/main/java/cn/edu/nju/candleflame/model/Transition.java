package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.model.inner.Graphics;
import cn.edu.nju.candleflame.model.inner.TransitionToolSpecific;
import lombok.Data;
import org.dom4j.Element;

@Data
public class Transition {

	private String id;

	private String name;

	private Graphics graphics;

	private TransitionToolSpecific transitionToolSpecific;

	public Transition(Element transitionNode) {
		this.id = transitionNode.attributeValue("id");

		this.name = transitionNode.element("name").element("text").getText();
		this.graphics = new Graphics(transitionNode.element("graphics"));
		this.transitionToolSpecific = new TransitionToolSpecific(transitionNode.element("toolspecific"));
	}
}
