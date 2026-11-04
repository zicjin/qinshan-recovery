/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.tree;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GroupingTreeDescriptor<T> {
    private List<NodeMaker<T>> nodeMakerList;

    public GroupingTreeDescriptor(List<NodeMaker<T>> nodeMakerList) {
        this.nodeMakerList = nodeMakerList;
    }

    public List<NodeMaker<T>> getNodeMakerList() {
        return this.nodeMakerList;
    }

    public static interface NodeMaker<T> {
        public String getId(T var1);

        public String getText(T var1);

        default public void copy(T item, AbstractTreeNode node) {
        }

        default public <R extends AbstractTreeNode> Comparator<R> getNodeComparator() {
            return (r1, r2) -> Objects.toString(r1.getId(), "").compareTo(Objects.toString(r2.getId(), ""));
        }
    }
}
