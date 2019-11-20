package cn.edu.nju.candleflame.model.util;

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

/**
 * 1. @Description:
 * 2. @Author: TianGuisong
 * 3. @Date: Created in 12:46 PM 2019/11/20
 */
public class Parser {
    public static List<Net> parseDocument(String path){
        List<Net> nets = new ArrayList<>();
        try {
            File file = new File(path);
            if (!file.exists()){
                throw new FileNotFoundException();
            }
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(file);
            Element pnmlNode = document.getRootElement();
            Iterator<Element> i = pnmlNode.elementIterator();
            while (i.hasNext()) {
                Element netNode = i.next();
                Net net = new Net(netNode);
                nets.add(net);
            }
        } catch (DocumentException | XMLParseException | FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("parse done");
        return nets;
    }
}
