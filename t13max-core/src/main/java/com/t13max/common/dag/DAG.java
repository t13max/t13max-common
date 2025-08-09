package com.t13max.common.dag;

import java.util.LinkedList;
import java.util.TreeSet;

/**
 * 有向无圈图
 *
 * @Author: t13max
 * @Since: 17:09 2025/8/9
 */
public class DAG<T extends Comparable<T>> {

    public interface IWalk<T extends Comparable<T>> {
        void onNode(DAGNode<T> n);
    }

    private final TreeSet<DAGNode<T>> nodes;

    public DAG() {
        nodes = new TreeSet<>();
    }

    /**
     * 不存在则创建。存在则返回
     *
     * @Author: t13max
     * @Since: 17:09 2025/8/9
     */
    public DAGNode<T> createNodeIfNotExist(T name) {
        DAGNode<T> n;
        if ((n = getNode(name)) != null)
            return n;
        n = new DAGNode<T>(name, this);
        nodes.add(n);
        return n;
    }

    /**
     * 返回指定的节点
     *
     * @Author: t13max
     * @Since: 17:09 2025/8/9
     */
    public DAGNode<T> getNode(T name) {
        for (final DAGNode<T> n : nodes) {
            if (n.getObj().equals(name))
                return n;
        }
        return null;
    }

    /**
     * 遍历图
     *
     * @Author: t13max
     * @Since: 17:09 2025/8/9
     */
    public void walk(IWalk<T> w) {
        final LinkedList<DAGNode<T>> walked = new LinkedList<>();
        for (final DAGNode<T> n : nodes) {
            if (walked.contains(n))
                continue;
            DAGNode<T> curr = n;
            while (true) {
                final LinkedList<DAGNode<T>> parents = new LinkedList<>();
                for (int i = 0; i != curr.getPrevs().size(); ) {
                    final DAGNode<T> p = curr.getPrevs().get(i);
                    if (walked.contains(p)) {
                        ++i;
                        continue;
                    }
                    if (parents.contains(p))
                        throw new RuntimeException("找到循环, name: " + p.getObj());
                    parents.push(curr);
                    curr = p;
                    i = 0;
                }

                // curr没有父节点
                w.onNode(curr);
                walked.add(curr);

                if (!parents.isEmpty()) {
                    curr = parents.pollLast();
                    continue;
                } else {
                    // 遍历next
                    for (final DAGNode<T> p : curr.getNexts()) {
                        if (walked.contains(p))
                            continue;
                        curr = p;
                    }
                    break;
                }
            }
        }
    }

}
