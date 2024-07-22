package com.t13max.ai.pathfinding;

import com.t13max.ai.pathfinding.jps.JumpPointSearch;
import org.junit.Test;

import java.util.List;

/**
 * @author: t13max
 * @since: 16:21 2024/7/22
 */
public class PathFindingTest {

    @Test
    public void pathFind() {
        TestGrid testGrid = new TestGrid("grid.txt");

        IPathFinding iPathFinding = createPathFinding();

        List<Node> path = iPathFinding.findPath(testGrid, new Node(1, 1), new Node(98, 98));

        if (path != null && !path.isEmpty()) {
            testGrid.printPath(path);
            for (Node node : path) {
                System.out.println("Node: (" + node.x + ", " + node.y + ")");
            }
        } else {
            System.out.println("No path found.");
        }
    }

    private IPathFinding createPathFinding() {
        return new JumpPointSearch();
    }
}
