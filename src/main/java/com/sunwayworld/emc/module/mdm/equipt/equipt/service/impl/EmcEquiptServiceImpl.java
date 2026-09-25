package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.Collections;
import java.util.List;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptDCUConfService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;

@Repository
@GikamBean
public class EmcEquiptServiceImpl implements EmcEquiptService {
    @Autowired
    private EmcEquiptDao emcEquiptDao;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    private EmcEquiptTestItemService equiptTestItemService;

    @Autowired
    private EmcEquiptDCUConfService equiptDCUConfService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptDao getDao() {
        return emcEquiptDao;
    }

    @Override
    @Transactional
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcEquiptBean equipt = wrapper.parseUnique(EmcEquiptBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        equipt.setId(id);
        //赋值所属单位信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        equipt.setOrgId(parentOrgId);
        
        getDao().insert(equipt);
        return id;
    }

    @Override
    public Long insertTestMethod(Long id, RestJsonWrapperBean wrapper) {
        return equiptTestMethodService.insert(id, wrapper);
    }

    @Override
    public Page<EmcEquiptTestMethodBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper) {
        return equiptTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("EQUIPTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void deleteTestMethod(Long id, RestJsonWrapperBean wrapper) {
        equiptTestMethodService.delete(wrapper);
    }

    @Override
    public Long insertTestItem(Long id, RestJsonWrapperBean wrapper) {
        return equiptTestItemService.insert(id, wrapper);
    }

    @Override
    public Page<EmcEquiptTestItemBean> selectEquiptTestItemRawPagination(Long id, RestJsonWrapperBean wrapper) {
        return equiptTestItemService.selectPaginationByFilter(SearchFilter.instance().match("EQUIPTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void deleteTestItem(Long id, RestJsonWrapperBean wrapper) {
        equiptTestItemService.delete(wrapper);
    }

    @Override
    public EmcEquiptDCUConfBean selectDCUConfDetail(Long id) {
        return equiptDCUConfService.selectDetail(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<EmcEquiptBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {

        MapperParameter parameter = wrapper.extractMapFilter();

        if (!StringUtils.isEmpty(wrapper.getFilterValue("testMethodId"))) {
            List<String> testMethodIdList = Arrays.asList(wrapper.extractMapFilter().get("testMethodId").toString().split(","));

            if (!testMethodIdList.isEmpty()) {
                parameter.put("testMethodIdList", testMethodIdList);
            }
        }

        parameter.setOrgAuthority(); // 卡单位权限

        // 选择查询
        parameter.setChoosableQueries();

        // 已审核完毕的数据
        if (Auditable.class.isAssignableFrom(this.getDao().getType())) {
            parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
        }

        // 未停用的数据
        if (Suspendable.class.isAssignableFrom(getDao().getType())) {
            parameter.setSuspendedFlag(Constant.SUSPENDED_STATUS_NO);
        }

        // 已启用的数据
        if (Activatable.class.isAssignableFrom(getDao().getType())) {
            parameter.setActivatedFlag(Constant.ACTIVATED_STATUS_YES);
        }

        parameter.putAll(wrapper.getExtFilter());

        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return this.selectPagination(parameter, rowBounds);

    }

    @Override
    public RestValidationResultBean validateDcuConfig(Long id) {
        List<EmcEquiptTestItemBean> equiptTestItemList = equiptTestItemService.getDao().selectListByOneColumnValue(id,"EQUIPTID", Collections.singletonList("ID"));
        if (equiptTestItemList.isEmpty()) {
            return new RestValidationResultBean(false, "EMC.MODULE.BP.RESULT.RUNS.TIP.EQUIPT_HAS_NO_DCU_CONFIG");
        }

        return new RestValidationResultBean(true);
    }

    @Override
    public Page<EmcEquiptBean> selectCaliChoose(RestJsonWrapperBean wrapper) {
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return  selectPagination(() ->  getDao().selectCaliChoose(), rowBounds);
    }
    @Override
    public Page<EmcEquiptBean> selectVerifyChoose(RestJsonWrapperBean wrapper) {
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return  selectPagination(() ->  getDao().selectVerifyChoose(), rowBounds);
    }
    @Override
    public Page<EmcEquiptBean> selectPerCheckChoose(RestJsonWrapperBean wrapper) {
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return  selectPagination(() ->  getDao().selectPerCheckChoose(), rowBounds);
    }
}
