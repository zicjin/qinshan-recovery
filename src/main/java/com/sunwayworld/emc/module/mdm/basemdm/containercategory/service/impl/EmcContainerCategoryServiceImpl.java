package com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.dao.EmcContainerCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
@Repository
@GikamBean
public class EmcContainerCategoryServiceImpl implements EmcContainerCategoryService{

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
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcContainerCategoryBean emcContainerCategoryBean = wrapper.parseUnique(EmcContainerCategoryBean.class);
        Long id =  ApplicationContextHelper.getNextIdentity();
        emcContainerCategoryBean.setId(id);
        getDao().insert(emcContainerCategoryBean);
        return id;
    }
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertOrderCategory(String ids, RestJsonWrapperBean wrapper) {
        EmcTestMethodOrderCategoryBean testMethodOrderCategory = wrapper.parseUnique(EmcTestMethodOrderCategoryBean.class);
        String testMethodIds = testMethodOrderCategory.getExt$Item("testMethodIds");

        if (StringUtils.isEmpty(testMethodIds)) {
            return;
        }

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
            newOrderCategory.setTestMethodId(NumberUtils.parseLong(testMethodId));
            newOrderCategory.setStoreCondition(testMethodOrderCategory.getStoreCondition());
            newTestMethodOrderCategoryList.add(newOrderCategory);
        });

        testMethodOrderCategoryService.getDao().insert(newTestMethodOrderCategoryList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteOrderCategory(String ids) {
        List<Long> idList = new ArrayList<Long>();
        if (!StringUtils.isEmpty(ids)) {
            String [] str = ids.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!StringUtils.isEmpty(str[i])) {
                    Long id = Long.parseLong(str[i]);
                    idList.add(id);
                }
            }
        }
        testMethodOrderCategoryService.getDao().deleteByIdList(idList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyOrderCategory(String ids, String monitortmplid,RestJsonWrapperBean wrapper) {
        List<Long> idList = new ArrayList<Long>();
        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryBeanList = wrapper.parse(EmcTestMethodOrderCategoryBean.class);
        if (!StringUtils.isEmpty(ids)) {
            String [] str = ids.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!StringUtils.isEmpty(str[i])) {
                    Long id = Long.parseLong(str[i]);
                    idList.add(id);
                }
            }
            List<EmcTestMethodOrderCategoryBean> ocList = testMethodOrderCategoryService.getDao().selectListByIds(idList);
            if (ocList.size() > 0) {
                List<EmcTestMethodOrderCategoryBean> addList = new ArrayList<EmcTestMethodOrderCategoryBean>();
                for (EmcTestMethodOrderCategoryBean oc : ocList) {
                    EmcTestMethodOrderCategoryBean newOc = new EmcTestMethodOrderCategoryBean();
                    BeanUtils.copyProperties(oc, newOc, "ID","MONITORTMPLID");
                    newOc.setId(ApplicationContextHelper.getNextIdentity());
                    newOc.setMonitorTmplId(monitortmplid);
                    newOc.setContainerGroup(testMethodOrderCategoryBeanList.get(0).getContainerGroup());
                    addList.add(newOc);
                };
                testMethodOrderCategoryService.getDao().insert(addList);
            }

        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByTMId(String testmethodids, RestJsonWrapperBean wrapper) {
        //testmethodids待添加关联的检测项目id
        List<Long> idList = new ArrayList<Long>();
        if (!StringUtils.isEmpty(testmethodids)) {
            String [] str = testmethodids.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!StringUtils.isEmpty(str[i])) {
                    Long id = Long.parseLong(str[i]);
                    idList.add(id);
                }
            }
        }
        List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(c -> JSON.parseObject(c)).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> addList = new ArrayList<EmcTestMethodOrderCategoryBean>();
        for (int i = 0; i < mapList.size(); i++) {
            String monitorTmplId = mapList.get(i).get("monitorTmplId").toString();//样品类型
            String containerCategoryId = mapList.get(i).get("containerCategoryId").toString();//容器类型
            String containerGroup = mapList.get(i).get("containerGroup").toString();//容器组
            String sampleqty = mapList.get(i).get("sampleqty").toString();//采样个数
            String fixative = Optional.ofNullable(mapList.get(i).get("fixative")).orElse("").toString();//固定剂
            String storeCondition = Optional.ofNullable(mapList.get(i).get("storeCondition")).orElse("").toString(); // 保存条件
            for (int j = 0; j < idList.size(); j++) {
                EmcTestMethodOrderCategoryBean oc = new EmcTestMethodOrderCategoryBean();
                oc.setId(ApplicationContextHelper.getNextIdentity());
                oc.setMonitorTmplId(monitorTmplId);
                oc.setContainerCategoryId(Long.parseLong(containerCategoryId));
                oc.setContainerGroup(containerGroup);
                oc.setFixative(fixative);
                oc.setSampleqty(Long.parseLong(sampleqty));
                oc.setStoreCondition(storeCondition);
                oc.setTestMethodId(idList.get(j));
                addList.add(oc);
            }
        }
        testMethodOrderCategoryService.getDao().insert(addList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delOrderCategory(String ids, String testmethodids) {
        List<Long> idList = new ArrayList<Long>();
        if (!StringUtils.isEmpty(ids)) {
            List<Long> delIdList = new ArrayList<Long>();
            String [] str = ids.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!StringUtils.isEmpty(str[i])) {
                    Long id = Long.parseLong(str[i]);
                    idList.add(id);
                }
            }

            List<EmcTestMethodOrderCategoryBean> ocList = testMethodOrderCategoryService.getDao().selectListByIds(idList);
            if (ocList.size() > 0) {
                for (EmcTestMethodOrderCategoryBean oc : ocList) {
                    String tmid = oc.getTestMethodId().toString();
                    if (!StringUtils.isEmpty(testmethodids) && testmethodids.contains(tmid)) {
                        delIdList.add(oc.getId());
                    }
                }
            }
            testMethodOrderCategoryService.getDao().deleteByIdList(delIdList);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void moveToOther(String oldIds, String moveIds, RestJsonWrapperBean wrapper) {
        //newIds 将要转移到的容器的id    oldIds转出的容器id  moveIds转出的testmethodid
        List<Long> idList = new ArrayList<Long>();
        //待删除的id
        List<Long> delIdList = new ArrayList<Long>();
        String [] str = oldIds.split(",");
        for (int i = 0; i < str.length; i++) {
            if (!StringUtils.isEmpty(str[i])) {
                Long id = Long.parseLong(str[i]);
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
        List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(c -> JSON.parseObject(c)).collect(Collectors.toList());
        String monitorTmplId = mapList.get(0).get("monitorTmplId").toString();//样品类型
        String containerCategoryId = mapList.get(0).get("containerCategoryId").toString();//容器类型
        String containerGroup = mapList.get(0).get("containerGroup").toString();//容器组
        String sampleqty = mapList.get(0).get("sampleqty").toString();//采样个数
        String fixative = Optional.ofNullable(mapList.get(0).get("fixative")).orElse("").toString();
        String ids = mapList.get(0).get("ids").toString();//ids

        List<EmcTestMethodOrderCategoryBean> addList = new ArrayList<EmcTestMethodOrderCategoryBean>();
        List<Long> newTMIdList = new ArrayList<Long>();
        String [] newstr = moveIds.split(",");
        for (int j = 0; j < newstr.length; j++) {
            String tmid = newstr[j];
            if (!StringUtils.isEmpty(newstr[j]) && !ids.contains(tmid)) {
                Long id = Long.parseLong(tmid);
                newTMIdList.add(id);
            }
        }

        for (int w = 0; w < newTMIdList.size(); w++) {
            EmcTestMethodOrderCategoryBean oc = new EmcTestMethodOrderCategoryBean();
            oc.setId(ApplicationContextHelper.getNextIdentity());
            oc.setMonitorTmplId(monitorTmplId);
            oc.setContainerCategoryId(Long.parseLong(containerCategoryId));
            oc.setContainerGroup(containerGroup);
            oc.setFixative(fixative);
            oc.setSampleqty(Long.parseLong(sampleqty));
            oc.setTestMethodId(newTMIdList.get(w));
            addList.add(oc);
        }
        testMethodOrderCategoryService.getDao().insert(addList);
        testMethodOrderCategoryService.getDao().deleteByIdList(delIdList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateOrderCategory(String type, String ids, RestJsonWrapperBean wrapper) {
        List<Long> idList = new ArrayList<Long>();
        String [] str = ids.split(",");
        for (int i = 0; i < str.length; i++) {
            if (!StringUtils.isEmpty(str[i])) {
                Long id = Long.parseLong(str[i]);
                idList.add(id);
            }
        }

        List<EmcTestMethodOrderCategoryBean> ocList = testMethodOrderCategoryService.getDao().selectListByIds(idList);
        if (ocList.size() > 0) {
            //待新增的数据
            List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(c -> JSON.parseObject(c)).collect(Collectors.toList());
            for (EmcTestMethodOrderCategoryBean oc : ocList) {
                if (!StringUtils.isEmpty(type) && "rqlx".equals(type)) {
                    String containerCategoryId = mapList.get(0).get("containerCategoryId").toString();//容器类型
                    oc.setContainerCategoryId(Long.parseLong(containerCategoryId));
                }
                if (!StringUtils.isEmpty(type) && "cygs".equals(type)) {
                    String sampleqty = mapList.get(0).get("sampleqty").toString();//采样个数
                    oc.setSampleqty(Long.parseLong(sampleqty));
                }
                if (!StringUtils.isEmpty(type) && "zcfs".equals(type)) {
                    String fixative = Optional.ofNullable(mapList.get(0).get("fixative")).orElse("").toString();//固定剂
                    oc.setFixative(fixative);
                }
                if (!StringUtils.isEmpty(type) && "storeCondition".equals(type)) {
                    String storeCondition = Optional.ofNullable(mapList.get(0).get("storeCondition")).orElse("").toString();//贮存方式
                    oc.setStoreCondition(storeCondition);
                }
            }
            testMethodOrderCategoryService.updateIfChanged(ocList);
        }
    }
}
