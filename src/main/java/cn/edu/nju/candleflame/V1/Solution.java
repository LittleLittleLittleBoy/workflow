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
	List<List<String>> path; // 节点包含的路径

	public Node(Element element, NodeType nodeType){
		switch (nodeType){
			case PLACE:
				type = NodeType.PLACE;
				id = element.attributeValue("id");
				path = new ArrayList<>();
				break;
			case TRANSITION:
				type = NodeType.TRANSITION;
				id = element.attributeValue("id");
				path = new ArrayList<>();
				path.add(Stream.of(element.element("name").element("text").getText()).collect(Collectors.toList()));
				break;
		}
	}
}
class SubSystem{
	String startNode;
	String endNode;
}
class ParalleResult{
	String transitionId;
	String endPlaceId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ParalleResult)) return false;
		ParalleResult that = (ParalleResult) o;
		return Objects.equals(transitionId, that.transitionId) &&
				Objects.equals(endPlaceId, that.endPlaceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transitionId, endPlaceId);
	}

	public ParalleResult(String transitionId, String endPlaceId) {
		this.transitionId = transitionId;
		this.endPlaceId = endPlaceId;
	}
}
public class Solution {

	private static Map<String, Node> nameNodeMap= new HashMap<>();
	private static Map<String, Set<String>> connectMap = new HashMap<>();

	public static void main(String[] args) {
//		getLogOfModel("/Users/liweimin/Documents/code/workflow/src/main/resources/Model1.pnml", "");

		// 测试C(a,b)方法
//		List<List<Integer>> lists = caculateIndex(2, 0, 4);
//		lists.forEach(list -> System.out.println(String.join(" ", list+"")));
		// 测试pathParallel 方法
		List<List<String>> path1= new ArrayList<>();
		path1.add(Stream.of("a","b").collect(Collectors.toList()));
		path1.add(Stream.of("c","d").collect(Collectors.toList()));
		List<List<String>> path2= new ArrayList<>();
		path2.add(Stream.of("e","f").collect(Collectors.toList()));
		path2.add(Stream.of("g","h").collect(Collectors.toList()));
		List<List<String>> lists = pathParallel(path1, path2);
		System.out.println(lists.size());
		lists.forEach(list -> System.out.println(String.join(" ", list)));
	}

	public static void getLogOfModel(String modelFile, String logFile){
		// 解析文件
		SubSystem subSystem = parseFile(modelFile);

		//处理并行结构
		removeParallel();

		List<List<String>> allPath = parseSubSystem(subSystem);

		allPath.forEach(list -> System.out.println(String.join(" ", list)));
	}

	private static void removeParallel() {
		Set<String> transitionSet = nameNodeMap.values().stream().filter(i -> i.type == NodeType.TRANSITION).map(i->i.id).collect(Collectors.toSet());

		Set<String> beginTransitionSet = connectMap.keySet().stream().filter(i -> transitionSet.contains(i) && connectMap.get(i).size() > 1).collect(Collectors.toSet());
		Map<String,Integer> countMap = new HashMap<>();
		for (Set<String> value: connectMap.values()){
			for (String id: value){
				countMap.put(id,countMap.getOrDefault(id,0)+1);
			}
		}

		while (!beginTransitionSet.isEmpty()){
			for (String nodeId : beginTransitionSet){
				Set<String> strings = connectMap.get(nodeId);
				for (String placeId:strings){
					Set<String> endTransitionSet = connectMap.keySet().stream().filter(i -> connectMap.get(i).size() > 1).collect(Collectors.toSet());
					Set<ParalleResult> results = parallelDFS(placeId, endTransitionSet, new HashSet<String>());

				}
			}
		}

	}

	private static Set<ParalleResult> parallelDFS(String nodeId, Set<String> endTransitionSet, HashSet<String> visited) {
		Set<String> nextNodes = connectMap.get(nodeId);
		Node node = nameNodeMap.get(nodeId);
		if (node.type == NodeType.PLACE){
			if (visited.contains(nodeId)){
				return new HashSet<>();
			}
			for (String nextNode: nextNodes){
				if (endTransitionSet.contains(nextNode)){
					return Stream.of(new ParalleResult(nextNode,nodeId)).collect(Collectors.toSet());
				}else{
					parallelDFS(nextNode, endTransitionSet, visited);
				}
			}
		}else {
			Set<ParalleResult> results = new HashSet<>();
			for (String nextNode : nextNodes){
				Set<ParalleResult> paralleResults = parallelDFS(nextNode, endTransitionSet, visited);
				results.addAll(paralleResults);
			}
			return results;
		}
		return new HashSet<>();
	}


	private static List<List<String>> parseSubSystem(SubSystem subSystem) {
		// 处理循环结构 太难了！！！
		// removeLoop(subSystem);

		// dfs 遍历整个有向无环图
		List<List<String>> allPath = findAllPath(subSystem.startNode, subSystem.endNode);
		return allPath;
	}

	private static List<List<String>> findAllPath(String startNode, String endNode) {
		List<List<String>> result = new ArrayList<>();
		findAllPathDfs(startNode,endNode,new ArrayList<>(), result);
		return result;
	}

