package com.sunwayworld.emc.module.bp.info.infoconfig.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigDTO;
import com.sunwayworld.emc.module.bp.info.infoconfig.dao.EmcInfoConfigDao;
import com.sunwayworld.emc.module.bp.info.infoconfig.service.EmcInfoConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
public class EmcInfoConfigServiceImpl implements EmcInfoConfigService {
    @Autowired
    private EmcInfoConfigDao infoConfigDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoConfigDao getDao() {
        return infoConfigDao;
    }

    final static String SMALL_CATEGORY = "SCY";
    final static String DATA_CATEGORY = "DCY";
    final static String ANNIX_CATEGORY = "ACY";
    final static String DATA_DETAIL = "数据明细";
    final static String ANNIX_EXPROT = "导入数据附件";

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        Map<String, String> param = ServletUtils.getCurrentRequestParamMap();
        String configType = param.get("configType");
        Long parentId = Objects.isNull(param.get("parentId")) ? null : NumberUtils.parseLong(param.get("parentId"));

        EmcInfoConfigBean emcInfoConfig = getDefaultInsertReport(null, configType, getOrderNo(parentId), parentId, jsonWrapper);
        getDao().insert(emcInfoConfig);
        if (StringUtils.equals(SMALL_CATEGORY, configType)) {
            List<EmcInfoConfigBean> emcInfoConfigList = selectListByFilter(SearchFilter.instance().match("PARENTID", emcInfoConfig.getId()).filter(MatchPattern.EQ), jsonWrapper);
            if (emcInfoConfigList.isEmpty()) {
                List<EmcInfoConfigBean> emcReportConfigList = new ArrayList<>();
                emcReportConfigList.add(getDefaultInsertReport(DATA_DETAIL, DATA_CATEGORY, 1L, emcInfoConfig.getId(), jsonWrapper));
                emcReportConfigList.add(getDefaultInsertReport(ANNIX_EXPROT, ANNIX_CATEGORY, 2L, emcInfoConfig.getId(), jsonWrapper));
                getDao().insert(emcReportConfigList);
            }
        }

        return emcInfoConfig.getId();
    }

    /*@Override
    @Transactional
    public void postUpdate(List<String> updatedColumnNameList, List<Long> idList) {
        String updateColumn = updatedColumnNameList.get(0);
        Long updateId = idList.get(0);
        EmcInfoConfigBean emcInfoDataConfig = selectFirstByFilter(
                SearchFilter.instance().match("PARENTID", updateId).filter(MatchPattern.EQ).match("CONFIGTYPE", DATA_CATEGORY).filter(MatchPattern.EQ));
        if (StringUtils.equals("infotmplid", updateColumn) && !ObjectUtils.isEmpty(emcInfoDataConfig)) {
            EmcInfoConfigBean emcInfoConfig = selectById(updateId);
            emcInfoDataConfig.setInfoTmplId(emcInfoConfig.getInfoTmplId());
            getDao().update(emcInfoDataConfig, "INFOTMPLID");
        }
    }*/

    @Override
    public List<EmcInfoConfigDTO> selectTree(MapperParameter parameter) {
        List<EmcInfoConfigBean> methodCategoryList = getDao().selectTreeNodeList(parameter);

        TreeDescriptor<EmcInfoConfigBean> descriptor = new TreeDescriptor<>("id", "parentId", "configName", "orderNo");
        return TreeHelper.parseTreeNode(methodCategoryList, descriptor, EmcInfoConfigDTO.class);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcInfoConfigBean> emcInfoConfigList = wrapper.parse(this.getDao().getType());
        getDao().deleteBy(getDeleteList(emcInfoConfigList));
    }

    private List<EmcInfoConfigBean> getDeleteList(List<EmcInfoConfigBean> emcInfoConfigList) {
        if (emcInfoConfigList.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询子配置表
        SearchFilter filter = SearchFilter.instance().match("PARENTID", emcInfoConfigList.stream().map(c -> c.getId()).collect(Collectors.toList())).filter(MatchPattern.OR);
        List<EmcInfoConfigBean> childConfigList = selectListByFilter(filter);

        List<EmcInfoConfigBean> deleteConfigList = new ArrayList<>(emcInfoConfigList);
        deleteConfigList.addAll(getDeleteList(childConfigList));

        return deleteConfigList;
    }

    // 私有方法
    private Long getOrderNo(Long parentId) {
        Long orderNo = EmcConstant.ONE;
        EmcInfoConfigBean maxEmcInfoConfig = selectFirstByFilter(SearchFilter.instance().match("PARENTID", parentId).filter(MatchPattern.EQ), Order.desc("orderNo"));
        if (Objects.nonNull(maxEmcInfoConfig)) {
            Long infoConfigOrderNo = maxEmcInfoConfig.getOrderNo();
            orderNo = ++infoConfigOrderNo;
        }
        return orderNo;
    }

    // 私有方法
    private EmcInfoConfigBean getDefaultInsertReport(String configName, String configType, Long orderNo, Long parentId, RestJsonWrapperBean wrapper) {
        EmcInfoConfigBean emcInfoConfig = wrapper.parseUnique(EmcInfoConfigBean.class);
        emcInfoConfig.setId(ApplicationContextHelper.getNextIdentity());
        emcInfoConfig.setParentId(parentId);
        emcInfoConfig.setConfigType(configType);
        emcInfoConfig.setOrderNo(orderNo);
        if (!StringUtils.equals(SMALL_CATEGORY, configType)) {
            emcInfoConfig.setConfigName(configName);
            if (!StringUtils.equals(DATA_CATEGORY, configType)) {
                emcInfoConfig.setInfoTmplId(null);
            }
        }
        return emcInfoConfig;
    }

}
