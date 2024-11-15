package com.t13max.ai.pathfinding;


import com.t13max.common.object.IPooledObject;
import com.t13max.common.object.ObjectPool;

import java.util.Objects;

/**
 * 寻路用节点实体类
 * equals和hashcode已被重写 只关注x与y的值
 * 在优先队列中 根据g和h的和排序
 *
 * @author: t13max
 * @since: 13:58 2024/7/22
 */
public class Node implements Comparable<Node>, IPooledObject {

    private static ObjectPool<Node> OBJECT_POOL = new ObjectPool<>(10000, Node::new);

    public int x, y;
    public double g, h;
    public Node parent;

    public Node() {
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Node(int x, int y, Node parent) {
        this(x, y);
        this.parent = parent;
    }

    public Node(int x, int y, double g, double h, Node parent) {
        this(x, y, parent);
        this.g = g;
        this.h = h;
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

    @Override
    public void borrowObject() {

    }

    @Override
    public void returnObject() {
        x = 0;
        y = 0;
        g = 0;
        h = 0;
        parent = null;
    }

    public static Node newNode(int x, int y) {
        //Node node = OBJECT_POOL.borrowObject();
        Node node = new Node();
        node.x = x;
        node.y = y;
        return node;
    }

    public static Node newNode(int x, int y, Node parent) {
        Node node = newNode(x, y);
        node.parent = parent;
        return node;
    }

    public static Node newNode(int x, int y, double g, double h, Node parent) {
        Node node = newNode(x, y, parent);
        node.g = g;
        node.h = h;
        return node;
    }
}
