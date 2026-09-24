package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeCostService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author licm@sunwayworld.com 2021年09月28日
 */
@Repository
@GikamBean
public class PfMonitorSchemeServiceImpl extends EmcMonitorSchemeServiceImpl {

	@Autowired
	private EmcMonitorSchemeCostService schemeCostService;

    @Override
	@Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void edit(String tmplId, String schemeName, RestJsonWrapperBean wrapper) {
		List<EmcMonitorSchemeBean> finalSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);
		List<String> finalSchemeUniqueIdList = finalSchemeList.stream().map(scheme -> scheme.getTestMethodId() + "$" + scheme.getItemGroupId()).collect(Collectors.toList());

		// 查询原有的方案列表
		List<EmcMonitorSchemeBean> originalSchemeList = selectListByFilter(SearchFilter.instance().match("schemeName", schemeName).filter(MatchPattern.EQ)
				.match("monitorTmplId", tmplId).filter(MatchPattern.EQ));
		List<String> originalSchemeUniqueIdList = originalSchemeList.stream().map(scheme -> scheme.getTestMethodId() + "$" + scheme.getItemGroupId()).collect(Collectors.toList());

		// 取编辑后的方案相对于原有方案的差集，即为需要新增的
		List<EmcMonitorSchemeBean> insertSchemeList = finalSchemeList.stream().filter(scheme -> !originalSchemeUniqueIdList.contains(scheme.getTestMethodId() + "$" + scheme.getItemGroupId())).collect(Collectors.toList());
		// 取原有方案相对于编辑后的方案的差集，即为需要删除的
		List<EmcMonitorSchemeBean> deleteSchemeList = originalSchemeList.stream().filter(scheme -> !finalSchemeUniqueIdList.contains(scheme.getTestMethodId() + "$" + scheme.getItemGroupId())).collect(Collectors.toList());

		if (!insertSchemeList.isEmpty()) {
			insertSchemeList.forEach(scheme -> {
				scheme.setId(ApplicationContextHelper.getNextIdentity());
				if (ObjectUtils.isEmpty(scheme.getExt$Item("defaultflag"))) {
					scheme.setExt$Item("defaultflag", "1");
				}
				scheme.setExt$Item("enableflag", "1");
			});
			getDao().insert(insertSchemeList);
		}

		if (!deleteSchemeList.isEmpty()) {
			getDao().deleteBy(deleteSchemeList, "SCHEMENAME", "MONITORTMPLID", "TESTMETHODID","ITEMGROUPID");
		}
    }

	@Override
	@Transactional
	public void delete(RestJsonWrapperBean wrapper) {
    	// 如果监测方案全部被删除了，则需要删除监测费用记录
		List<EmcMonitorSchemeBean> schemeList = wrapper.parse(EmcMonitorSchemeBean.class);
		List<Long> schemeIdList = schemeList.stream().map(EmcMonitorSchemeBean::getId).collect(Collectors.toList());
		List<EmcMonitorSchemeBean> deletedSchemeList = getDao().selectListByOneColumnValues(schemeIdList, "id", Arrays.asList("id", "schemeName", "monitorTmplId"));
		Map<String, Long> deletedCountMap = deletedSchemeList.stream().collect(Collectors.groupingBy(EmcMonitorSchemeBean::getSchemeName, Collectors.counting()));

		List<String> schemeNameList = deletedSchemeList.stream().map(EmcMonitorSchemeBean::getSchemeName).distinct().collect(Collectors.toList());
		String monitorTmplId = deletedSchemeList.get(0).getMonitorTmplId();

		List<EmcMonitorSchemeBean> originalSchemeList = selectListByFilter(SearchFilter.instance().match("schemeName", schemeNameList).filter(MatchPattern.OR).match("monitorTmplId", monitorTmplId).filter(MatchPattern.SEQ));
		Map<String, Long> originalCountMap = originalSchemeList.stream().collect(Collectors.groupingBy(EmcMonitorSchemeBean::getSchemeName, Collectors.counting()));

		List<EmcMonitorSchemeCostBean> deletedSchemeCostList = new ArrayList<>();
		deletedCountMap.forEach((schemeName, count) -> {
			if (ObjectUtils.equals(count, originalCountMap.get(schemeName))) {
				EmcMonitorSchemeCostBean schemeCost = new EmcMonitorSchemeCostBean();
				schemeCost.setMonitorTmplId(monitorTmplId);
				schemeCost.setSchemeName(schemeName);
				deletedSchemeCostList.add(schemeCost);
			}
		});

		if (!deletedSchemeCostList.isEmpty()) {
			schemeCostService.getDao().deleteBy(deletedSchemeCostList, "schemeName", "monitorTmplId");
		}

		super.delete(wrapper);
	}
}

