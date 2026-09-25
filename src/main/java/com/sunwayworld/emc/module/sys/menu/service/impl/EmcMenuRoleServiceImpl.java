/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.sys.menu.service.impl;

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
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDTO;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.menu.service.impl.CoreMenuRoleServiceImpl;
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

@Repository
@GikamBean
public class EmcMenuRoleServiceImpl
extends CoreMenuRoleServiceImpl {
    @Autowired
    @Lazy
    private CoreRoleService coreRoleService;
    @Autowired
    @Lazy
    private CoreMenuService coreMenuService;
    @Autowired
    @Lazy
    private CoreRolePermissionService rolePermissionService;

    @Override
    public Page<MatrixRowMap> selectMatrixRowPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        Page<CoreMenuDTO> menuTreePagination = this.coreMenuService.selectTreeList(wrapper, false);
        if (menuTreePagination.getRows().isEmpty()) {
            return new Pagination<MatrixRowMap>();
        }
        List<CoreRolePermissionBean> rolePermissionList = this.rolePermissionService.selectListByFilter(SearchFilter.instance().match("PERMISSIONTYPEID", "T_CORE_MENU").filter(MatchPattern.OR), new Order[0]);
        Map<String, List<CoreRolePermissionBean>> map = rolePermissionList.stream().collect(Collectors.groupingBy(CoreRolePermissionBean::getTargetId, Collectors.toList()));
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
                    filteredRolePermissionList.forEach(ru -> {
                        List<String> menuIdList = rolePermissionList.stream().filter(f -> f.getRoleId().equals(ru.getRoleId())).map(e -> e.getTargetId()).collect(Collectors.toList());
                        if (childrenMenuIdList.stream().anyMatch(f -> !menuIdList.contains(f))) {
                            row.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "2");
                        } else {
                            row.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1");
                        }
                    });
                }
                row.setPredicate(t -> {
                    if (MatchPattern.EQ.name().equals(t.getMatchPattern())) {
                        return t.getMap().entrySet().stream().allMatch(e -> {
                            Object v = CollectionUtils.getValueIgnorecase(row, (String)e.getKey());
                            if (StringUtils.startsWith((String)e.getKey(), this.coreRoleService.getDao().getTable()) && "0".equals(e.getValue())) {
                                return v == null || "0".equals(v);
                            }
                            return ObjectUtils.equals(v, e.getValue());
                        });
                    }
                    return t.match(row);
                });
            }
            this.buildChildMixRow(row, menu.getChildren(), map, rolePermissionList);
            rowList.add(row);
        }
        List<MatrixRowMap> filteredRowList = MatrixHelper.getFilteredRowList(parameter, rowList);
        MatrixHelper.sortRowList(parameter, filteredRowList);
        Page<MatrixRowMap> matrixRowPagination = new Pagination<>(filteredRowList);
        matrixRowPagination.setTotal(menuTreePagination.getTotal());
        matrixRowPagination.setPageNumber(menuTreePagination.getPageNumber());
        matrixRowPagination.setColumnTypeMap(menuTreePagination.getColumnTypeMap());
        List<MatrixHeaderBean> headerList = this.selectMatrixHeaderList();
        headerList.forEach(m -> matrixRowPagination.getColumnTypeMap().put(m.getField(), m.getJavaType().toLowerCase()));
        return matrixRowPagination;
    }

    private void buildChildMixRow(MatrixRowMap row, List<AbstractTreeNode> treeNodeList, Map<String, List<CoreRolePermissionBean>> map, List<CoreRolePermissionBean> rolePermissionList) {
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
                        filteredRolePermissionList.forEach(ru -> {
                            List<String> menuIdList = rolePermissionList.stream().filter(f -> f.getRoleId().equals(ru.getRoleId())).map(e -> e.getTargetId()).collect(Collectors.toList());
                            if (childrenMenuIdList.stream().anyMatch(f -> !menuIdList.contains(f))) {
                                childRow.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "2");
                            } else {
                                childRow.putRotateValue(this.coreRoleService.getDao().getTable(), ru.getRoleId(), "ROLENAME", "1");
                            }
                        });
                    }
                    childRow.setPredicate(t -> {
                        if (MatchPattern.EQ.name().equals(t.getMatchPattern())) {
                            return t.getMap().entrySet().stream().allMatch(e -> {
                                Object v = CollectionUtils.getValueIgnorecase(childRow, (String)e.getKey());
                                if (StringUtils.startsWith((String)e.getKey(), this.coreRoleService.getDao().getTable()) && "0".equals(e.getValue())) {
                                    return v == null || "0".equals(v);
                                }
                                return ObjectUtils.equals(v, e.getValue());
                            });
                        }
                        return t.match(childRow);
                    });
                }
                childRowList.add(childRow);
                this.buildChildMixRow(childRow, menuDTO.getChildren(), map, rolePermissionList);
            }
            row.put("children", childRowList);
        }
    }

    private List<CoreMenuDTO> getAllChildren(CoreMenuDTO menuDTO) {
        List<CoreMenuDTO> menuDTOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(menuDTO.getChildren())) {
            return menuDTOList;
        }
        List<CoreMenuDTO> children = menuDTO.getChildren().stream().map(e -> (CoreMenuDTO)e).collect(Collectors.toList());
        menuDTOList.addAll(children);
        children.forEach(e -> menuDTOList.addAll(this.getAllChildren((CoreMenuDTO)e)));
        return menuDTOList;
    }
}
