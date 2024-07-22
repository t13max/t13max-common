package com.t13max.ai.pathfinding;

import java.util.List;

/**
 * 寻路接口
 *
 * @author: t13max
 * @since: 15:24 2024/7/22
 */
public interface IPathFind {

    List<Node> findPath(Node start, Node end);

}
