package cn.edu.nju.candleflame;

import cn.edu.nju.candleflame.exception.XMLParseException;
import cn.edu.nju.candleflame.model.Net;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("/Users/liweimin/Documents/code/workflow/src/main/resources/Model1.pnml");
		if (!file.exists()){
			throw new FileNotFoundException();
		}
		SAXReader saxReader = new SAXReader();
		List<Net> nets = new ArrayList<>();
		try {
			Document document = saxReader.read(file);
			Element pnmlNode = document.getRootElement();
			Iterator<Element> i = pnmlNode.elementIterator();
			while (i.hasNext()) {
				Element netNode = i.next();
				Net net = new Net(netNode);
				nets.add(net);
			}
		} catch (DocumentException | XMLParseException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("parse Done");
	}
}
