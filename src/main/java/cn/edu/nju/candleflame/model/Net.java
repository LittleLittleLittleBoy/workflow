package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.exception.XMLParseException;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Net {

	private String id;

	private String type;

	private List<Place> places;

	private List<Transition> transitions;

	private List<Arc> arcs;

	public Net(Element netNode) throws XMLParseException {
		this.id = netNode.attributeValue("id");
		this.type = netNode.attributeValue("type");

		this.places = new ArrayList<>();
		Iterator<Element> placeNodes = netNode.elementIterator("place");
		while (placeNodes.hasNext()){
			Element placeNode = placeNodes.next();
			Place place = new Place(placeNode);
			places.add(place);
		}

		this.transitions = new ArrayList<>();
		Iterator<Element> transitionsNodes = netNode.elementIterator("transition");
		while (transitionsNodes.hasNext()){
			Element transitionNode = transitionsNodes.next();
			Transition transition = new Transition(transitionNode);
			transitions.add(transition);
		}

		this.arcs = new ArrayList<>();
		Iterator<Element> arcNodes = netNode.elementIterator("arc");
		while (arcNodes.hasNext()){
			Element arcNode = arcNodes.next();
			Arc arc = new Arc(arcNode);
			arcs.add(arc);
		}
	}
}
