/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.validator;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuDao;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreMenuDeleteValidator
implements DataValidator {
    @Autowired
    private CoreMenuDao coreMenuDao;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        MapperParameter parameter = wrapper.extractMapFilter();
        CoreMenuBean coreMenu = wrapper.parseUnique(CoreMenuBean.class);
        parameter.put("parentId", coreMenu.getId());
        List menuList = this.coreMenuDao.selectTreeNodeList(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, CoreMenuBean.class)).collect(Collectors.toList());
        boolean needRole = true;
        if (!menuList.isEmpty()) {
            needRole = false;
        }
        if (!needRole) {
            this.addConstraintViolation("CORE.MODULE.SYS.MENU.VALIDATOR.DELETE_ONELEVEL_MENU");
        }
        return needRole;
    }
}
