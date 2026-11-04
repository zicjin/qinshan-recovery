/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigIconDTO;
import com.sunwayworld.module.sys.todo.dao.CoreTodoConfigDao;
import com.sunwayworld.module.sys.todo.service.CoreTodoConfigService;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreTodoConfigServiceImpl
implements CoreTodoConfigService {
    @Autowired
    private CoreTodoConfigDao configDao;

    @Override
    public CoreTodoConfigDao getDao() {
        return this.configDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreTodoConfigBean config = wrapper.parseUnique(CoreTodoConfigBean.class);
        config.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(config);
        return config.getId();
    }

    @Override
    @Cacheable(value={"T_CORE_TODO_CONFIG"}, key="'ACTIVATED'")
    public List<CoreTodoConfigBean> selectActivatedConfigList() {
        CoreTodoConfigBean filter = new CoreTodoConfigBean();
        filter.setActivatedFlag("1");
        return this.selectList(filter, new Order[0]);
    }

    @Override
    public List<CoreTodoConfigIconDTO> selectIconList() {
        List<Resource> iconResourceList = ClassUtils.getResourceList("/static/icon/todo/", new Predicate[0]);
        return iconResourceList.stream().map(i -> {
            CoreTodoConfigIconDTO icon = new CoreTodoConfigIconDTO();
            icon.setIconName(i.getFilename());
            icon.setIconUrl("/static/icon/todo/" + i.getFilename());
            return icon;
        }).collect(Collectors.toList());
    }

    @Override
    public CoreTodoConfigBean selectDetail(Long id) {
        CoreTodoConfigBean todoConfigBean = PersistableHelper.mapToPersistable(this.getDao().selectDetail(id), CoreTodoConfigBean.class);
        todoConfigBean.setExt$Item("todoIconUrl", this.getTodoIconUrl(todoConfigBean.getIcon()));
        return todoConfigBean;
    }

    private String getTodoIconUrl(String menuIcon) {
        if (StringUtils.isEmpty(menuIcon)) {
            return null;
        }
        return "/static/icon/todo/" + menuIcon;
    }
}