	private static void findAllPathDfs(String currentNode, String endNode,List<List<String>> before, List<List<String>> result) {
		Node node = nameNodeMap.get(currentNode);

		before = pathAppend(before, node.path);
		if (currentNode.equals(endNode)){ // 当前节点为终止节点
			result.addAll(before);
		}else {
			Set<String> outNodes = connectMap.get(currentNode);
			for (String outNode: outNodes){
				findAllPathDfs(outNode, endNode, before, result);
			}
		}
	}

	private static void removeLoop(SubSystem subSystem) {
		// 遍历所有的点看这个点是否是循环点
		for (Node node: nameNodeMap.values()){
			if (node.type == NodeType.PLACE){
				// 如果 place只有一个入度 肯定不是循环
				int inPathNum = (int) connectMap.values().stream().filter(set-> set.contains(node.id)).count();
				if (inPathNum == 0){
					continue;
				}
				List<List<String>> result = new ArrayList<>();
				findLoopPath(node.id, result, subSystem);
			}
		}
	}

	private static void findLoopPath(String palceId, List<List<String>> result, SubSystem subSystem) {
		Set<String> ids = connectMap.get(palceId);

		//LoopPlaceResult loopPlaceResult = loopPathDfs(palceId,palceId subSystem.endNode, new HashSet<String>());
	}

//	private static LoopPlaceResult loopPathDfs(String id, String startId, String endNode, HashSet<String> visitedPlace) {
//		Set<String> nextNodes = connectMap.get(id);
//		for (String nextNode: nextNodes){
//			Node node = nameNodeMap.get(nextNode);
//
//		}
//	}

	private static SubSystem parseFile(String modelFile) {
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

				inNodeSet.add(source);
				outNodeSet.add(target);
			}
			SubSystem subSystem = new SubSystem();
			for (String nodeId: nameNodeMap.keySet()){
				if (!inNodeSet.contains(nodeId)){
					subSystem.endNode = nodeId;
				}else if (!outNodeSet.contains(nodeId)){
					subSystem.startNode = nodeId;
				}
			}
			return subSystem;

		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 两个路径进行拼接
	private static List<List<String>> pathAppend(List<List<String>> path1, List<List<String>> path2){
		if (path1 == null|| path1.size()==0){
			return path2;
		}
		if (path2 == null|| path2.size()==0){
			return path1;
		}

		List<List<String>> tmp = new ArrayList<>();
		for (List<String> beforePath: path1){
			for (List<String> currentPath: path2){
				List<String> newPath = new ArrayList<>();
				newPath.addAll(beforePath);
				newPath.addAll(currentPath);
				tmp.add(newPath);
			}
		}
		return tmp;
	}

	// 两个路径并行
	private static List<List<String>> pathParallel(List<List<String>> path1, List<List<String>> path2){
		if (path1 == null|| path1.size()==0){
			return path2;
		}
		if (path2 == null|| path2.size()==0){
			return path1;
		}
		List<List<String>> result = new ArrayList<>();
		for (List<String> pathA: path1){
			for (List<String> pathB: path2){
				// 求C(a,a+b)的全部情况
				int a = pathA.size();
				int b = pathB.size();
				List<String> shortPath;
				List<String> longPath;
				if (a>b){
					longPath = pathA;
					shortPath = pathB;
				}else {
					longPath = pathB;
					shortPath = pathA;
				}

				List<List<Integer>> indexList = caculateIndex(Math.min(a, b), 0, a + b - 1);
				for (List<Integer> index: indexList){
					String[] tmp = new String[a+b];
					int longPathIndex = 0;
					int shortPathIndex = 0;
					int indexCurrent = 0;
					for (int i =0;i<a+b;i++){
						if (longPathIndex<longPath.size()&&i == index.get(indexCurrent)){
							tmp[i] = longPath.get(longPathIndex);
							indexCurrent++;
							longPathIndex++;
						}else {
							tmp[i] = shortPath.get(shortPathIndex);
							shortPathIndex++;
						}
					}
					result.add(Stream.of(tmp).collect(Collectors.toList()));
				}

			}
		}
		return result;
	}

	//计算 C(a,b)的情况
	private static List<List<Integer>> caculateIndex(int n, int start, int end){
		List<List<Integer>> result = new ArrayList<>();
		if (n == 1){
			for (int i=start;i<=end;i++){
				ArrayList<Integer> tmp = new ArrayList<>();
				tmp.add(i);
				result.add(tmp);
			}
			return result;
		}

		if (n >= end-start+1){
			ArrayList<Integer> tmp = new ArrayList<>();
			for (int i=start;i<=end;i++){
				tmp.add(i);
			}
			return result;
		}

		for (int i = start ;i<=end-n+1;i++){
			List<List<Integer>> subResult = caculateIndex(n-1,i+1, end);
			for (List<Integer> sub:subResult){
				sub.add(0,i);
				result.add(sub);
			}
		}
		return result;
	}
}
