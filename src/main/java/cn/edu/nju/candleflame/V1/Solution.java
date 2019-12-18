package cn.edu.nju.candleflame.V1;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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

	private static String endPalceId;

	public static void main(String[] args) {
		getLogOfModel("/Users/liweimin/Documents/code/workflow/src/main/resources/Model1.pnml", "/Users/liweimin/Documents/code/workflow/src/main/resources/path1.txt");
	}


	public static void getLogOfModel(String modelFile, String logFile){
		// 解析文件
		parseFile(modelFile);
		dfs(start,"",new HashSet<String>());
		writeFile(logFile);
		System.out.println(finalPath.size());
	}

	private static void writeFile(String logFile) {

		try (FileWriter fileWriter = new FileWriter(logFile)){
			for (String path:finalPath){
				fileWriter.write(path+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param currentState 当前的palce 令牌分布状态
	 * @param visitedPath 当前路径已经遍历过的Transition 路径记录
	 * @param visitedConnectedLine 经历过的place 与transition 之间的连线
	 */
	public static void dfs(boolean[] currentState, String visitedPath, HashSet<String> visitedConnectedLine){
		// 已经到达终点状态
		if (Arrays.equals(currentState, end)){
			finalPath.add(visitedPath.substring(1));
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
				boolean onLoopChoose = false;
				if (nextTransitions.size()>1){
					onLoopChoose = true;
				}
				loop:for (String nextTransition: nextTransitions){
					// 查看是否能进行下一个状态(针对并行结构出口情况，判断是否所有的place都包含token。如果没有，表示并不满足发生变迁的条件，跳过当前路径)
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
					// 并行结构,令牌由一个变为多个
					if (nextPlaces.size()>1){
						for (String nextPlace:nextPlaces){
							nextState[nameIndexMap.get(nextPlace)]=true;
						}

						// 判断循环结构是否存在过：根据 （下一个place所需要经过的Transition名称)来保证循环结构只有一个
						if (onLoopChoose&&visitedConnectedLine.contains(nextTransition)){
							continue loop;
						}
						visitedConnectedLine.add(nextTransition);
						dfs(nextState, visitedPath+" "+nameNodeMap.get(nextTransition).name, visitedConnectedLine);
						visitedConnectedLine.remove(nextTransition);
					}else { // 正常结构
						int nextPlaceIndex = nameIndexMap.get(nextPlaces.iterator().next());
						nextState[nextPlaceIndex]=true;

						if (onLoopChoose&&visitedConnectedLine.contains(nextTransition)){
							continue loop;
						}
						visitedConnectedLine.add(nextTransition);
						dfs(nextState, visitedPath+" "+nameNodeMap.get(nextTransition).name, visitedConnectedLine);
						visitedConnectedLine.remove(nextTransition);
					}
				}
			}
		}
	}

	private static boolean bfs(String currentPalceName) {
		Set<String> allPaths = connectMap.get(currentPalceName);
		boolean[] results = new boolean[allPaths.size()];
		int index=0;
		for (String path:allPaths){
			Queue<String> queue = new ArrayDeque<>();
			HashSet<String> visited = new HashSet<>();
			queue.offer(path);
			boolean result =false;
			while (queue.size()!=0){
				Set<String> nextIds = connectMap.get(queue.poll());
				for (String nextId: nextIds){
					if (nextId.equals(endPalceId)){
						result=true;
						break;
					}
					if (visited.contains(nextId)){
						continue;
					}
					visited.add(nextId);
					queue.add(nextId);
				}
			}

			results[index]=result;
			index++;
		}
		boolean finalResult = true;
		for (boolean tmp:results){
			finalResult &= tmp;
		}
		return !finalResult;
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

			endPalceId = endNode;

		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
