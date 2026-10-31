package com.sunwayworld.emc.module.bp.report.report.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportSendInfoBean;
import com.sunwayworld.emc.module.bp.report.report.dao.EmcReportSendInfoDao;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportSendInfoService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcReportSendInfoServiceImpl implements EmcReportSendInfoService {

    @Autowired
    private EmcReportSendInfoDao reportSendInfoDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportSendInfoDao getDao() {
        return reportSendInfoDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcReportSendInfoBean sendInfo = wrapperBean.parseUnique(EmcReportSendInfoBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        sendInfo.setId(id);
        getDao().insert(sendInfo);

        return id;
    }

    @Override
    public int updateByProjectId(RestJsonWrapperBean wrapperBean) {
        List<EmcReportSendInfoBean> projectList = wrapperBean.parse(EmcReportSendInfoBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcReportSendInfoBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcReportSendInfoBean> sendList = getDao().selectListByOneColumnValues(projectIdList, "projid", new Order[0]);
        sendList.stream().forEach(sendInfo -> {
            sendInfo.setExt$Item("username", projectList.get(0).getExt$Item("username"));
            sendInfo.setExt$Item("address", projectList.get(0).getExt$Item("address"));
            sendInfo.setExt$Item("phone", projectList.get(0).getExt$Item("phone"));
            sendInfo.setExt$Item("pic", projectList.get(0).getExt$Item("pic"));
            sendInfo.setExt$Item("orgid", LocalContextHelper.getLoginOrgId());
            sendInfo.setExt$Item("backsendno", ApplicationContextHelper.getNextSequence("T_EMC_REPORT_SEND_INFO_HQ"));
            sendInfo.setExpressType(projectList.get(0).getExpressType());
            sendInfo.setExpressNo(projectList.get(0).getExpressNo());
            sendInfo.setRemark(projectList.get(0).getRemark());
        });
        getDao().update(sendList, new String[]{"username", "address", "phone", "pic", "expresstype", "expressno", "remark", "backsendno", "orgid"});
        return 0;
    }
}
