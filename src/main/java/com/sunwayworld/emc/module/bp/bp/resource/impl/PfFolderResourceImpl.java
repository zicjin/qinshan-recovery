package com.sunwayworld.emc.module.bp.bp.resource.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.resource.PfFolderResource;
import com.sunwayworld.emc.module.bp.bp.service.PfFolderService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com
 * @date 2021/11/04 
 */
@RestController
@GikamBean
public class PfFolderResourceImpl extends EmcFolderResourceImpl implements PfFolderResource {

    @Autowired
    @Lazy
    private PfFolderService pfFolderService;

    @Override
    @Log(value = "提示信息", type = LogType.SELECT)
    @RequestMapping(value = "/action/submit-remind", method = RequestMethod.POST)
    public String submitRemind(RestJsonWrapperBean wrapper) {
    	return pfFolderService.submitRemind(wrapper);
    }

    @Override
    @Log(value = "采样安排按任务批量提交", type = LogType.SELECT)
    @RequestMapping(value = "/action/submit-project", method = RequestMethod.POST)
    public Long submitProject(RestJsonWrapperBean wrapper) {
        pfFolderService.submitProject(wrapper);
        return 1L;
    }

    @Log(value = "批量修改点位信息", type = LogType.SELECT)
    @RequestMapping(value = "/action/update-by-project", method = RequestMethod.POST)
    public Long updateByProject(RestJsonWrapperBean wrapper) {
        pfFolderService.updateByProject(wrapper);
        return 1L;
    }

    @Override
    @Log(value = "验证数据", type = LogType.VALIDATE)
    @RequestMapping(value = "/{id}/action/validate/{columnName}/{columnValue}", method = RequestMethod.POST)
    public RestValidationResultBean validateUnique(@PathVariable(required = false) Long id, @PathVariable(required = false) String columnName, @PathVariable(required = false) String columnValue) {
        return pfFolderService.customizeValidate(id, columnName, columnValue);
    }

    @Log(value = "点位重新编码", type = LogType.UPDATE)
    @RequestMapping(value = "/sort-folderno", method = RequestMethod.PUT)
    public Long sortFolderNo(RestJsonWrapperBean wrapper) {
        pfFolderService.sortFolderNo(wrapper);
        return 1L;
    }

}
