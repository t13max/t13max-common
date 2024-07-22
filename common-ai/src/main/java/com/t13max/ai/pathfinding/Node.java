package com.t13max.ai.pathfinding;


import java.util.Objects;

/**
 * 寻路用节点实体类
 * equals和hashcode已被重写 只关注x与y的值
 * 在优先队列中 根据g和h的和排序
 *
 * @author: t13max
 * @since: 13:58 2024/7/22
 */
public class Node implements Comparable<Node> {
    public int x, y;

    public double g, h;
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Node(int x, int y, double g, double h, Node parent) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public double getF() {
        return this.g + this.h;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
