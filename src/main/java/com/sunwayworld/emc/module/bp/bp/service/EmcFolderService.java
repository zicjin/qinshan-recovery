package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
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

public interface EmcFolderService extends GenericService<EmcFolderBean, Long>, GenericAuditableService<EmcFolderBean, Long>, GenericSelectableService<EmcFolderBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectSampleInfoById(Long id, RestJsonWrapperBean wrapper);

    void updatProjectStatus(RestJsonWrapperBean jsonWrapper);

    void terminateFolder(RestJsonWrapperBean jsonWrapper);

    void undoToAccept(RestJsonWrapperBean jsonWrapper);

    EmcFolderBean insertByOrder(EmcOrderBean order);

    void copyFolders(String folderNo, RestJsonWrapperBean wrapper);

    void dataSharing(RestJsonWrapperBean wrapper);

    String generateFolderNoByProjId(Long projId, String projectNo);

    List<String> generateFolderNosByProjId(Long projId, String projectNo, int num);

    Page<EmcFolderBean> querySamplePlans(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper);
    
    void updateStatus(List<EmcOrderTaskBean> orderTaskList);
    
    public default List<EmcFolderBean> getBpmnVarsItemList(List<Long> idList) {
        return selectListByFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));
    }
}
