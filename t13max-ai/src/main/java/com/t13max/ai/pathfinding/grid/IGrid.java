package com.t13max.ai.pathfinding.grid;


import com.t13max.ai.pathfinding.Node;

/**
 * 网格接口
 * 用于判断是否可达 等
 *
 * @author: t13max
 * @since: 15:34 2024/7/22
 */
public interface IGrid {

    boolean isInBound(int x, int y);

    boolean isWalkable(int x, int y);

    default boolean isValid(Node node) {
        return isValid(node.x, node.y);
    }

    default boolean isValid(int x, int y) {
        return isInBound(x, y) && isWalkable(x, y);
    }

}
