package com.t13max.ai.pathfinding;

import com.t13max.ai.pathfinding.astar.AStarPathFinding;
import com.t13max.ai.pathfinding.jps.JumpPointSearch;
import com.t13max.common.object.ObjectPool;
import org.junit.Test;

import java.util.List;

/**
 * @author: t13max
 * @since: 16:21 2024/7/22
 */
public class PathFindingTest {

    @Test
    public void pathFind() {

        //JumpPointSearch  15716
        //StarPathFinding  33786

        TestGrid testGrid = new TestGrid("grid.txt");

        IPathFinding aStarPathFinding = createAStarPathFinding();
        IPathFinding jpsPathFinding = createJPSPathFinding();

        calcCostMills(aStarPathFinding, testGrid);
        calcCostMills(jpsPathFinding, testGrid);

        //List<Node> path=jpsPathFinding.findPath(testGrid, new Node(1, 1), new Node(98, 98));
        //printPath(path, testGrid);
    }

    private void printPath(List<Node> path, TestGrid testGrid) {
        if (path != null && !path.isEmpty()) {
            testGrid.printPath(path);
            for (Node node : path) {
                System.out.println("Node: (" + node.x + ", " + node.y + ")");
            }
        } else {
            System.out.println("No path found.");
        }
    }

    private void calcCostMills(IPathFinding jpsPathFinding, TestGrid testGrid) {
        long beginMills = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            List<Node> path = jpsPathFinding.findPath(testGrid, new Node(1, 1), new Node(98, 98));
        }
        long endMills = System.currentTimeMillis();
        System.out.println(jpsPathFinding.getClass().getSimpleName() + (endMills - beginMills));
    }

    private IPathFinding createAStarPathFinding() {
        return new AStarPathFinding();
    }

    private IPathFinding createJPSPathFinding() {
        return new JumpPointSearch();
    }
}
