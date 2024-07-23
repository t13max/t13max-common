package com.t13max.ai.pathfinding.jps;


import com.t13max.ai.pathfinding.AbstractPathFinding;
import com.t13max.ai.pathfinding.Node;
import com.t13max.ai.pathfinding.grid.IGrid;

import java.util.*;

/**
 * jps寻路
 * https://zhuanlan.zhihu.com/p/290924212
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

        //判断起始和终点是否合法
        if (!grid.isValid(start) || !grid.isValid(end)) {
            return Collections.emptyList();
        }
        //开放列表
        PriorityQueue<Node> openList = new PriorityQueue<>();
        //关闭列表
        Set<Node> closedList = new HashSet<>();
        //起始节点
        Node startNode =  Node.newNode(start.x, start.y, 0, heuristic(start, end), null);
        //加入openList
        openList.add(startNode);
        //遍历
        while (!openList.isEmpty()) {
            Node current = openList.poll();
            //是终点 构建路径
            if (current.equals(end)) {
                return reconstructPath(current);
            }
            //加入closedList
            closedList.add(current);
            //八个方向
            for (int[] direction : DIRECTIONS) {
                //寻找跳点
                Node jumpNode = jump(grid, current, direction, end);
                //不为空 且不在关闭列表
                if (jumpNode != null && !closedList.contains(jumpNode)) {
                    //计算两点间的花费
                    double g = current.g + distance(current, jumpNode);
                    //计算两点间预估花费
                    double h = heuristic(jumpNode, end);
                    //新节点
                    Node nextNode = Node.newNode(jumpNode.x, jumpNode.y, g, h, current);
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
    private Node jump(IGrid grid, Node current, int[] direction, Node end) {
        //计算目标点做标
        int x = current.x + direction[0];
        int y = current.y + direction[1];
        //目标点非法
        if (!grid.isValid(x, y)) {
            return null;
        }
        //终点 直接返回 花费为0
        if (x == end.x && y == end.y) {
            return Node.newNode(x, y, 0, 0, current);
        }
        //水平或垂直
        if (direction[0] == 0 || direction[1] == 0) {
            return jumpLine(grid, current, direction, end);
        } else {
            //斜向
            return jumpDiagonal(grid, current, direction, end);
        }
    }

    private Node jumpLine(IGrid grid, Node current, int[] direction, Node end) {
        //计算目标点做标
        int x = current.x + direction[0];
        int y = current.y + direction[1];
        //目标点非法
        if (!grid.isValid(x, y)) {
            return null;
        }
        Node nextNode = Node.newNode(x, y, current);
        //终点 直接返回 花费为0
        if (nextNode.equals(end)) {
            return Node.newNode(x, y, 0, 0, current);
        }
        //水平 检查是否左右可达 左后或右后不可达 也就是左或右为强迫邻居
        if (direction[0] != 0) {
            if ((grid.isValid(x, y + 1) && !grid.isValid(x - direction[0], y + 1)) ||
                    (grid.isValid(x, y - 1) && !grid.isValid(x - direction[0], y - 1))) {
                return nextNode;
            }
        } else {
            //垂直
            if ((grid.isValid(x + 1, y) && !grid.isValid(x + 1, y - direction[1])) ||
                    (grid.isValid(x - 1, y) && !grid.isValid(x - 1, y - direction[1]))) {
                return nextNode;
            }
        }
        return jumpLine(grid, nextNode, direction, end);
    }

    private Node jumpDiagonal(IGrid grid, Node current, int[] direction, Node end) {
        //计算目标点做标
        int x = current.x + direction[0];
        int y = current.y + direction[1];
        //目标点非法
        if (!grid.isValid(x, y)) {
            return null;
        }
        //下一个节点
        Node nextNode = Node.newNode(x, y, current);
        //终点 直接返回 花费为0
        if (nextNode.equals(end)) {
            return nextNode;
        }
        //水平寻找
        Node horNode = jumpLine(grid, nextNode, new int[]{direction[0], 0}, end);
        if (horNode != null) return nextNode;
        //垂直寻找
        Node verCell = jumpLine(grid, nextNode, new int[]{0, direction[1]}, end);
        if (verCell != null) return nextNode;
        //没找到 继续递归
        return jumpDiagonal(grid, nextNode, direction, end);
    }

}

