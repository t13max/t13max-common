package com.t13max.ai.pathfinding.jps;


import com.t13max.ai.pathfinding.IPathFind;
import com.t13max.ai.pathfinding.Node;
import com.t13max.ai.pathfinding.grid.IGrid;

import java.util.*;

/**
 * jps寻路
 *
 * @author: t13max
 * @since: 13:59 2024/7/22
 */
public class JumpPointSearch implements IPathFind {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {-1, 1}, {1, -1}};

    private final IGrid grid;

    public JumpPointSearch(IGrid grid) {
        this.grid = grid;
    }

    public List<Node> findPath(Node start, Node end) {
        return findPath(start.x, start.y, end.x, end.y);
    }

    public List<Node> findPath(int startX, int startY, int goalX, int goalY) {

        if (!grid.isValid(startX, startY) || !grid.isValid(goalX, goalY)) {
            return Collections.emptyList();
        }

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(startX, startY, 0, heuristic(startX, startY, goalX, goalY), null);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (int[] direction : DIRECTIONS) {
                Node jumpNode = jump(current, direction, goalX, goalY);
                if (jumpNode != null && !closedList.contains(jumpNode)) {
                    double g = current.g + distance(current, jumpNode);
                    double h = heuristic(jumpNode.x, jumpNode.y, goalX, goalY);
                    Node neighbor = new Node(jumpNode.x, jumpNode.y, g, h, current);

                    if (!isInOpenList(openList, neighbor) || g < getGValue(openList, neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private Node jump(Node current, int[] direction, int goalX, int goalY) {
        int x = current.x + direction[0];
        int y = current.y + direction[1];

        if (!grid.isValid(x, y)) {
            return null;
        }

        if (x == goalX && y == goalY) {
            return new Node(x, y, 0, 0, current);
        }

        if ((direction[0] != 0 && direction[1] != 0)) { // Diagonal move
            if ((grid.isValid(x - direction[0], y) && !grid.isValid(x - direction[0], y - direction[1])) ||
                    (grid.isValid(x, y - direction[1]) && !grid.isValid(x - direction[0], y - direction[1]))) {
                return new Node(x, y, 0, 0, current);
            }
        } else { // Horizontal or vertical move
            if (direction[0] != 0) { // Horizontal move
                if ((grid.isValid(x, y + 1) && !grid.isValid(x - direction[0], y + 1)) ||
                        (grid.isValid(x, y - 1) && !grid.isValid(x - direction[0], y - 1))) {
                    return new Node(x, y, 0, 0, current);
                }
            } else { // Vertical move
                if ((grid.isValid(x + 1, y) && !grid.isValid(x + 1, y - direction[1])) ||
                        (grid.isValid(x - 1, y) && !grid.isValid(x - 1, y - direction[1]))) {
                    return new Node(x, y, 0, 0, current);
                }
            }
        }

        if (direction[0] != 0 && direction[1] != 0) {
            Node nextJump = jump(new Node(x, y, 0, 0, current), new int[]{direction[0], 0}, goalX, goalY);
            if (nextJump != null) {
                return new Node(x, y, 0, 0, current);
            }
            nextJump = jump(new Node(x, y, 0, 0, current), new int[]{0, direction[1]}, goalX, goalY);
            if (nextJump != null) {
                return new Node(x, y, 0, 0, current);
            }
        }

        return jump(new Node(x, y, 0, 0, current), direction, goalX, goalY);
    }

    private double heuristic(int x1, int y1, int x2, int y2) {
        // Use Manhattan distance as the heuristic
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private double distance(Node n1, Node n2) {
        return Math.sqrt(Math.pow(n1.x - n2.x, 2) + Math.pow(n1.y - n2.y, 2));
    }

    private boolean isInOpenList(PriorityQueue<Node> openList, Node node) {
        return openList.contains(node);
    }

    private double getGValue(PriorityQueue<Node> openList, Node node) {
        for (Node n : openList) {
            if (n.equals(node)) {
                return n.g;
            }
        }
        return Double.MAX_VALUE;
    }

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

