package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPlanDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-19
 */

@Repository
public class EmcEquiptPlanServiceImpl implements EmcEquiptPlanService {
    @Autowired
    private EmcEquiptPlanDao equiptPlanDao;

    @Autowired
    private EmcEquiptService equiptService;

    @Autowired
    CoreCodeService coreCodeService;

    @Autowired
    private EmcEquiptMaintService equiptMaintService;

    @Autowired
    private EmcEquiptPerCheckPlanService emcEquiptPerCheckPlanService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptPlanDao getDao() {
        return equiptPlanDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptPlanBean equiptPlanBean = jsonWrapper.parseUnique(EmcEquiptPlanBean.class);
        equiptPlanBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(equiptPlanBean);
        return equiptPlanBean.getId();
    }

    @Override
    public Page<EmcEquiptPlanBean> selectRawPaginationByEquiptId(Long equiptId, RestJsonWrapperBean wrapper) {

        return selectPaginationByFilter(SearchFilter.instance().match("equiptId", equiptId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public List<EmcEquiptMaintBean> selectMaintSelectableList(Long equiptId ,RestJsonWrapperBean wrapper) {
        String pageCategoryFilter = wrapper.getFilterValue("pageType");
        EmcEquiptBean equiptBean = equiptService.selectById(equiptId);
        List<EmcEquiptMaintBean> equiptMaintListAll = equiptMaintService.getDao().selectListByOneColumnValue(equiptBean.getEquiptCategoryId(), "EQUIPTCATEGORYID");
        List<EmcEquiptMaintBean> equiptMaintList = new ArrayList<>();
        for (EmcEquiptMaintBean emcEquiptMaintBean : equiptMaintListAll) {
            if ("equipt".equals(pageCategoryFilter)) {
                if (emcEquiptMaintBean.getMaintEvent().startsWith("equipt")) {
                    equiptMaintList.add(emcEquiptMaintBean);
                }
            } else if ("during".equals(pageCategoryFilter)){
                if (emcEquiptMaintBean.getMaintEvent().startsWith("during")) {
                    equiptMaintList.add(emcEquiptMaintBean);
                }
            }
        }
        if (!equiptMaintList.isEmpty()){
            List<CoreCodeBean> codeBeanList = coreCodeService.selectListByFilter(SearchFilter.instance().match("codeCategoryId","maintainEvent").filter(MatchPattern.EQ), Order.asc("id"));
            for (int i = 0; i < equiptMaintList.size(); i++){
                for (CoreCodeBean c : codeBeanList) {
                    if (equiptMaintList.get(i).getMaintEvent().equals(c.getCode())){
                        equiptMaintList.get(i).setMaintEvent(c.getCodeName());
                    }
                }
            }
        }

        if ("during".equals(pageCategoryFilter)) {
            List<EmcEquiptPerCheckPlanBean> list = emcEquiptPerCheckPlanService.getDao().selectListByOneColumnValue(equiptId, "equiptId");
            if (list.isEmpty()) {
                return equiptMaintList;
            } else {
                return null;
            }
        }

        List<EmcEquiptPlanBean> equiptPlanList = getDao().selectListByOneColumnValue(equiptId, "equiptId");
        if (equiptPlanList.isEmpty()) {
            return equiptMaintList;
        }
        List<EmcEquiptMaintBean> equiptMaintList2 = new ArrayList<>();
        for (EmcEquiptMaintBean equiptMaintBean : equiptMaintList) {
            for (int i = 0; i < equiptPlanList.size(); i++) {
                if (equiptPlanList.get(i).getMaintId().equals(equiptMaintBean.getId())) {
                    break;
                }
                if (i == equiptPlanList.size() - 1) {
                    equiptMaintList2.add(equiptMaintBean);
                }
            }
        }
        return equiptMaintList2;
    }



    @Override
    public Page<EmcEquiptPlanBean> selectEquiptMaintPlanOverdue(RestJsonWrapperBean wrapper) {

        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectEquiptMaintPlanOverdue(parameter), rowBounds);
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updatePlan(EmcEquiptPlanBean emcEquiptPlanBean) {
        getDao().updatePlan(emcEquiptPlanBean);
    }

}
