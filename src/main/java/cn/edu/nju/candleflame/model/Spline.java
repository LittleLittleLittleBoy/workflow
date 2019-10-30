package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.exception.XMLParseException;
import org.dom4j.Element;

import java.util.List;
import java.util.stream.Collectors;

public class Spline {
	private List<Point> points;
	private End end;

	public Spline(Element splineNode) throws XMLParseException {
		List<Element> pointNodes = splineNode.elements("point");
		points = pointNodes.stream().map(Point::new).collect(Collectors.toList());
		this.end = new End(splineNode.element("end"));
	}
}
