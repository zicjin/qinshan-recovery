package com.sunwayworld.module.sys.menu.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.support.matrix.bean.MatrixHeaderBean;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDTO;
import com.sunwayworld.module.sys.menu.service.CoreMenuRoleService;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMenuRoleServiceImpl implements CoreMenuRoleService {
   @Autowired
   @Lazy
   private CoreRoleService coreRoleService;
   @Autowired
   @Lazy
   private CoreMenuService coreMenuService;
   @Autowired
   @Lazy
   private CoreMenuRoleService proxyInstance;
   @Autowired
   @Lazy
   private CoreRolePermissionService rolePermissionService;

   @Override
   public List<MatrixHeaderBean> selectMatrixHeaderList() {
      List<CoreRoleBean> roleList = this.coreRoleService.getDao().selectAll(Order.asc("ORDERNO"));
      return roleList.stream().map(r -> {
         MatrixHeaderBean header = MatrixHeaderBean.getRotateInstance(this.coreRoleService.getDao().getTable(), r.getId(), "ROLENAME", r.getRoleName());
         header.setJavaType(String.class.getSimpleName());
         header.setType("simpleCheckbox");
         header.setEditor(true);
         header.setReadonly(false);
         return header;
      }).collect(Collectors.toList());
   }

   @Override
   public Page<MatrixRowMap> selectMatrixRowPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      Page<CoreMenuDTO> menuTreePagination = this.coreMenuService.selectTreeList(wrapper, false);
      if (menuTreePagination.getRows().isEmpty()) {
         return new Pagination<>();
      } else {
         List<CoreRolePermissionBean> rolePermissionList = this.rolePermissionService
            .selectListByFilter(SearchFilter.instance().match("PERMISSIONTYPEID", "T_CORE_MENU").filter(MatchPattern.OR), new Order[0]);
         Map<String, List<CoreRolePermissionBean>> map = rolePermissionList.stream()
            .collect(Collectors.groupingBy(CoreRolePermissionBean::getTargetId, Collectors.toList()));
         List<MatrixRowMap> rowList = new ArrayList<>();

         for (CoreMenuDTO menu : menuTreePagination.getRows()) {
            boolean leaf = CollectionUtils.isEmpty(menu.getChildren());
            List<CoreRolePermissionBean> filteredRolePermissionList = map.get(menu.getId());
            MatrixRowMap row = new MatrixRowMap();
            row.putRowId(this.coreMenuService.getDao().getTable(), menu.getId());
            row.putItem(menu);
            if (filteredRolePermissionList != null && !filteredRolePermissionList.isEmpty()) {
               if (leaf) {
                  filteredRolePermissionList.forEach(ru -> row.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1"));
               } else {
                  List<String> childrenMenuIdList = this.getAllChildren(menu).stream().map(e -> e.getId()).collect(Collectors.toList());
                  filteredRolePermissionList.forEach(
                     ru -> {
                        List<String> menuIdList = rolePermissionList.stream()
                           .filter(f -> f.getRoleId().equals(ru.getRoleId()))
                           .map(e -> e.getTargetId())
                           .collect(Collectors.toList());
                        if (childrenMenuIdList.stream().anyMatch(f -> !menuIdList.contains(f))) {
                           row.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "2");
                        } else {
                           row.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1");
                        }
                     }
                  );
               }

               row.setPredicate(
                  t -> MatchPattern.EQ.name().equals(t.getMatchPattern())
                     ? t.getMap()
                        .entrySet()
                        .stream()
                        .allMatch(
                           e -> {
                              Object v = CollectionUtils.getValueIgnorecase(row, e.getKey());
                              return StringUtils.startsWith(e.getKey(), this.coreRoleService.getDao().getTable()) && "0".equals(e.getValue())
                                 ? v == null || "0".equals(v)
                                 : ObjectUtils.equals(v, e.getValue());
                           }
                        )
                     : t.match(row)
               );
            }

            this.buildChildMixRow(row, menu.getChildren(), map, rolePermissionList);
            rowList.add(row);
         }

         List<MatrixRowMap> filteredRowList = MatrixHelper.getFilteredRowList(parameter, rowList);
         MatrixHelper.sortRowList(parameter, filteredRowList);
         Page<MatrixRowMap> matrixRowPagination = new Pagination<>(filteredRowList);
         matrixRowPagination.setColumnTypeMap(menuTreePagination.getColumnTypeMap());
         List<MatrixHeaderBean> headerList = this.selectMatrixHeaderList();
         headerList.forEach(m -> {
            String var10000 = matrixRowPagination.getColumnTypeMap().put(m.getField(), m.getJavaType().toLowerCase());
         });
         return matrixRowPagination;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.INSTANT_SAVE")
   @Override
   public void instantSave(RestJsonWrapperBean wrapper) {
      List<String> contentList = wrapper.getBodyList().get(0);
      this.proxyInstance.save(contentList.stream().map(c -> JSON.parseObject(c)).collect(Collectors.toList()));
   }

   @Transactional
   @Override
   public void save(List<Map<String, Object>> mapList) {
      List<String> menuIdList = mapList.stream().map(e -> (String)CollectionUtils.getValueIgnorecase((Map<String, ?>)e, "id")).collect(Collectors.toList());
      String key = mapList.get(0).keySet().stream().filter(k -> k.startsWith("T_CORE_ROLE$ROLENAME$")).findAny().get();
      Long roleId = NumberUtils.parseLong(StringUtils.removeStart(key, "T_CORE_ROLE$ROLENAME$"));
      List<CoreRolePermissionBean> rolePermisionList = this.rolePermissionService
         .selectListByFilter(
            SearchFilter.instance()
               .match("PERMISSIONTYPEID", "T_CORE_MENU")
               .filter(MatchPattern.SEQ)
               .match("ROLEID", roleId)
               .filter(MatchPattern.EQ)
               .match("TARGETID", menuIdList)
               .filter(MatchPattern.OR),
            new Order[0]
         );
      List<String> oriMenuIdList = rolePermisionList.stream().map(e -> e.getTargetId()).collect(Collectors.toList());
      if ("1".equals(mapList.get(0).get(key))) {
         List<String> menuIdList4Insert = menuIdList.stream().filter(f -> !oriMenuIdList.contains(f)).collect(Collectors.toList());
         this.rolePermissionService.insert(roleId, "T_CORE_MENU", menuIdList4Insert);
      } else {
         List<String> menuIdList4Delete = menuIdList.stream().filter(f -> oriMenuIdList.contains(f)).collect(Collectors.toList());
         this.rolePermissionService.delete(roleId, "T_CORE_MENU", menuIdList4Delete);
      }
   }

   private List<CoreMenuDTO> getAllChildren(CoreMenuDTO menuDTO) {
      List<CoreMenuDTO> menuDTOList = new ArrayList<>();
      if (CollectionUtils.isEmpty(menuDTO.getChildren())) {
         return menuDTOList;
      } else {
         List<CoreMenuDTO> children = menuDTO.getChildren().stream().map(e -> (CoreMenuDTO)e).collect(Collectors.toList());
         menuDTOList.addAll(children);
         children.forEach(e -> menuDTOList.addAll(this.getAllChildren(e)));
         return menuDTOList;
      }
   }

   private void buildChildMixRow(
      MatrixRowMap row, List<AbstractTreeNode> treeNodeList, Map<String, List<CoreRolePermissionBean>> map, List<CoreRolePermissionBean> rolePermissionList
   ) {
      if (treeNodeList != null && !treeNodeList.isEmpty()) {
         List<MatrixRowMap> childRowList = new ArrayList<>();

         for (AbstractTreeNode child : treeNodeList) {
            CoreMenuDTO menuDTO = (CoreMenuDTO)child;
            boolean leaf = CollectionUtils.isEmpty(menuDTO.getChildren());
            List<CoreRolePermissionBean> filteredRolePermissionList = map.get(menuDTO.getId());
            MatrixRowMap childRow = new MatrixRowMap();
            childRow.putRowId(this.coreMenuService.getDao().getTable(), menuDTO.getId());
            childRow.putItem(menuDTO);
            if (filteredRolePermissionList != null && !filteredRolePermissionList.isEmpty()) {
               if (leaf) {
                  filteredRolePermissionList.forEach(ru -> childRow.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1"));
               } else {
                  List<String> childrenMenuIdList = this.getAllChildren(menuDTO).stream().map(e -> e.getId()).collect(Collectors.toList());
                  filteredRolePermissionList.forEach(
                     ru -> {
                        List<String> menuIdList = rolePermissionList.stream()
                           .filter(f -> f.getRoleId().equals(ru.getRoleId()))
                           .map(e -> e.getTargetId())
                           .collect(Collectors.toList());
                        if (childrenMenuIdList.stream().anyMatch(f -> !menuIdList.contains(f))) {
                           childRow.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "2");
                        } else {
                           childRow.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1");
                        }
                     }
                  );
               }

               childRow.setPredicate(
                  t -> MatchPattern.EQ.name().equals(t.getMatchPattern())
                     ? t.getMap()
                        .entrySet()
                        .stream()
                        .allMatch(
                           e -> {
                              Object v = CollectionUtils.getValueIgnorecase(childRow, e.getKey());
                              return StringUtils.startsWith(e.getKey(), this.coreRoleService.getDao().getTable()) && "0".equals(e.getValue())
                                 ? v == null || "0".equals(v)
                                 : ObjectUtils.equals(v, e.getValue());
                           }
                        )
                     : t.match(childRow)
               );
            }

            childRowList.add(childRow);
            this.buildChildMixRow(childRow, menuDTO.getChildren(), map, rolePermissionList);
         }

         row.put("children", childRowList);
      }
   }
}
