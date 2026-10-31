package com.sunwayworld.framework.data;

import java.io.Serializable;

/** Mutable three-value tuple. */
public class Triple<L, M, R> implements Serializable {
    private static final long serialVersionUID = -7920891595758591838L;
    private L left;
    private M middle;
    private R right;

    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new Triple<L, M, R>(left, middle, right);
    }

    private Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public L getLeft() { return left; }
    public void setLeft(L left) { this.left = left; }
    public M getMiddle() { return middle; }
    public void setMiddle(M middle) { this.middle = middle; }
    public R getRight() { return right; }
    public void setRight(R right) { this.right = right; }
}
