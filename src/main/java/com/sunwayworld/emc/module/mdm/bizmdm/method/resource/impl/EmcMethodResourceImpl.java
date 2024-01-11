package com.sunwayworld.emc.module.mdm.bizmdm.method.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.resource.EmcMethodResource;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@LogModule("方法")
@RestController
@GikamBean
public class EmcMethodResourceImpl implements EmcMethodResource {
    @Autowired
    private EmcMethodService methodService;

    @Autowired
    private EmcMethodVersionService methodVersionService;

    @Override
    public EmcMethodService getService() {
        return methodService;
    }

    @Log(value = "新增方法版本", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/versions", method = RequestMethod.POST)
    public Long insertVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return methodVersionService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除方法版本", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/versions", method = RequestMethod.DELETE)
    public void deleteVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        methodVersionService.deleteVersion(id, wrapper);
    }

    @Log(value = "通过方法查询方法版本", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/versions/queries", method = RequestMethod.POST)
    public Page<EmcMethodVersionBean> selectVersionPagationByMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return methodVersionService.selectPaginationByFilter(SearchFilter.instance().match("METHODID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "修改方法的最大版本信息", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/action/update-version", method = RequestMethod.PUT)
    public void updateVersionInfo(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        methodService.updateVersionInfo(id);
    }

    @Log(value = "查询方法版本详细信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/versions/{id}", method = RequestMethod.GET)
    public EmcMethodVersionBean selectMethodVersionDetail(@PathVariable Long id) {
        return methodVersionService.selectDetail(id);
    }

    @Log(value = "校验方法是否被使用", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/action/check-used", method = RequestMethod.POST)
    public List<Long> methodCheckUsed(RestJsonWrapperBean wrapper) {
        return methodService.checkUsed(wrapper);
    }

    @Log(value = "查询易标准", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/estand-datas/queries", method = RequestMethod.POST)
    public Page<EmcMethodBean> getEstandard(RestJsonWrapperBean wrapper) {
        return methodService.getEstandard(wrapper);
    }

    @Log(value = "同步易标准数据", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/estand-datas/action/sync", method = RequestMethod.POST)
    public void estandSync(RestJsonWrapperBean wrapper) {
        methodService.estandSync(wrapper);
    }

}
