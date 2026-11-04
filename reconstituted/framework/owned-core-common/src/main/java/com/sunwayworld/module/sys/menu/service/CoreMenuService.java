/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuDTO;
import com.sunwayworld.module.sys.menu.bean.CoreMenuIconDTO;
import java.util.List;

public interface CoreMenuService
extends GenericTreeService<CoreMenuBean, String>,
GenericService<CoreMenuBean, String>,
GenericChoosableService<CoreMenuBean, String> {
    public String getReplacedHref(CoreMenuBean var1);

    public List<CoreMenuBean> selectCurrentUserMenuList();

    public List<CoreMenuDTO> selectEditTree(String var1, RestJsonWrapperBean var2);

    public void swapOrderNo(String var1, Long var2, String var3, Long var4, RestJsonWrapperBean var5);

    public Page<CoreMenuDTO> selectTreeList(RestJsonWrapperBean var1, Boolean ... var2);

    public void drag(RestJsonWrapperBean var1);

    public List<CoreMenuIconDTO> selectIconList();

    public List<CoreMenuIconDTO> selectTodoIconList();

    public CoreMenuBean selectPublishedMenuDetail(String var1);

    public void savePublishedMenu(String var1, RestJsonWrapperBean var2);
}
