package cn.edu.nju.candleflame.model;

import cn.edu.nju.candleflame.model.inner.Graphics;
import cn.edu.nju.candleflame.model.inner.TransitionToolSpecific;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1. @Description:
 * 2. @Author: TianGuisong
 * 3. @Date: Created in 1:20 PM 2019/11/20
 */
@Data
public class WrapNode {

    private String id;

    private String name;

    private Graphics graphics;

    private TransitionToolSpecific transitionToolSpecific;

    /**
     * for transition 0
     * for place 1
     */
    private int type;

    /**
     * in coming nodes
     */
    private List<WrapNode> ins = new ArrayList<>();

    /**
     * out nodes
     */
    private List<WrapNode> outs = new ArrayList<>();

    /**
     * all possible inner paths, for integration mode
     */
    private List<List<WrapNode>> innerPaths = new ArrayList<>();

    public WrapNode(Transition transition){
        this.id = transition.getId();
        this.name = transition.getName();
        this.graphics = transition.getGraphics();
        this.transitionToolSpecific = transition.getTransitionToolSpecific();
        this.type = 0;
        this.innerPaths.add(Arrays.asList(this));
    }

    public WrapNode(Place place){
        this.id = place.getId();
        this.name = place.getName();
        this.graphics = place.getGraphics();
        this.type = 1;
        this.innerPaths.add(Arrays.asList(this));
    }
}
