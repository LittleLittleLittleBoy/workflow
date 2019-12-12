package cn.edu.nju.candleflame.V1;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum NodeType{
	PLACE, TRANSITION;
}

class Node{
	NodeType type;
	String id;
	String name;

	public Node(Element element, NodeType nodeType){
		switch (nodeType){
			case PLACE:
				type = NodeType.PLACE;
				id = element.attributeValue("id");
				break;
			case TRANSITION:
				type = NodeType.TRANSITION;
				id = element.attributeValue("id");
				name = element.element("name").element("text").getText();
				break;
		}
	}
}
public class Solution {

	// 所有节点的集合
	private static Map<String, Node> nameNodeMap= new HashMap<>();

	// 保存可达图状态与place名字的对应关系
	private static Map<String, Integer> nameIndexMap = new HashMap<>();
	private static String[] nodeNames;

	// 所有的连接线
	private static Map<String, Set<String>> connectMap = new HashMap<>();
	// 所有的被连接线
	private static Map<String, Set<String>> beConnectMap = new HashMap<>();

	// 最终的路径集合
	private static Set<String> finalPath = new HashSet<>();
	// 开始状态
	private static boolean[] start;
	// 结束状态
	private static boolean[] end;

	public static void main(String[] args) {
		getLogOfModel("/Users/liweimin/Documents/code/workflow/src/main/resources/Model3.pnml", "");
	}


	public static void getLogOfModel(String modelFile, String logFile){
		// 解析文件
		parseFile(modelFile);
		dfs(start,"");
		finalPath.forEach(i-> System.out.println(i));
		System.out.println(finalPath.size());
	}

	public static void dfs(boolean[] currentState, String visitedPath){
		if (Arrays.equals(currentState, end)){
			finalPath.add(visitedPath);
			return;
		}
		for (int i=0;i<currentState.length;i++){
			// 当前这个place有令牌
			if (currentState[i]){
				String currentPalceName = nodeNames[i];
				Set<String> nextTransitions = connectMap.get(currentPalceName);
				if (nextTransitions==null){
					continue;
				}
				loop:for (String nextTransition: nextTransitions){
					// 查看是否能进行下一个状态
					boolean[] nextState = currentState.clone();
					Set<String> beforePlaces = beConnectMap.get(nextTransition);
					if (!beforePlaces.isEmpty()){
						for (String placeName:beforePlaces){
							if (!currentState[nameIndexMap.get(placeName)]){
								continue loop;
							}else {
								nextState[nameIndexMap.get(placeName)] = false;
							}
						}
					}

					Set<String> nextPlaces = connectMap.get(nextTransition);
					// 并行结构
					if (nextPlaces.size()>1){
						for (String nextPlace:nextPlaces){
							nextState[nameIndexMap.get(nextPlace)]=true;
						}
						dfs(nextState, visitedPath+" "+nameNodeMap.get(nextTransition).name);
					}else { // 正常结构
						nextState[nameIndexMap.get(nextPlaces.iterator().next())]=true;
						dfs(nextState, visitedPath+" "+nameNodeMap.get(nextTransition).name);
					}
				}
			}
		}
	}


	private static void parseFile(String modelFile) {
		try {
			File file = new File(modelFile);
			if (!file.exists()){
				throw new FileNotFoundException();
			}
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(file);
			Element pnmlNode = document.getRootElement();
			Element netNode = pnmlNode.element("net");
			List<Element> places = netNode.elements("place");
			for (Element place:places){
				Node node = new Node(place, NodeType.PLACE);
				nameNodeMap.put(node.id,node);
			}
			List<Element> transitions = netNode.elements("transition");
			for (Element transition:transitions){
				Node node = new Node(transition, NodeType.TRANSITION);
				nameNodeMap.put(node.id,node);
			}

			Set<String> inNodeSet = new HashSet<>();
			Set<String > outNodeSet = new HashSet<>();
			List<Element> arcs = netNode.elements("arc");
			for (Element arc:arcs){
				String source = arc.attributeValue("source");
				String target = arc.attributeValue("target");
				if (connectMap.containsKey(source)){
					connectMap.get(source).add(target);
				}else {
					connectMap.put(source, Stream.of(target).collect(Collectors.toSet()));
				}
				if (beConnectMap.containsKey(target)){
					beConnectMap.get(target).add(source);
				}else {
					beConnectMap.put(target, Stream.of(source).collect(Collectors.toSet()));
				}

				inNodeSet.add(source);
				outNodeSet.add(target);
			}
			String endNode = null;
			String startNode = null;
			for (String nodeId: nameNodeMap.keySet()){
				if (!inNodeSet.contains(nodeId)){
					endNode = nodeId;
				}else if (!outNodeSet.contains(nodeId)){
					startNode = nodeId;
				}
			}

			List<String> transitionNodeNames = nameNodeMap.values().stream().filter(i -> i.type == NodeType.PLACE).map(i -> i.id).collect(Collectors.toList());
			int transitionNum = transitionNodeNames.size();
			nodeNames = new String[transitionNum];
			for (int i =0;i<transitionNodeNames.size();i++){
				nameIndexMap.put(transitionNodeNames.get(i), i);
				nodeNames[i] = transitionNodeNames.get(i);
			}

			start = new boolean[transitionNum];
			start[nameIndexMap.get(startNode)] = true;
			end = new boolean[transitionNum];
			end[nameIndexMap.get(endNode)] = true;

		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
