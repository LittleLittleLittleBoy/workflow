package cn.edu.nju.candleflame.algorithms;

import cn.edu.nju.candleflame.model.Net;
import cn.edu.nju.candleflame.model.WrapNode;
import cn.edu.nju.candleflame.util.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 1. @Description:
 * 2. @Author: TianGuisong
 * 3. @Date: Created in 12:51 PM 2019/11/20
 */
public class BruteForce implements Solution {

    private List<List> allPaths = new ArrayList<>();

    @Override
    public List<String> solve(Net net) {
        allPaths.clear();
        WrapNode start = parseNet(net);
        dfs(start, new ArrayList<>());

        List<String> res = new ArrayList<>();
        for (List<WrapNode> path: allPaths){
            String spath = path.stream().map(node -> node.getId()).collect(Collectors.joining("->"));
            res.add(spath);
        }
        return res;
    }

    public static void main(String[] args) {
        Solution solution = new BruteForce();
        String path = "/Users/loick/Documents/研一/高级算法/workflow/src/main/resources/Model1.pnml";
        List<Net> nets = Parser.parseDocument(path);
        List<String> res = solution.solve(nets.get(0));
        System.out.println(res);
    }

    private void dfs(WrapNode node, List<WrapNode> path){
        while (node.getOuts().size() <= 1){
            path.add(node);
            if (node.getOuts().size() == 1){
                node = node.getOuts().get(0);
            }else{
                List<WrapNode> pathnodes = new ArrayList<>();
                pathnodes.addAll(path);
                allPaths.add(pathnodes);
                return;
            }
        }

        path.add(node);
        for (WrapNode nx: node.getOuts()){
            dfs(nx, path);
        }
        path.remove(path.size()-1);
    }

    /**
     * parse net, return start node
     * @param net
     * @return
     */
    private WrapNode parseNet(Net net){
        List<WrapNode> nodes = net.getPlaces().stream().map(WrapNode::new).collect(Collectors.toList());
        List<WrapNode> trans = net.getTransitions().stream().map(WrapNode::new).collect(Collectors.toList());
        nodes.addAll(trans);

        Map<String, WrapNode> nodeMap = nodes.stream().collect(Collectors.toMap(node -> node.getId(), node -> node));
        net.getArcs().stream().forEach(arc -> {
            WrapNode fr = nodeMap.get(arc.getSource());
            WrapNode to = nodeMap.get(arc.getTarget());
            fr.getOuts().add(to);
            to.getIns().add(fr);
        });

        for (WrapNode node: nodeMap.values()){
            if (node.getIns().size() == 0){
                return node;
            }
        }
        return null;
    }
}
