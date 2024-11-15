package com.t13max.ai.btree.leaf;


import com.t13max.ai.btree.BTNode;
import com.t13max.ai.btree.LeafNode;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

/**
 * 用于行为树执行过程中的强制返回，即终止该行为树的全部执行
 *
 * @Author t13max
 * @Date 18:01 2024/5/17
 */
@Setter
public class EndNode<E> extends LeafNode<E> {
    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failure";

    private Status endStatus;

    private boolean interruptOutside;

    public EndNode() {
    }

    public EndNode(Status endStatus) {
        this.endStatus = endStatus;
        this.interruptOutside = false;
    }

    public EndNode(String status, boolean interruptOutside) {
        switch (status) {
            case SUCCESS:
                this.endStatus = Status.BT_SUCCESS;
                break;
            case FAILURE:
                this.endStatus = Status.BT_FAILURE;
                break;
            default:
                throw new IllegalArgumentException("入参错误, status : " + status);
        }
        this.interruptOutside = interruptOutside;
    }

    @Override
    public Status execute() {
        BTNode<E> rootNode;
        if (interruptOutside) {
            rootNode = getRootNode();
        } else {
            rootNode = tree.getRootNode();
        }
        rootNode.interrupt(endStatus);

        return Status.BT_CANCEL;
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        EndNode<E> endNode = (EndNode<E>) node;
        endNode.endStatus = this.endStatus;
        endNode.interruptOutside = this.interruptOutside;
    }

}
