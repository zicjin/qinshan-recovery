package com.sunwayworld.emc.module.bp.edit.projectchange.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcFolderChangeService extends GenericService<EmcFolderChangeBean, Long>, GenericAuditableService<EmcFolderChangeBean, Long>, GenericSelectableService<EmcFolderChangeBean, Long> {

    Page<EmcSampleBean> selectSampleInfoById(Long id, RestJsonWrapperBean wrapper);

    void undoToAccept(RestJsonWrapperBean jsonWrapper);

    EmcFolderChangeBean insertByOrder(EmcOrderChangeBean order);

    void copyFolders(String folderNo, RestJsonWrapperBean wrapper);

    void dataSharing(RestJsonWrapperBean wrapper);

    String generateFolderNoByProjId(Long projId, String projectNo);

    List<String> generateFolderNosByProjId(Long projId, String projectNo, int num);

    Page<EmcFolderChangeBean> querySamplePlans(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper);

    void updateStatus(List<EmcOrderTaskBean> orderTaskList);

    public default List<EmcFolderChangeBean> getBpmnVarsItemList(List<Long> idList) {
        return selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));
    }
}
