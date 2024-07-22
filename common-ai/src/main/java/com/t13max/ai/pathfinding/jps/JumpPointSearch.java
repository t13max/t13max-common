package com.t13max.ai.pathfinding.jps;


import com.t13max.ai.pathfinding.AbstractPathFinding;
import com.t13max.ai.pathfinding.Node;
import com.t13max.ai.pathfinding.grid.IGrid;

import java.util.*;

/**
 * jps寻路
 *
 * @author: t13max
 * @since: 13:59 2024/7/22
 */
public class JumpPointSearch extends AbstractPathFinding {

    //八个方向
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {-1, 1}, {1, -1}};

    /**
     * 根据起始和终点进行寻路
     *
     * @Author t13max
     * @Date 18:44 2024/7/22
     */
    public List<Node> findPath(IGrid grid, Node start, Node end) {
        return findPath(grid, start.x, start.y, end.x, end.y);
    }

    public List<Node> findPath(IGrid grid, int startX, int startY, int goalX, int goalY) {

        //判断起始和终点是否合法
        if (!grid.isValid(startX, startY) || !grid.isValid(goalX, goalY)) {
            return Collections.emptyList();
        }
        //开放列表
        PriorityQueue<Node> openList = new PriorityQueue<>();
        //关闭列表
        Set<Node> closedList = new HashSet<>();
        //起始节点
        Node startNode = new Node(startX, startY, 0, heuristic(startX, startY, goalX, goalY), null);
        //加入openList
        openList.add(startNode);
        //遍历
        while (!openList.isEmpty()) {
            Node current = openList.poll();
            //是终点 构建路径
            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }
            //加入closedList
            closedList.add(current);
            //八个方向
            for (int[] direction : DIRECTIONS) {
                //寻找跳点
                Node jumpNode = jump(grid, current, direction, goalX, goalY);
                //不为空 且不在关闭列表
                if (jumpNode != null && !closedList.contains(jumpNode)) {
                    //计算两点间的花费
                    double g = current.g + distance(current, jumpNode);
                    //计算两点间预估花费
                    double h = heuristic(jumpNode.x, jumpNode.y, goalX, goalY);
                    //新节点
                    Node nextNode = new Node(jumpNode.x, jumpNode.y, g, h, current);
                    //不在openList直接添加 或者花费更低则覆盖
                    if (!isInOpenList(openList, nextNode) || g < getGValue(openList, nextNode)) {
                        openList.add(nextNode);
                    }
                }
            }
        }
        //没找到
        return Collections.emptyList();
    }

    /**
     * 寻找跳点
     *
     * @Author t13max
     * @Date 18:48 2024/7/22
     */
    private Node jump(IGrid grid, Node current, int[] direction, int goalX, int goalY) {
        //计算目标点做标
        int x = current.x + direction[0];
        int y = current.y + direction[1];
        //目标点非法
        if (!grid.isValid(x, y)) {
            return null;
        }
        //终点 直接返回 花费为0
        if (x == goalX && y == goalY) {
            return new Node(x, y, 0, 0, current);
        }
        //斜向移动
        if ((direction[0] != 0 && direction[1] != 0)) {
            if ((grid.isValid(x - direction[0], y) && !grid.isValid(x - direction[0], y - direction[1])) ||
                    (grid.isValid(x, y - direction[1]) && !grid.isValid(x - direction[0], y - direction[1]))) {
                return new Node(x, y, 0, 0, current);
            }
        } else {
            //水平
            if (direction[0] != 0) {
                if ((grid.isValid(x, y + 1) && !grid.isValid(x - direction[0], y + 1)) ||
                        (grid.isValid(x, y - 1) && !grid.isValid(x - direction[0], y - 1))) {
                    return new Node(x, y, 0, 0, current);
                }
            } else {
                //垂直
                if ((grid.isValid(x + 1, y) && !grid.isValid(x + 1, y - direction[1])) ||
                        (grid.isValid(x - 1, y) && !grid.isValid(x - 1, y - direction[1]))) {
                    return new Node(x, y, 0, 0, current);
                }
            }
        }
        //斜向移动
        if (direction[0] != 0 && direction[1] != 0) {
            //
            Node nextJump = jump(grid, new Node(x, y, 0, 0, current), new int[]{direction[0], 0}, goalX, goalY);
            if (nextJump != null) {
                return new Node(x, y, 0, 0, current);
            }
            nextJump = jump(grid, new Node(x, y, 0, 0, current), new int[]{0, direction[1]}, goalX, goalY);
            if (nextJump != null) {
                return new Node(x, y, 0, 0, current);
            }
        }
        //水平或垂直
        return jump(grid, new Node(x, y, 0, 0, current), direction, goalX, goalY);
    }


}

