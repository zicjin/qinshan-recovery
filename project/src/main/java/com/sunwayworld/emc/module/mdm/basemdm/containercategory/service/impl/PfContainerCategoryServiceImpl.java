package com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.dao.EmcContainerCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.PfContainerCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@GikamBean
public class PfContainerCategoryServiceImpl extends EmcContainerCategoryServiceImpl implements PfContainerCategoryService {

    @Autowired
    private EmcContainerCategoryDao emcContainerCategoryDao;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;


    @SuppressWarnings("unchecked")
    @Override
    public EmcContainerCategoryDao  getDao() {
        return emcContainerCategoryDao;
    }

    @Override
    public void insertOrderCategory(String ids, RestJsonWrapperBean wrapper) {
        EmcTestMethodOrderCategoryBean testMethodOrderCategory = wrapper.parseUnique(EmcTestMethodOrderCategoryBean.class);
        String testMethodIds = testMethodOrderCategory.getExt$Item("testMethodIds");

        if (StringUtils.isEmpty(testMethodIds)) {
            return;
        }
        //赋值所属单位信息     
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        String[] testMethodIdArray = StringUtils.split(testMethodIds, ",");
        List<EmcTestMethodOrderCategoryBean> newTestMethodOrderCategoryList = new ArrayList<>();
        Arrays.stream(testMethodIdArray).forEach(testMethodId -> {
            EmcTestMethodOrderCategoryBean newOrderCategory = new EmcTestMethodOrderCategoryBean();
            newOrderCategory.setId(ApplicationContextHelper.getNextIdentity());
            newOrderCategory.setMonitorTmplId(testMethodOrderCategory.getMonitorTmplId());
            newOrderCategory.setContainerCategoryId(testMethodOrderCategory.getContainerCategoryId());
            newOrderCategory.setFixative(testMethodOrderCategory.getFixative());
            newOrderCategory.setContainerGroup(testMethodOrderCategory.getContainerGroup());
            newOrderCategory.setSampleqty(1L);
            newOrderCategory.setSampleWeight(testMethodOrderCategory.getSampleWeight());
            newOrderCategory.setTestMethodId(NumberUtils.parseLong(testMethodId));
            newOrderCategory.setStoreCondition(testMethodOrderCategory.getStoreCondition());
            newOrderCategory.setExt$Item("orgid", parentOrgId);
            newTestMethodOrderCategoryList.add(newOrderCategory);
        });

        testMethodOrderCategoryService.getDao().insert(newTestMethodOrderCategoryList);
    }

    @Override
    public void insertByTMId(String testmethodids, RestJsonWrapperBean wrapper) {
        //testmethodids待添加关联的检测项目id
        List<Long> idList = new ArrayList<Long>();
        if (!StringUtils.isEmpty(testmethodids)) {
            String [] str = testmethodids.split(",");
            for (String s : str) {
                if (!StringUtils.isEmpty(s)) {
                    Long id = Long.parseLong(s);
                    idList.add(id);
                }
            }
        }
        List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(JSON::parseObject).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> addList = new ArrayList<EmcTestMethodOrderCategoryBean>();
        for (Map<String, Object> stringObjectMap : mapList) {
            String monitorTmplId = stringObjectMap.get("monitorTmplId").toString();//样品类型
            String containerCategoryId = stringObjectMap.get("containerCategoryId").toString();//容器类型
            String containerGroup = stringObjectMap.get("containerGroup").toString();//容器组
            String sampleqty = stringObjectMap.get("sampleqty").toString();//采样个数
            String sampleWeight = Optional.ofNullable(stringObjectMap.get("sampleWeight")).orElse("").toString();//采样重量
            String fixative = Optional.ofNullable(stringObjectMap.get("fixative")).orElse("").toString();//固定剂
            String storeCondition = Optional.ofNullable(stringObjectMap.get("storeCondition")).orElse("").toString(); // 保存条件
            //赋值所属单位信息
            CoreUserBean user = new CoreUserBean();
            user = LocalContextHelper.getLoginUser();
            String parentOrgId = user.getExt$Item("parentorgid");
            for (Long aLong : idList) {
                EmcTestMethodOrderCategoryBean oc = new EmcTestMethodOrderCategoryBean();
                oc.setId(ApplicationContextHelper.getNextIdentity());
                oc.setMonitorTmplId(monitorTmplId);
                oc.setContainerCategoryId(Long.parseLong(containerCategoryId));
                oc.setContainerGroup(containerGroup);
                oc.setFixative(fixative);
                oc.setSampleqty(Long.parseLong(sampleqty));
                oc.setSampleWeight(sampleWeight);
                oc.setStoreCondition(storeCondition);
                oc.setTestMethodId(aLong);
                oc.setExt$Item("ORGID", parentOrgId);
                addList.add(oc);
            }
        }
        testMethodOrderCategoryService.getDao().insert(addList);
    }

