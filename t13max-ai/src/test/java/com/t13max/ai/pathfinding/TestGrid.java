package com.t13max.ai.pathfinding;


import com.t13max.ai.pathfinding.grid.IGrid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author: t13max
 * @since: 15:25 2024/7/22
 */
public class TestGrid implements IGrid {

    private boolean[][] cellArray;

    private int length;

    public TestGrid(String name) {

        this.cellArray = new boolean[100][100];

        InputStream resourceAsStream = TestGrid.class.getClassLoader().getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new RuntimeException("资源找不到");
        }
        InputStreamReader isr = new InputStreamReader(resourceAsStream);

        try (BufferedReader br = new BufferedReader(isr)) {

            String lineTxt = null;
            int lineNum = 0;
            //将文件内容全部拼接到 字符串s
            while ((lineTxt = br.readLine()) != null) {
                char[] charArray = lineTxt.toCharArray();
                length = charArray.length;
                for (int i = 0; i < charArray.length; i++) {
                    if (charArray[i] == ' ') {
                        cellArray[lineNum][i] = true;
                    }
                }
                lineNum++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isInBound(int x, int y) {
        return x >= 0 && x < length && y >= 0 && y < cellArray.length;
    }

    @Override
    public boolean isWalkable(int x, int y) {
        return cellArray[y][x];
    }

    public void printPath(List<Node> path) {
       Set<Node> pathSet = new HashSet<>(path);
        for (int y = 0; y < cellArray.length; y++) {
            boolean[] line = cellArray[y];
            for (int x = 0; x < line.length; x++) {
                if (!line[x]) {
                    System.out.print("#");
                } else {
                    if (pathSet.contains(new Node(x,y))) {
                        System.out.print("*");
                    }else{
                        System.out.print(" ");
                    }
                }
            }
            System.out.println();
        }
    }
}
