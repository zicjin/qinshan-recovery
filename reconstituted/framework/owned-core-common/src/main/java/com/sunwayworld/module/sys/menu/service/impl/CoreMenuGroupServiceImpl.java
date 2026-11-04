/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupLineBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupLineRepresentation;
import com.sunwayworld.module.sys.menu.dao.CoreMenuGroupDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuGroupLineService;
import com.sunwayworld.module.sys.menu.service.CoreMenuGroupService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreMenuGroupServiceImpl
implements CoreMenuGroupService {
    @Autowired
    private CoreMenuGroupDao groupDao;
    @Autowired
    private CoreMenuGroupLineService lineService;

    @Override
    public CoreMenuGroupDao getDao() {
        return this.groupDao;
    }

    @Override
    @Transactional
    public String insert(RestJsonWrapperBean wrapper) {
        CoreMenuGroupBean group = wrapper.parseUnique(CoreMenuGroupBean.class);
        group.setId(ApplicationContextHelper.getNextSequence("T_CORE_MENU_GROUP"));
        this.getDao().insert(group);
        return group.getId();
    }

    @Override
    @Transactional
    public void insertLines(String id, RestJsonWrapperBean wrapper) {
        List<CoreMenuGroupLineBean> lineList = wrapper.parse(CoreMenuGroupLineBean.class);
        int qty = lineList.size();
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
        for (int i = 0; i < qty; ++i) {
            CoreMenuGroupLineBean line = lineList.get(i);
            line.setId(idList.get(i));
            line.setGroupId(id);
        }
        this.lineService.getDao().insert(lineList);
    }

    @Override
    @Transactional
    public void deleteLines(String id, RestJsonWrapperBean wrapper) {
        List<Long> lineIdList = wrapper.parseId(Long.class);
        HashSet<Long> selectedLineIdList = new HashSet<Long>(lineIdList);
        lineIdList.forEach(i -> selectedLineIdList.addAll(this.selectAllSubLineIdList((Long)i)));
        this.lineService.getDao().deleteByIdList(selectedLineIdList.stream().collect(Collectors.toList()));
    }

    @Override
    public Page<CoreMenuGroupLineRepresentation> selectLineTreePagination(String id, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("GROUPID", id).filter(MatchPattern.SEQ));
        List<Map<String, Object>> mapList = this.lineService.getDao().selectByCondition(parameter);
        if (mapList.isEmpty()) {
            return new Pagination<CoreMenuGroupLineRepresentation>(CollectionUtils.emptyList());
        }
        List groupLineList = mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreMenuGroupLineRepresentation.class)).collect(Collectors.toList());
        TreeDescriptor<CoreMenuGroupLineRepresentation> descriptor = new TreeDescriptor<CoreMenuGroupLineRepresentation>("id", "parentId", "menuName");
        descriptor.addConsumer((t, n) -> n.setExpanded(true));
        List<CoreMenuGroupLineRepresentation> treeNodeList = TreeHelper.parseTreeNode(groupLineList, descriptor, CoreMenuGroupLineRepresentation.class);
        treeNodeList.forEach(n -> {
            if (!StringUtils.isEmpty(n.getParam())) {
                String href = n.getExt$().get("href");
                if (StringUtils.contains(href, "?")) {
                    n.setExt$Item("href", href + "&" + n.getParam());
                } else {
                    n.setExt$Item("href", href + "?" + n.getParam());
                }
            }
            n.setHref("/secure/core/module/sys/menu-groups/" + id + "/menu-group-lines/tree-grid");
        });
        return new Pagination<CoreMenuGroupLineRepresentation>(treeNodeList);
    }

    private List<Long> selectAllSubLineIdList(Long lineId) {
        CoreMenuGroupLineBean filter = new CoreMenuGroupLineBean();
        filter.setParentId(lineId);
        List<Long> childIdList = this.lineService.getDao().selectIdList((CoreMenuGroupLineBean)filter, new Order[0]);
        if (!childIdList.isEmpty()) {
            for (Long childId : new ArrayList<Long>(childIdList)) {
                childIdList.addAll(this.selectAllSubLineIdList(childId));
            }
        }
        return childIdList;
    }
}
