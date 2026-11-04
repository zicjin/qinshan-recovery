/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDTO;
import com.sunwayworld.module.sys.menu.bean.CoreMenuIconDTO;
import com.sunwayworld.module.sys.menu.resource.CoreMenuResource;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.menu.validator.CoreMenuDeleteValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u83dc\u5355")
@RestController
@GikamBean
public class CoreMenuResourceImpl
implements CoreMenuResource {
    @Autowired
    private CoreMenuService menuService;

    @Override
    public CoreMenuService getService() {
        return this.menuService;
    }

    @Override
    @Log(value="\u7f16\u8f91\u9875\u9762\u67e5\u8be2\u6811", type=LogType.SELECT)
    public List<CoreMenuDTO> selectEditTree(@PathVariable String parentId, RestJsonWrapperBean wrapper) {
        return this.getService().selectEditTree(parentId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u83dc\u5355", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u83dc\u5355", type=LogType.DELETE)
    @ValidateDataWith(value=CoreMenuDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u6811\u7ed3\u6784\u66f4\u6539\u6392\u5e8f", type=LogType.DELETE)
    public void swapOrderNo(@PathVariable String firstId, @PathVariable Long firstOrderNo, @PathVariable String secondId, @PathVariable Long secondOrderNo, RestJsonWrapperBean wrapper) {
        this.getService().swapOrderNo(firstId, firstOrderNo, secondId, secondOrderNo, wrapper);
    }

    @Override
    @Log(value="\u9009\u62e9\u9875\u9762\u67e5\u8be2\u6811", type=LogType.SELECT)
    public Page<CoreMenuDTO> selectTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectTreeList(wrapper, new Boolean[0]);
    }

    @Override
    @Log(value="\u62d6\u52a8\u83dc\u5355", type=LogType.UPDATE)
    public void drag(RestJsonWrapperBean wrapper) {
        this.getService().drag(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u83dc\u5355\u56fe\u6807", type=LogType.SELECT)
    public List<CoreMenuIconDTO> selectIconList() {
        return this.getService().selectIconList();
    }

    @Override
    @Log(value="\u67e5\u8be2\u5f85\u529e\u56fe\u6807", type=LogType.SELECT)
    public List<CoreMenuIconDTO> selectTodoIconList() {
        return this.getService().selectTodoIconList();
    }

    @Override
    @Log(value="\u67e5\u8be2\u5df2\u53d1\u5e03\u83dc\u5355\u660e\u7ec6", type=LogType.SELECT)
    public CoreMenuBean selectPublishedMenuDetail(String id) {
        return this.getService().selectPublishedMenuDetail(id);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u5df2\u53d1\u5e03\u7684\u83dc\u5355", type=LogType.UPDATE)
    public void savePublishedMenu(String id, RestJsonWrapperBean wrapper) {
        this.getService().savePublishedMenu(id, wrapper);
    }
}
