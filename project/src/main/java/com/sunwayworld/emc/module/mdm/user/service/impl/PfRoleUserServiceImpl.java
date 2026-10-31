/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.mdm.user.service.impl;

import com.sunwayworld.emc.module.mdm.user.service.PfRoleUserService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.support.matrix.bean.MatrixHeaderBean;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import com.sunwayworld.module.sys.role.service.impl.CoreRoleUserServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class PfRoleUserServiceImpl
extends CoreRoleUserServiceImpl
implements PfRoleUserService {
    @Lazy
    @Autowired
    private CoreRoleService coreRoleService;
    @Lazy
    @Autowired
    private CoreUserService coreUserService;
    @Lazy
    @Autowired
    private CoreRoleUserService proxyInstance;

    public Page<CoreRoleUserBean> selectPaginationByRole(Long roleId, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String orgId = user.getExt$Item("parentorgid");
        parameter.setFilter(SearchFilter.instance().match("ROLEID", (Object)roleId).filter(MatchPattern.EQ).match("PARENTORGID", (Object)orgId).filter(MatchPattern.SEQ));
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }

    public Page<MatrixRowMap> selectMatrixRowPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        Page<CoreUserBean> userPage = this.coreUserService.selectPagination(wrapper);
        List<CoreUserBean> coreUserList = userPage.getRows();
        if (coreUserList.isEmpty()) {
            return new Pagination();
        }
        List<String> userIdList = coreUserList.stream().map(CoreUserBean::getId).collect(Collectors.toList());
        List<CoreRoleUserBean> allRoleUserList = this.proxyInstance.selectAll();
        List<CoreRoleUserBean> roleUserList = allRoleUserList.stream().filter(e -> userIdList.contains(e.getUserId())).collect(Collectors.toList());
        Map<String, List<CoreRoleUserBean>> map = roleUserList.stream().collect(Collectors.groupingBy(CoreRoleUserBean::getUserId, Collectors.toList()));
        List<MatrixRowMap> rowList = new ArrayList<MatrixRowMap>();
        for (CoreUserBean user : coreUserList) {
            List<CoreRoleUserBean> filteredRoleList = map.get(user.getId());
            MatrixRowMap row = new MatrixRowMap();
            row.putRowId(this.coreUserService.getDao().getTable(), (Object)user.getId());
            row.putItem((Object)user);
            if (filteredRoleList != null && !filteredRoleList.isEmpty()) {
                filteredRoleList.forEach(ru -> row.putRotateValue(this.coreRoleService.getDao().getTable(), (Object)ru.getRoleId(), "ROLENAME", (Object)"1"));
                row.setPredicate(t -> {
                    if (MatchPattern.EQ.name().equals(t.getMatchPattern())) {
                        return t.getMap().entrySet().stream().allMatch(e -> {
                            Object v = CollectionUtils.getValueIgnorecase((Map)row, (String)((String)e.getKey()));
                            if (StringUtils.startsWith((String)((String)e.getKey()), (String)this.coreRoleService.getDao().getTable()) && "0".equals(e.getValue())) {
                                return v == null || "0".equals(v);
                            }
                            return ObjectUtils.equals((Object)v, e.getValue());
                        });
                    }
                    return t.match((Map)row);
                });
            }
            rowList.add(row);
        }
        List<MatrixRowMap> filteredRowList = MatrixHelper.getFilteredRowList((MapperParameter)parameter, rowList);
        MatrixHelper.sortRowList((MapperParameter)parameter, (List)filteredRowList);
        Page<MatrixRowMap> matrixRowPagination = new Pagination<>(filteredRowList);
        matrixRowPagination.setTotal(userPage.getTotal());
        matrixRowPagination.setPageNumber(userPage.getPageNumber());
        Map<String, String> userColumnTypeMap = new HashMap<String, String>();
        userColumnTypeMap.put("id", "string");
        userColumnTypeMap.put("username", "string");
        matrixRowPagination.setColumnTypeMap(userColumnTypeMap);
        List<MatrixHeaderBean> headerList = this.selectMatrixHeaderList();
        headerList.forEach(m -> matrixRowPagination.getColumnTypeMap().put(m.getField(), m.getJavaType().toLowerCase()));
        return matrixRowPagination;
    }
}
