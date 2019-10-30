package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.exception.XMLParseException;
import org.dom4j.Element;

import java.util.List;

public class Spline {
	private Point point1;
	private Point point2;
	private Point point3;
	private Point point4;
	private End end;

	public Spline(Element splineNode) throws XMLParseException {
		List<Element> pointNodes = splineNode.elements("point");
		if (pointNodes.size()!=4){
			throw new XMLParseException();
		}
		this.point1 = new Point(pointNodes.get(0));
		this.point2 = new Point(pointNodes.get(1));
		this.point3 = new Point(pointNodes.get(2));
		this.point4 = new Point(pointNodes.get(3));
		this.end = new End(splineNode.element("end"));
	}
}
