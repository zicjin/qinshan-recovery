package com.sunwayworld.emc.module.bp.mobile.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileFileDTO;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
@RequestMapping("/secure/emc/module/bp/mobile/projects")
public interface EmcMobileProjectResource {

    @Log(value = "查询项目列表", type = LogType.SELECT)
    @RequestMapping(value = "/queries", method = RequestMethod.POST)
    EmcMobileDTO selectProjectListByCond(RestJsonWrapperBean wrapper);

    @Log(value = "查询项目数据", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/data/queries", method = RequestMethod.POST)
    EmcMobileDTO selectProjectData(@PathVariable String id, RestJsonWrapperBean wrapper);

    @Log(value = "查询操作类型", type = LogType.SELECT)
    @RequestMapping(value = "/operation-types/queries", method = RequestMethod.POST)
    EmcMobileDTO selectOperationTypeByCond(RestJsonWrapperBean wrapper);

    @Log(value = "查询静态数据", type = LogType.SELECT)
    @RequestMapping(value = "/static-data/queries", method = RequestMethod.POST)
    EmcMobileDTO selectStaticDataByCond(RestJsonWrapperBean wrapper);

    @Log(value = "上传项目信息", type = LogType.INSERT)
    @RequestMapping(value = "/action/upload", method = RequestMethod.POST)
    EmcMobileDTO uploadProjectInfo(RestJsonWrapperBean wrapper);

    @Log(value = "上传项目附件", type = LogType.INSERT)
    @RequestMapping(value = "/files/action/upload", method = RequestMethod.POST)
    EmcMobileDTO uploadProjectFile(EmcMobileFileDTO mobileFile, MultipartFile file);

    @Log(value = "查询待接样任务信息", type = LogType.SELECT)
    @RequestMapping(value = "/todo-accept/queries", method = RequestMethod.POST)
    Page<EmcSampleBean> selectMobileScanner(RestJsonWrapperBean wrapper);

    @Log(value = "查询详细标签信息", type = LogType.SELECT)
    @RequestMapping(value = "/detail-label/queries", method = RequestMethod.POST)
    Page<EmcSampleBean> selectDetailLabel(RestJsonWrapperBean wrapper);

    @Log(value = "查询标签详细信息", type = LogType.SELECT)
    @RequestMapping(value = "/label-detail/queries", method = RequestMethod.POST)
    Page<EmcSampleBean> selectLabelDetail(RestJsonWrapperBean wrapper);

    @Log(value = "根据labelId更新扫描标志", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-sanned", method = RequestMethod.POST)
    String updateSannerByLabelId(RestJsonWrapperBean wrapper);

    @Log(value = "根据labelId更新备注", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-remark", method = RequestMethod.POST)
    void updateRemarkByLabelId(RestJsonWrapperBean wrapper);

}
