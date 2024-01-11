package com.sunwayworld.emc.module.mdm.basemdm.containercategory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.containercategory.resource.EmcContainerCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "容器类型")
@RestController
@GikamBean
public class EmcContainerCategoryResourceImpl implements EmcContainerCategoryResource{

    @Autowired
    private EmcContainerCategoryService containerCategoryService;
    
    @Override
    public EmcContainerCategoryService getService() {
        return containerCategoryService;
    }

    @Log(value = "新增测试样品类型", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{ids}/add", method = RequestMethod.POST)
    public void insertOrderCategory(@PathVariable String ids, RestJsonWrapperBean wrapper) {
        getService().insertOrderCategory(ids, wrapper);
    }
    
    @Log(value = "删除测试样品类型", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/{ids}/dele-order-categories",  method = RequestMethod.DELETE)
    public void deleteOrderCategory(@PathVariable String ids,RestJsonWrapperBean wrapper) {
        getService().deleteOrderCategory(ids);
    }
    
    @Log(value = "复制测试样品类型", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{ids}/{monitortmplid}/copy",  method = RequestMethod.POST)
    public void copyOrderCategory(@PathVariable String ids, @PathVariable String monitortmplid,RestJsonWrapperBean wrapper) {
        getService().copyOrderCategory(ids, monitortmplid,wrapper);
    }
    
    @Log(value = "添加检测项目", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/add/{testmethodids}",  method = RequestMethod.POST)
    public void insertByTMId(@PathVariable String testmethodids, RestJsonWrapperBean wrapper) {
        getService().insertByTMId(testmethodids, wrapper);
    }
    
    @Log(value = "右侧删除测试样品类型", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/{ids}/{testmethodids}/dele-order-categories",  method = RequestMethod.DELETE)
    public void delOrderCategory(@PathVariable String ids,@PathVariable String testmethodids) {
        getService().delOrderCategory(ids, testmethodids);
    }
    
    @Log(value = "移动", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/move/{oldIds}/{moveIds}",  method = RequestMethod.POST)
    public void moveToOther(@PathVariable String oldIds , @PathVariable String moveIds, RestJsonWrapperBean wrapper) {
        getService().moveToOther(oldIds, moveIds, wrapper);
    }
    
    
    @Log(value = "修改", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/modify/{type}/{ids}",  method = RequestMethod.POST)
    public void updateOrderCategory(@PathVariable String type, @PathVariable String ids, RestJsonWrapperBean wrapper) {
        getService().updateOrderCategory(type, ids, wrapper);
    }
}
