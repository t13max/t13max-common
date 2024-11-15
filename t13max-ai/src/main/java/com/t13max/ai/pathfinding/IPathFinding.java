package com.t13max.ai.pathfinding;

import com.t13max.ai.pathfinding.grid.IGrid;

import java.util.List;

/**
 * 寻路接口
 *
 * @author: t13max
 * @since: 15:24 2024/7/22
 */
public interface IPathFinding {

    List<Node> findPath(IGrid grid, Node start, Node end);

}
