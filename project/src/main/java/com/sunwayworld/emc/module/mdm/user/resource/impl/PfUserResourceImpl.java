package com.sunwayworld.emc.module.mdm.user.resource.impl;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.emc.module.mdm.user.resource.PfUserResource;
import com.sunwayworld.emc.module.mdm.user.service.PfUserService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.resource.impl.CoreUserResourceImpl;

/**
 * @author licm@sunwayworld.com 2021年09月10日
 */
@LogModule("用户管理")
@RestController
@GikamBean
public class PfUserResourceImpl extends CoreUserResourceImpl implements PfUserResource {
	
    @Autowired
    private PfUserService userService;
    
    @Override
    public PfUserService getService() {
        return userService;
    }

    @Override
    @Log(value = "最大值id+1", type = LogType.UPDATE)
    @RequestMapping(value = "/action/select-maxid", method = RequestMethod.POST)
    public String selectMaxId(RestJsonWrapperBean wrapper) {
    	return userService.selectMaxId(wrapper);
    }
    

    @Override
    @Log(value = "按科室查找用户并插入用户权限表", type = LogType.UPDATE)
    @RequestMapping(value = "/action/insert-report-config-by-dept", method = RequestMethod.POST)
    public void insertReportConfigByDept(RestJsonWrapperBean wrapper) {
    	  userService.insertReportConfigByDept(wrapper);
    }

    @Override
    @Log(value = "按科室查找用户并删除用户权限表", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-report-config-by-dept", method = RequestMethod.POST)
    public void deleteReportConfigByDept(RestJsonWrapperBean wrapper) {
    	  userService.deleteReportConfigByDept(wrapper);
    }
}
