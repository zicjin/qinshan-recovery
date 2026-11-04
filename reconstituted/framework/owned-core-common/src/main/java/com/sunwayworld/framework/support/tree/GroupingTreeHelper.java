package com.sunwayworld.framework.support.tree;

import com.sunwayworld.framework.support.tree.GroupingTreeDescriptor.NodeMaker;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class GroupingTreeHelper {
   public static final <T, R extends AbstractTreeNode> List<R> parseTreeNode(List<T> treeItemList, GroupingTreeDescriptor<T> descriptor, Class<R> treeNodeClass) {
      if (treeItemList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         Map<String, R> map = new HashMap<>();

         for (T item : treeItemList) {
            String parentId = null;
            int lvl = 1;

            for (NodeMaker<T> nodeMaker : descriptor.getNodeMakerList()) {
               String id = nodeMaker.getId(item);
               String key = id + "$" + lvl;
               if (!map.containsKey(key)) {
                  R node = (R)ClassUtils.newInstance(treeNodeClass);
                  node.setId(id);
                  node.setText(nodeMaker.getText(item));
                  node.setParentId(parentId);
                  node.setLvl(lvl);
                  nodeMaker.copy(item, node);
                  map.put(key, node);
               }

               parentId = id;
               lvl++;
            }
         }

         List<R> treeNodeList = new ArrayList<>(map.values());

         for (R treeNode : treeNodeList) {
            if (!StringUtils.isEmpty(treeNode.getParentId())) {
               R parentTreeNode = (R)treeNodeList.stream()
                  .filter(n -> Objects.toString(n.getId(), "").equals(Objects.toString(treeNode.getParentId(), "")))
                  .findFirst()
                  .get();
               parentTreeNode.addChild(treeNode);
            }
         }

         treeNodeList.removeIf(t -> !StringUtils.isEmpty(t.getParentId()));
         sortTreeNodeList(treeNodeList, descriptor);
         return treeNodeList;
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

   private static <T, R extends AbstractTreeNode> void sortTreeNodeList(List<R> treeNodeList, GroupingTreeDescriptor<T> descriptor) {
      if (treeNodeList != null && !treeNodeList.isEmpty()) {
         int lvl = treeNodeList.get(0).getLvl();
         NodeMaker<T> nodeMarker = descriptor.getNodeMakerList().get(lvl - 1);
         treeNodeList.sort(nodeMarker.getNodeComparator());

         for (R treeNode : treeNodeList) {
            sortTreeNodeList(treeNode.getChildren(), descriptor);
         }
      }
   }
}
