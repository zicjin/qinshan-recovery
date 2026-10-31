package com.sunwayworld.emc.module.bp.mobile.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobilePaginationDTO;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月02日
 */
@RequestMapping("/secure/emc/module/bp/mobile/projects")
public interface PfMobileProjectResource extends EmcMobileProjectResource {

    @Log(value = "平板新增点位", type = LogType.INSERT)
    @RequestMapping(value = "/action/create-folder", method = RequestMethod.POST)
    EmcMobileDTO createFolder(RestJsonWrapperBean wrapper);

    @Log(value = "平板新增样品", type = LogType.INSERT)
    @RequestMapping(value = "/action/create-order", method = RequestMethod.POST)
    EmcMobileDTO createOrder(RestJsonWrapperBean wrapper);

    @Log(value = "平板删除点位", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-folder", method = RequestMethod.POST)
    EmcMobileDTO deleteFolder(RestJsonWrapperBean wrapper);

    @Log(value = "平板删除样品", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-order", method = RequestMethod.POST)
    EmcMobileDTO deleteOrder(RestJsonWrapperBean wrapper);

    @Log(value = "分页下载静态数据", type = LogType.INSERT)
    @RequestMapping(value = "/static-data/pagination/queries", method = RequestMethod.POST)
    EmcMobilePaginationDTO selectStaticDataByPagination(RestJsonWrapperBean wrapper);

    @Log(value = "分页下载项目数据", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/pagination/queries", method = RequestMethod.POST)
    EmcMobilePaginationDTO selectProjectDataByPagination(@PathVariable Long id, RestJsonWrapperBean wrapper);

    @Log(value = "样品交接-创建交接单", type = LogType.INSERT)
    @RequestMapping(value = "/action/order-delivery", method = RequestMethod.POST)
    EmcMobileDTO createDelivery(RestJsonWrapperBean wrapper);
}