    @Override
    public void moveToOther(String oldIds, String moveIds, RestJsonWrapperBean wrapper) {
        //newIds 将要转移到的容器的id    oldIds转出的容器id  moveIds转出的testmethodid
        List<Long> idList = new ArrayList<Long>();
        //待删除的id
        List<Long> delIdList = new ArrayList<Long>();
        String [] str = oldIds.split(",");
        for (String s : str) {
            if (!StringUtils.isEmpty(s)) {
                Long id = Long.parseLong(s);
                idList.add(id);
            }
        }

        List<EmcTestMethodOrderCategoryBean> ocList = testMethodOrderCategoryService.getDao().selectListByIds(idList);
        if (ocList.size() > 0) {
            for (EmcTestMethodOrderCategoryBean oc : ocList) {
                String tmid = oc.getTestMethodId().toString();
                if (!StringUtils.isEmpty(moveIds) && moveIds.contains(tmid)) {
                    delIdList.add(oc.getId());
                }
            }
        }

        //待新增的数据
        List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(JSON::parseObject).collect(Collectors.toList());
        String monitorTmplId = mapList.get(0).get("monitorTmplId").toString();//样品类型
        String containerCategoryId = mapList.get(0).get("containerCategoryId").toString();//容器类型
        String containerGroup = mapList.get(0).get("containerGroup").toString();//容器组
        String sampleqty = mapList.get(0).get("sampleqty").toString();//采样个数
        String sampleWeight = mapList.get(0).get("sampleWeight").toString();//采样重量
        String fixative = Optional.ofNullable(mapList.get(0).get("fixative")).orElse("").toString();
        String ids = mapList.get(0).get("ids").toString();//ids

        List<EmcTestMethodOrderCategoryBean> addList = new ArrayList<EmcTestMethodOrderCategoryBean>();
        List<Long> newTMIdList = new ArrayList<Long>();
        String [] newstr = moveIds.split(",");
        for (String tmid : newstr) {
            if (!StringUtils.isEmpty(tmid) && !ids.contains(tmid)) {
                Long id = Long.parseLong(tmid);
                newTMIdList.add(id);
            }
        }

        for (Long aLong : newTMIdList) {
            EmcTestMethodOrderCategoryBean oc = new EmcTestMethodOrderCategoryBean();
            oc.setId(ApplicationContextHelper.getNextIdentity());
            oc.setMonitorTmplId(monitorTmplId);
            oc.setContainerCategoryId(Long.parseLong(containerCategoryId));
            oc.setContainerGroup(containerGroup);
            oc.setFixative(fixative);
            oc.setSampleqty(Long.parseLong(sampleqty));
            oc.setSampleWeight(sampleWeight);
            oc.setTestMethodId(aLong);
            addList.add(oc);
        }
        testMethodOrderCategoryService.getDao().insert(addList);
        testMethodOrderCategoryService.getDao().deleteByIdList(delIdList);
    }

    @Override
    public void updateOrderCategory(String type, RestJsonWrapperBean wrapper) {
        List<Long> idList = new ArrayList<Long>();
/*
        String [] str = ids.split(",");
        for (String s : str) {
            if (!StringUtils.isEmpty(s)) {
                Long id = Long.parseLong(s);
                idList.add(id);
            }
        }
*/
        List<EmcTestMethodOrderCategoryBean> tmocList = wrapper.parse(EmcTestMethodOrderCategoryBean.class,0);
        List<EmcTestMethodOrderCategoryBean> tmoc2List = wrapper.parse(EmcTestMethodOrderCategoryBean.class,1);
        tmocList.forEach(oc ->{
            String [] str = oc.getExt$Item("ids").split(",");
            for (String s : str) {
                if (!StringUtils.isEmpty(s)) {
                    Long id = Long.parseLong(s);
                    if(!idList.contains(id)){
                        idList.add(id);
                    }
                }
            }
        });
        List<EmcTestMethodOrderCategoryBean> ocList = testMethodOrderCategoryService.getDao().selectListByIds(idList);

        if (ocList.size() > 0) {
            //待新增的数据

            for (EmcTestMethodOrderCategoryBean oc : ocList) {
                if (!StringUtils.isEmpty(type) && "containerGroup".equals(type)) {
                    //容器组
                    oc.setContainerGroup(tmoc2List.get(0).getContainerGroup());
                }
                if (!StringUtils.isEmpty(type) && "rqlx".equals(type)) {
                    //容器类型
                    oc.setContainerCategoryId(tmoc2List.get(0).getContainerCategoryId());
                }
                if (!StringUtils.isEmpty(type) && "cygs".equals(type)) {
                    //采样个数
                    oc.setSampleqty(tmoc2List.get(0).getSampleqty());
                }
                if (!StringUtils.isEmpty(type) && "sampleWeight".equals(type)) {
                    //采样重量
                    oc.setSampleWeight(tmoc2List.get(0).getSampleWeight());
                }
                if (!StringUtils.isEmpty(type) && "zcfs".equals(type)) {
                    //固定剂
                    String fixative = Optional.ofNullable(tmoc2List.get(0).getFixative()).orElse("");
                    oc.setFixative(fixative);
                }
                if (!StringUtils.isEmpty(type) && "storeCondition".equals(type)) {
                    //贮存方式
                    String storeCondition = Optional.ofNullable(tmoc2List.get(0).getStoreCondition()).orElse("");
                    oc.setStoreCondition(storeCondition);
                }
            }
            testMethodOrderCategoryService.updateIfChanged(ocList);
        }
    }
}
