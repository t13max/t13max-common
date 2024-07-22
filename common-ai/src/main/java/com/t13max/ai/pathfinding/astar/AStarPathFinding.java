package com.t13max.ai.pathfinding.astar;

import com.t13max.ai.pathfinding.IPathFind;
import com.t13max.ai.pathfinding.Node;
import com.t13max.ai.pathfinding.grid.IGrid;
import com.t13max.ai.utils.Log;

import java.util.*;

/**
 * A*寻路算法
 *
 * @author: t13max
 * @since: 15:22 2024/7/22
 */
public class AStarPathFinding implements IPathFind {

    //八个方向
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};

    //地图表格接口
    private IGrid grid;

    public AStarPathFinding(IGrid grid) {
        this.grid = grid;
    }

    private final Set<Node> closeSet = new HashSet<>();

    private final PriorityQueue<Node> openList = new PriorityQueue<>();


    @Override
    public List<Node> findPath(Node start, Node end) {
        //合法性校验
        if (!grid.isValid(end.x, end.y)) {
            Log.A_STAR.error("起点or终点 不在格子内");
            return null;
        }
        //先清空 可以重复使用
        closeSet.clear();
        openList.clear();
        //加入开放列表
        openList.add(start);
        //遍历所有
        while (!openList.isEmpty()) {
            //选出最近的
            Node current = openList.poll();
            //是终点 构造路径
            if (current.equals(end)) {
                return reconstructPath(current);
            }
            //加入closeSet
            closeSet.add(current);
            //八个方向
            for (int[] direction : DIRECTIONS) {
                int x = current.x + direction[0];
                int y = current.y + direction[1];
                //位置合法 且不在closeSet中
                if (grid.isValid(x, y) && !closeSet.contains(new Node(x, y))) {
                    //计算出到当前点所需花费 斜边按1.4算
                    double g = current.g + x == 0 || y == 0 ? 1 : 1.4;
                    //计算邻居点到终点的预期值
                    double h = heuristic(x, y, end.x, end.y);
                    //新建节点
                    Node nextNode = new Node(x, y, g, h, current);
                    //已经在openList 则更新花费 或者不在则直接添加
                    if (!openList.contains(nextNode) || g < getGValue(openList, nextNode)) {
                        openList.add(nextNode);
                    }
                }
            }
        }
        //未找到
        return Collections.emptyList();
    }

    /**
     * 获取指定点的G值
     *
     * @Author t13max
     * @Date 18:16 2024/7/22
     */
    private double getGValue(PriorityQueue<Node> openList, Node node) {
        for (Node n : openList) {
            if (n.equals(node)) {
                return n.g;
            }
        }
        return Double.MAX_VALUE;
    }

    /**
     * 或者指定两点之间的预估花费
     *
     * @Author t13max
     * @Date 18:16 2024/7/22
     */
    private double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * 构建路径
     *
     * @Author t13max
     * @Date 18:16 2024/7/22
     */
    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

}


