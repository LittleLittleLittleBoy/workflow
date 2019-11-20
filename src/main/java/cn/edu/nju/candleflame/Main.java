package cn.edu.nju.candleflame;

import cn.edu.nju.candleflame.model.Net;
import cn.edu.nju.candleflame.model.util.Parser;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		String path = "/Users/loick/Documents/研一/高级算法/workflow/src/main/resources/Model1.pnml";
		List<Net> nets = Parser.parseDocument(path);
	}
}
