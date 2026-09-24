package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.PfTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.validator.PfAddTestCapacityValidator;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodDefaultQcService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月01日
 */
@Repository
@GikamBean
public class PfTestCapacityServiceImpl extends EmcTestCapacityServiceImpl implements PfTestCapacityService {

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcTestMethodDefaultQcService testMethodDefaultQcService;

    @Autowired
    private EmcEquiptTestMethodService emcEquiptTestMethodService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcCapacityItemService capacityItemService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcItemService itemService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(PfAddTestCapacityValidator.class)
    public Long batchInsert(RestJsonWrapperBean jsonWrapper) {
        List<EmcTestCapacityBean> testCapacityList = jsonWrapper.parse(EmcTestCapacityBean.class);
        List<Long> testMethodIdList = testCapacityList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValues(testMethodIdList,"TESTMETHODID");
        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        List<EmcTestCapacityBean> insertList = new ArrayList<>();
        List<EmcCapacityItemGroupBean> insertGroupList = new ArrayList<>();
        List<EmcCapacityItemBean> insertCapacityItemList = new ArrayList<>();
        testCapacityList.forEach(e->{
            EmcTestCapacityBean insertRow = new EmcTestCapacityBean();
            BeanUtils.copyProperties(e, insertRow, EmcConstant.CREATED_COLUMNS);
            insertRow.setId(ApplicationContextHelper.getNextIdentity());
            insertRow.setTestMethodId(e.getId());
            insertRow.setOrgId(parentOrgId);
            insertRow.setExt$Item("analysisOrgName", e.getExt$Item("orgName"));
            insertRow.setExt$Item("hours", e.getExt$Item("hours"));
            insertRow.setReportTestName(e.getExt$Item("testname"));
            insertList.add(insertRow);
        });
        Map<Long,Long> insertMap = insertList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getTestMethodId,EmcTestCapacityBean::getId));
        itemGroupList.forEach(e->{
            EmcCapacityItemGroupBean itemGroup = new EmcCapacityItemGroupBean();
            Long capacityItemGroupId = ApplicationContextHelper.getNextIdentity();
            itemGroup.setId(capacityItemGroupId);
            itemGroup.setTestCapacityId(insertMap.get(e.getTestMethodId()));
            itemGroup.setTestId(e.getTestId());
            itemGroup.setItemGroup(e.getItemGroup());
            itemGroup.setItemGroupId(e.getId());
            itemGroup.setTestMethodId(e.getTestMethodId());
            insertGroupList.add(itemGroup);
            List<EmcGroupItemBean> filterGroupItemList = groupItemList.stream().filter(gi -> ObjectUtils.equals(gi.getItemGroupId(), e.getId())).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(filterGroupItemList)) {
                List<Long> filterItemIdList = filterGroupItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
                List<EmcItemBean> filterItemList = itemList.stream().filter(i -> filterItemIdList.contains(i.getId())).collect(Collectors.toList());
                filterItemList.forEach(item -> {
                    EmcCapacityItemBean capacityItem = new EmcCapacityItemBean();
                    BeanUtils.copyProperties(item, capacityItem, EmcConstant.CREATED_COLUMNS);
                    capacityItem.setCapItemGroupId(capacityItemGroupId);
                    capacityItem.setTestCapacityId(itemGroup.getTestCapacityId());
                    capacityItem.setId(ApplicationContextHelper.getNextIdentity());
                    capacityItem.setItemId(item.getId());
                    capacityItem.setOrgId(parentOrgId);
                    insertCapacityItemList.add(capacityItem);
                });
            }
        });
        getDao().insert(insertList);
        capacityItemGroupService.getDao().insert(insertGroupList);
        capacityItemService.getDao().insert(insertCapacityItemList);
        return testCapacityList.get(0).getId();
    }

    @Override
    public Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(Long id, RestJsonWrapperBean wrapper) {
        return testMethodDefaultQcService.selectPaginationByFilter(SearchFilter.instance().match("TESTCAPACITYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public  void copyAllToOtherOrg(RestJsonWrapperBean wrapper) {
    	//选择的要复制到数据的公司
    	String orgId = wrapper.getFilterValue("orgId");
    	//用户当前登录的公司的监测能力库作为母数据
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
    	List<EmcTestCapacityBean> copyTestCapacityList= getDao().selectListByOneColumnValue(parentOrgId, "orgId");
    	List<EmcTestCapacityBean> isExistOrgTestCapacityList = getDao().selectListByOneColumnValue(orgId, "orgId");
    	if(!isExistOrgTestCapacityList.isEmpty()){
    		return;
    	}
    	List<Long> testCapacityIdList = copyTestCapacityList.stream().map(EmcTestCapacityBean ::getId).distinct().collect(Collectors.toList());
    	//当前检测能力库下的分析项组数据；需要在循环中匹配
    	List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");
    	List<EmcTestMethodDefaultQcBean> testMethodDefaultQcList = testMethodDefaultQcService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");

   	 	List<EmcTestCapacityBean> addTestCapacityList =  new ArrayList<>();
    	List<EmcCapacityItemGroupBean> addCapacityItemGroupList = new ArrayList<>();
    	List<EmcTestMethodDefaultQcBean> addTestMethodDefaultQcList = new ArrayList<>();

    	copyTestCapacityList.forEach(tc ->{
    		Long oldId = tc.getId();
    		tc.setId(ApplicationContextHelper.getNextIdentity());
    		tc.setOrgId(orgId);
    		//现场记录编号、分析科室、分析科室ID、分析记录受控编号、录入人名称、录入人ID、称重人、称重人ID、前处理人、前处理人ID、唯一性编号置空
    		tc.setExt$Item("siterecordno","");
    		tc.setExt$Item("analysisorgname","");
    		tc.setExt$Item("analysisorgid","");
    		tc.setExt$Item("conrolledno","");
    		tc.setExt$Item("inputusername","");
    		tc.setExt$Item("inputuserid","");
    		tc.setExt$Item("weighingusername","");
    		tc.setExt$Item("weighinguserid","");
    		tc.setExt$Item("pretreatuserid","");
    		tc.setExt$Item("pretreatusername","");
    		tc.setExt$Item("batchno","");
    		tc.setCreatedById(LocalContextHelper.getLoginUserId());
    		tc.setCreatedByName(LocalContextHelper.getLoginUserName());
    		tc.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
    		tc.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
    		tc.setCreatedTime(LocalDateTime.now());
    		addTestCapacityList.add(tc);
    		//根据监测能力未更新前的id找到对应的分析项组及QC，把新的id赋给他们
    		List<EmcCapacityItemGroupBean> capacityItemGroupForSingleList = capacityItemGroupList.stream().filter(c ->ObjectUtils.equals(oldId, c.getTestCapacityId())).collect(Collectors.toList());
    		List<EmcTestMethodDefaultQcBean> testMethodDefaultQcForSingleList = testMethodDefaultQcList.stream().filter(t ->ObjectUtils.equals(oldId, t.getTestCapacityId()))
    				.filter(t ->StringUtils.equals(parentOrgId,t.getOrgId())).collect(Collectors.toList());
    		if(!capacityItemGroupForSingleList.isEmpty()){
        		capacityItemGroupForSingleList.forEach(it ->{
        			it.setId(ApplicationContextHelper.getNextIdentity());
        			it.setTestCapacityId(tc.getId());
        			it.setCreatedById(LocalContextHelper.getLoginUserId());
        			it.setCreatedByName(LocalContextHelper.getLoginUserName());
        			it.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        			it.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        			it.setCreatedTime(LocalDateTime.now());
        		});
        		addCapacityItemGroupList.addAll(capacityItemGroupForSingleList);
    		}
    		if(!testMethodDefaultQcForSingleList.isEmpty()){
        		testMethodDefaultQcForSingleList.forEach(qc ->{
        			qc.setId(ApplicationContextHelper.getNextIdentity());
        			qc.setTestCapacityId(tc.getId());
        			qc.setOrgId(orgId);
        			qc.setCreatedById(LocalContextHelper.getLoginUserId());
        			qc.setCreatedByName(LocalContextHelper.getLoginUserName());
        			qc.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        			qc.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        			qc.setCreatedTime(LocalDateTime.now());
        		});
        		addTestMethodDefaultQcList.addAll(testMethodDefaultQcForSingleList);
    		}

    		//根据监测能力库的id，找到对应的分析项组表及默认qc表
    	});
    	getDao().insert(addTestCapacityList);
    	if(!addCapacityItemGroupList.isEmpty()){
        	capacityItemGroupService.getDao().insert(addCapacityItemGroupList);
    	}
    	if(!addTestMethodDefaultQcList.isEmpty()){
        	testMethodDefaultQcService.getDao().insert(addTestMethodDefaultQcList);
    	}
    }

	@Override
	public void copyQcTo(RestJsonWrapperBean wrapper) {
		List<EmcTestCapacityBean> originalCapacities = wrapper.parse(EmcTestCapacityBean.class,0);
		List<EmcTestMethodDefaultQcBean> qcList = wrapper.parse(EmcTestMethodDefaultQcBean.class,1);

		if (originalCapacities.isEmpty() || qcList.isEmpty()) {
			return;
		}
		List<EmcTestMethodDefaultQcBean> insertQcList = new ArrayList<>();
		originalCapacities.forEach(originalCapacity -> {
			qcList.forEach(qc -> {
				EmcTestMethodDefaultQcBean newBean = new EmcTestMethodDefaultQcBean();
                BeanUtils.copyProperties(qc,newBean);
				newBean.setId(ApplicationContextHelper.getNextIdentity());
				newBean.setTestCapacityId(originalCapacity.getId());
				insertQcList.add(newBean);
			});
		});
		testMethodDefaultQcService.getDao().insert(insertQcList);
	}

	@Override
	@Transactional
	public void copyInfoTo(RestJsonWrapperBean wrapper) {
		List<EmcTestCapacityBean> originalCapacities = wrapper.parse(EmcTestCapacityBean.class, 0);
		if (originalCapacities.isEmpty()) {
			return;
		}

		EmcTestCapacityBean originalCapacity = originalCapacities.get(0);
		List<EmcTestCapacityBean> targetCapacities = wrapper.parse(EmcTestCapacityBean.class, 1);
		List<Long> targetTestMethodIdList = targetCapacities.stream().map(EmcTestCapacityBean::getTestMethodId).collect(Collectors.toList());
		List<Long> targetCapacityIdList = targetCapacities.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());

		String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
		List<EmcEquiptTestMethodBean> equiptTestMethodList = emcEquiptTestMethodService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SB).match("testMethodId", originalCapacity.getTestMethodId()).filter(MatchPattern.SEQ));

		List<EmcTestMethodDefaultQcBean> defaultQcList = testMethodDefaultQcService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SB).match("testCapacityId", originalCapacity.getId()).filter(MatchPattern.SEQ).match("testMethodId", originalCapacity.getTestMethodId()).filter(MatchPattern.SEQ));

		List<EmcEquiptTestMethodBean> existedEquiptList = emcEquiptTestMethodService.selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.SB).match("testMethodId", targetTestMethodIdList).filter(MatchPattern.OR));
		List<EmcTestMethodDefaultQcBean> existedDefaultQcList = testMethodDefaultQcService.getDao().selectListByOneColumnValues(targetCapacityIdList, "testCapacityId");
		List<Long> existedEquiptIdList = existedEquiptList.stream().map(EmcEquiptTestMethodBean::getEquiptId).distinct().collect(Collectors.toList());
		List<Long> existedQcIdList = existedDefaultQcList.stream().map(EmcTestMethodDefaultQcBean::getQcCategoryId).distinct().collect(Collectors.toList());

		List<EmcPropertyDataBean> originalPropertyDataList = propertyDataService.selectListByFilter(SearchFilter.instance().match("bizId", originalCapacity.getId()).filter(MatchPattern.SEQ).match("tableName", "T_EMC_TEST_CAPACITY").filter(MatchPattern.SEQ));
		List<EmcPropertyDataBean> existedPropertyDataList = propertyDataService.selectListByFilter(SearchFilter.instance().match("bizId", targetCapacityIdList).filter(MatchPattern.OR).match("tableName", "T_EMC_TEST_CAPACITY").filter(MatchPattern.SEQ));
		List<Long> existedBizIdList = existedPropertyDataList.stream().map(EmcPropertyDataBean::getBizId).distinct().collect(Collectors.toList());

		List<EmcEquiptTestMethodBean> newEquiptTestMethodList = new ArrayList<>();
		List<EmcTestMethodDefaultQcBean> newDefaultQcList = new ArrayList<>();
		List<EmcPropertyDataBean> newPropertyDataList = new ArrayList<>();

		targetCapacities.forEach(capacity -> {
			if (!StringUtils.isEmpty(originalCapacity.getExt$Item("analysisOrgId"))) {
				capacity.setExt$Item("analysisOrgId", originalCapacity.getExt$Item("analysisOrgId"));
				capacity.setExt$Item("analysisOrgName", originalCapacity.getExt$Item("analysisOrgName"));
			}

			if (!StringUtils.isEmpty(originalCapacity.getExt$Item("CONROLLEDNO"))) {
				capacity.setExt$Item("CONROLLEDNO", originalCapacity.getExt$Item("CONROLLEDNO"));
			}

			if (!StringUtils.isEmpty(originalCapacity.getExt$Item("ANALYSISRECORDNAME"))) {
				capacity.setExt$Item("ANALYSISRECORDNAME", originalCapacity.getExt$Item("ANALYSISRECORDNAME"));
			}

			if (!equiptTestMethodList.isEmpty()) {
				equiptTestMethodList.stream().filter(equipt -> !existedEquiptIdList.contains(equipt.getEquiptId())).forEach(equipt -> {
					EmcEquiptTestMethodBean newEquipt = new EmcEquiptTestMethodBean();
					BeanUtils.copyProperties(equipt, newEquipt, EmcConstant.CREATED_COLUMNS);
					newEquipt.setId(ApplicationContextHelper.getNextIdentity());
					newEquipt.setTestMethodId(capacity.getTestMethodId());
					newEquipt.setTestId(NumberUtils.parseLong(capacity.getExt$Item("testId")));
					newEquipt.setMethodId(NumberUtils.parseLong(capacity.getExt$Item("methodId")));
					newEquipt.setTestCategoryId(NumberUtils.parseLong(capacity.getExt$Item("testCategoryId")));
					newEquiptTestMethodList.add(newEquipt);
				});
			}

			if (!defaultQcList.isEmpty()) {
				defaultQcList.stream().filter(qc -> !existedQcIdList.contains(qc.getQcCategoryId())).forEach(qc -> {
					EmcTestMethodDefaultQcBean newDefaultQc = new EmcTestMethodDefaultQcBean();
					BeanUtils.copyProperties(qc, newDefaultQc, EmcConstant.CREATED_COLUMNS);
					newDefaultQc.setId(ApplicationContextHelper.getNextIdentity());
					newDefaultQc.setTestCapacityId(capacity.getId());
					newDefaultQc.setTestMethodId(capacity.getTestMethodId());
					newDefaultQcList.add(newDefaultQc);
				});
			}

			if (!originalPropertyDataList.isEmpty() && !existedBizIdList.contains(capacity.getId())) {
				originalPropertyDataList.stream().filter(data -> StringUtils.equals(data.getExt$Item("flag1"), Constant.NO)).findFirst().ifPresent(data -> {
					EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
					BeanUtils.copyProperties(data, newPropertyData, EmcConstant.CREATED_COLUMNS);
					newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
					newPropertyData.setBizId(capacity.getId());
					newPropertyDataList.add(newPropertyData);
				});

				originalPropertyDataList.stream().filter(data -> StringUtils.equals(data.getExt$Item("flag1"), Constant.YES)).findFirst().ifPresent(data -> {
					EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
					BeanUtils.copyProperties(data, newPropertyData, EmcConstant.CREATED_COLUMNS);
					newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
					newPropertyData.setBizId(capacity.getId());
					newPropertyDataList.add(newPropertyData);
				});
			}
		});

		getDao().updateIfChanged(targetCapacities);
		emcEquiptTestMethodService.getDao().insert(newEquiptTestMethodList);
		testMethodDefaultQcService.getDao().insert(newDefaultQcList);
		propertyDataService.getDao().insert(newPropertyDataList);
	}

	@Override
	public Page<EmcCapacityItemBean> selectCapacityItemPagination(RestJsonWrapperBean wrapper) {
		return capacityItemService.selectRawPagination(wrapper);
	}

	@Override
	@Transactional
	public void syncItem(RestJsonWrapperBean wrapper) {
		List<EmcCapacityItemGroupBean> capacityItemGroupList = wrapper.parse(EmcCapacityItemGroupBean.class);
		List<Long> capacityItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());
		List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

		List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
		List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
		List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);

		List<EmcCapacityItemBean> capacityItemList = capacityItemService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "capItemGroupId", Arrays.asList("id", "capItemGroupId", "itemName", "itemId"));

		List<EmcCapacityItemBean> newCapacityItemList = new ArrayList<>();
		List<Long> deleteCapacityItemIdList = new ArrayList<>();

		String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

		capacityItemGroupList.forEach(cig -> {
			List<EmcCapacityItemBean> existedItemList = capacityItemList.stream().filter(ci -> ObjectUtils.equals(cig.getId(), ci.getCapItemGroupId())).collect(Collectors.toList());
			List<Long> existedItemIdList = existedItemList.stream().map(EmcCapacityItemBean::getItemId).collect(Collectors.toList());
			List<Long> curItemIdList = groupItemList.stream().filter(gi -> ObjectUtils.equals(cig.getItemGroupId(), gi.getItemGroupId())).map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
			List<EmcItemBean> curItemList = itemList.stream().filter(item -> curItemIdList.contains(item.getId())).collect(Collectors.toList());

			List<Long> deleteCapItemIdList = existedItemList.stream().filter(ei -> !curItemIdList.contains(ei.getItemId())).map(EmcCapacityItemBean::getId).collect(Collectors.toList());
			deleteCapacityItemIdList.addAll(deleteCapItemIdList);

			List<EmcItemBean> diffItemList = curItemList.stream().filter(item -> !existedItemIdList.contains(item.getId())).collect(Collectors.toList());
			diffItemList.forEach(item -> {
				EmcCapacityItemBean capacityItem = new EmcCapacityItemBean();
				BeanUtils.copyProperties(item, capacityItem, EmcConstant.CREATED_COLUMNS);
				capacityItem.setCapItemGroupId(cig.getId());
				capacityItem.setTestCapacityId(cig.getTestCapacityId());
				capacityItem.setId(ApplicationContextHelper.getNextIdentity());
				capacityItem.setItemId(item.getId());
				capacityItem.setOrgId(parentOrgId);
				newCapacityItemList.add(capacityItem);
			});
		});

		capacityItemService.getDao().insert(newCapacityItemList);
		capacityItemService.getDao().deleteByIdList(deleteCapacityItemIdList);
	}

	@Override
	@Transactional
	public void refetchItem(RestJsonWrapperBean wrapper) {
		List<EmcCapacityItemGroupBean> capacityItemGroupList = wrapper.parse(EmcCapacityItemGroupBean.class);
		List<Long> capacityItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getId).collect(Collectors.toList());
		List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

		List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
		List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
		List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);

		// 删除原来的分析项
		List<EmcCapacityItemBean> deleteItemList = new ArrayList<>(capacityItemGroupIdList.size());
		capacityItemGroupIdList.forEach(cig -> {
			EmcCapacityItemBean deleteItem = new EmcCapacityItemBean();
			deleteItem.setCapItemGroupId(cig);
			deleteItemList.add(deleteItem);
		});
		capacityItemService.getDao().deleteBy(deleteItemList, "capItemGroupId");

		String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
		List<EmcCapacityItemBean> newCapacityItemList = new ArrayList<>();
		capacityItemGroupList.forEach(cig -> {
			List<Long> curItemIdList = groupItemList.stream().filter(gi -> ObjectUtils.equals(cig.getItemGroupId(), gi.getItemGroupId())).map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
			List<EmcItemBean> curItemList = itemList.stream().filter(item -> curItemIdList.contains(item.getId())).collect(Collectors.toList());

			curItemList.forEach(item -> {
				EmcCapacityItemBean capacityItem = new EmcCapacityItemBean();
				BeanUtils.copyProperties(item, capacityItem, EmcConstant.CREATED_COLUMNS);
				capacityItem.setCapItemGroupId(cig.getId());
				capacityItem.setTestCapacityId(cig.getTestCapacityId());
				capacityItem.setId(ApplicationContextHelper.getNextIdentity());
				capacityItem.setItemId(item.getId());
				capacityItem.setOrgId(parentOrgId);
				newCapacityItemList.add(capacityItem);
			});
		});

		capacityItemService.getDao().insert(newCapacityItemList);
	}

	@Override
	@Transactional
	@AuditTrailEntry(AuditTrailType.INSERT)
	public Long refetchItemByTest(RestJsonWrapperBean jsonWrapper) {
		List<EmcTestCapacityBean> testCapacityList = jsonWrapper.parse(EmcTestCapacityBean.class);
		List<Long> testCapacityIdList = testCapacityList.stream().map(a -> a.getId()).collect(Collectors.toList());
		List<EmcCapacityItemGroupBean> deleteItemGroupList = new ArrayList<>();
		List<EmcCapacityItemBean> deleteItemList = new ArrayList<>();
		testCapacityIdList.forEach(tci -> {
			EmcCapacityItemGroupBean deleteItemGroup = new EmcCapacityItemGroupBean();
			deleteItemGroup.setTestCapacityId(tci);
			deleteItemGroupList.add(deleteItemGroup);
			EmcCapacityItemBean deleteItem = new EmcCapacityItemBean();
			deleteItem.setTestCapacityId(tci);
			deleteItemList.add(deleteItem);
		});
		capacityItemGroupService.getDao().deleteBy(deleteItemGroupList, "TESTCAPACITYID");
		capacityItemService.getDao().deleteBy(deleteItemList, "TESTCAPACITYID");

		List<Long> testMethodIdList = testCapacityList.stream().map(a -> a.getTestMethodId()).collect(Collectors.toList());
		List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValues(testMethodIdList, "TESTMETHODID");
		List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());
		List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID");
		List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).distinct().collect(Collectors.toList());
		List<EmcItemBean> itemList = itemService.selectListByIds(itemIdList);

		CoreUserBean user = LocalContextHelper.getLoginUser();
		String parentOrgId = user.getExt$Item("parentorgid");
		List<EmcCapacityItemGroupBean> insertGroupList = new ArrayList<>();
		List<EmcCapacityItemBean> insertCapacityItemList = new ArrayList<>();
		Map<Long,Long> insertMap = testCapacityList.stream().collect(Collectors.toMap(EmcTestCapacityBean::getTestMethodId,EmcTestCapacityBean::getId));
		itemGroupList.forEach(e->{
			EmcCapacityItemGroupBean itemGroup = new EmcCapacityItemGroupBean();
			Long capacityItemGroupId = ApplicationContextHelper.getNextIdentity();
			itemGroup.setId(capacityItemGroupId);
			itemGroup.setTestCapacityId(insertMap.get(e.getTestMethodId()));
			itemGroup.setTestId(e.getTestId());
			itemGroup.setItemGroup(e.getItemGroup());
			itemGroup.setItemGroupId(e.getId());
			itemGroup.setTestMethodId(e.getTestMethodId());
			insertGroupList.add(itemGroup);
			List<EmcGroupItemBean> filterGroupItemList = groupItemList.stream().filter(gi -> ObjectUtils.equals(gi.getItemGroupId(), e.getId())).collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(filterGroupItemList)) {
				List<Long> filterItemIdList = filterGroupItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
				List<EmcItemBean> filterItemList = itemList.stream().filter(i -> filterItemIdList.contains(i.getId())).collect(Collectors.toList());
				filterItemList.forEach(item -> {
					EmcCapacityItemBean capacityItem = new EmcCapacityItemBean();
					BeanUtils.copyProperties(item, capacityItem, EmcConstant.CREATED_COLUMNS);
					capacityItem.setCapItemGroupId(capacityItemGroupId);
					capacityItem.setTestCapacityId(itemGroup.getTestCapacityId());
					capacityItem.setId(ApplicationContextHelper.getNextIdentity());
					capacityItem.setItemId(item.getId());
					capacityItem.setOrgId(parentOrgId);
					insertCapacityItemList.add(capacityItem);
				});
			}
		});
		capacityItemGroupService.getDao().insert(insertGroupList);
		capacityItemService.getDao().insert(insertCapacityItemList);
		return testCapacityList.get(0).getId();
	}

}
