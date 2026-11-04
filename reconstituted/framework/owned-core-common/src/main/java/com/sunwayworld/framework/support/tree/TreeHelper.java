package com.sunwayworld.framework.support.tree;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class TreeHelper {
   public static final <T, R extends AbstractTreeNode> List<R> parseTreeNode(List<T> treeItemList, TreeDescriptor<T> descriptor, Class<R> treeNodeClass) {
      if (treeItemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<T> cloneTreeItemList = new ArrayList<>();
         cloneTreeItemList.addAll(treeItemList);
         cloneTreeItemList.sort((b1, b2) -> descriptor.getOrderComparator().compare((T)b1, (T)b2));
         List<R> sortedNodeList = cloneTreeItemList.stream().map(t -> {
            R nodex = null;
            if (descriptor.getParseTreeNodeFunction() != null) {
               nodex = (R)descriptor.getParseTreeNodeFunction().apply((T)t);
            } else {
               nodex = ClassUtils.newInstance(treeNodeClass);
               BeanUtils.copyProperties(t, nodex);
            }

            String id = descriptor.getParseTreeNodeIdFunc().apply((T)t);
            nodex.setId(id);
            if (descriptor.getParseTreeNodeParentIdFunc() != null) {
               nodex.setParentId(descriptor.getParseTreeNodeParentIdFunc().apply((T)t));
            }

            nodex.setText(descriptor.getParseTreeNodeTextFunction().apply((T)t));

            for (BiConsumer<T, AbstractTreeNode> consumer : descriptor.getConsumerList()) {
               consumer.accept((T)t, nodex);
            }

            return nodex;
         }).collect(Collectors.toList());
         List<R> sortedRootNodeList = new ArrayList<>();
         if (descriptor.getParseTreeNodeParentIdFunc() != null) {
            CollectionUtils.sort(sortedNodeList, new Comparator<R>() {
               public int compare(R r1, R r2) {
                  if (r1.getId().equals(r2.getParentId())) {
                     return -1;
                  } else {
                     return r2.getId().equals(r1.getParentId()) ? 1 : 0;
                  }
               }
            });

            for (R node : sortedNodeList) {
               boolean hasParent = false;

               for (R innerLoopNode : sortedNodeList) {
                  if (innerLoopNode.getId().equals(node.getParentId())) {
                     if (innerLoopNode.getLvl() == null) {
                        innerLoopNode.setLvl(1);
                     }

                     innerLoopNode.addChild(node);
                     node.setLvl(innerLoopNode.getLvl() + 1);
                     hasParent = true;
                     break;
                  }
               }

               if (!hasParent) {
                  if (node.getLvl() == null) {
                     node.setLvl(1);
                  }

                  sortedRootNodeList.add(node);
               }
            }
         } else {
            for (R node : sortedNodeList) {
               int nodeLength = node.getId().length();
               R parentNode = null;

               for (R innerLoopNodex : sortedNodeList) {
                  int innerLoopNodeLength = innerLoopNodex.getId().length();
                  if (nodeLength > innerLoopNodeLength
                     && StringUtils.startsWith(node.getId(), innerLoopNodex.getId())
                     && (parentNode == null || innerLoopNodeLength > parentNode.getId().length())) {
                     parentNode = innerLoopNodex;
                  }
               }

               if (parentNode == null) {
                  if (node.getLvl() == null) {
                     node.setLvl(1);
                  }

                  sortedRootNodeList.add(node);
               } else {
                  if (parentNode.getLvl() == null) {
                     parentNode.setLvl(1);
                  }

                  parentNode.addChild(node);
                  node.setLvl(parentNode.getLvl() + 1);
                  node.setParentId(parentNode.getId());
               }
            }
         }

         return sortedRootNodeList;
      }
   }

   public static <R extends AbstractTreeNode> void updateChildQty(List<R> treeItemList) {
      for (R item : treeItemList) {
         if (item.getChildren() != null && !item.getChildren().isEmpty()) {
            item.setChildQty(item.getChildren().size());
            updateChildQty(item.getChildren());
         } else {
            item.setChildQty(0);
         }
      }
   }
}
