package com.t13max.ai.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author: t13max
 * @since: 19:02 2024/7/22
 */
public abstract class AbstractPathFinding implements IPathFinding {
    /**
     * 计算两点间预估花费
     *
     * @Author t13max
     * @Date 18:48 2024/7/22
     */
    protected double heuristic(Node node1, Node node2) {
        return heuristic(node1.x, node1.y, node2.x, node2.y);
    }

    protected double heuristic(int x1, int y1, int x2, int y2) {
        // Use Manhattan distance as the heuristic
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * 计算两点间距离
     *
     * @Author t13max
     * @Date 18:46 2024/7/22
     */
    protected double distance(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
    }

    /**
     * 在开放列表中
     *
     * @Author t13max
     * @Date 18:49 2024/7/22
     */
    protected boolean isInOpenList(PriorityQueue<Node> openList, Node node) {
        return openList.contains(node);
    }

    /**
     * 获取指定节点的花费
     *
     * @Author t13max
     * @Date 18:49 2024/7/22
     */
    protected double getGValue(PriorityQueue<Node> openList, Node node) {
        for (Node n : openList) {
            if (n.equals(node)) {
                return n.g;
            }
        }
        return Double.MAX_VALUE;
    }

    /**
     * 构建路径
     *
     * @Author t13max
     * @Date 18:49 2024/7/22
     */
    protected List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

}
