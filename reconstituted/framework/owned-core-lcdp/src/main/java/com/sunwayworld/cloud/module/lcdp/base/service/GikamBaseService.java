/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.base.service;

import com.sunwayworld.cloud.module.lcdp.base.annotation.Mapping;
import com.sunwayworld.cloud.module.lcdp.base.annotation.MappingType;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpWrapperParseUtils;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

public interface GikamBaseService {
    default public Object getService() {
        return null;
    }

    @Mapping(value="\u6838\u5fc3\u5206\u9875\u67e5\u8be2\u6570\u636e", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectPagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectPagination", wrapper);
        return this.toMapPage(page);
    }

    @Mapping(value="\u6838\u5fc3\u7f16\u5236\u9875\u9762\u67e5\u8be2\u6570\u636e", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectRawPagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectRawPagination", wrapper);
        return this.toMapPage(page);
    }

    @Mapping(value="\u6838\u5fc3\u67e5\u8be2\u9875\u9762\u67e5\u8be2\u6570\u636e", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectSearchablePagination", wrapper);
        return this.toMapPage(page);
    }

    @Mapping(value="\u83b7\u53d6\u72b6\u6001\u4fe1\u606f", type=MappingType.SELECT)
    default public CoreServiceStatusDTO selectStatus(RestJsonWrapperBean wrapper) {
        String id = wrapper.getParamValue("id");
        String currentStatusCode = ServletUtils.getCurrentRequestParamMap().get("currentStatusCode");
        return (CoreServiceStatusDTO)SpringUtils.invoke(this.getService(), "selectStatus", id, currentStatusCode);
    }

    @Mapping(value="\u6838\u5fc3\u5220\u9664\u6570\u636e", type=MappingType.DELETE)
    @Transactional
    @Audit(value="AUDIT.DELETE")
    default public void delete(RestJsonWrapperBean wrapper) {
        SpringUtils.invoke(this.getService(), "delete", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u65b0\u589e\u6570\u636e", type=MappingType.INSERT)
    @Transactional
    @Audit(value="AUDIT.INSERT")
    default public Object insert(RestJsonWrapperBean wrapper) {
        return SpringUtils.invoke(this.getService(), "insert", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u81ea\u52a8\u4fdd\u5b58\u6570\u636e", type=MappingType.INSERT)
    @Transactional
    @Audit(value="AUDIT.INSTANT_SAVE")
    default public void instantSave(RestJsonWrapperBean wrapper) {
        SpringUtils.invoke(this.getService(), "instantSave", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5206\u9875\u67e5\u8be2\u6570\u636e\uff08\u5de5\u4f5c\u6d41\uff09", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectAuditablePagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectAuditablePagination", wrapper);
        return this.toMapPage(page);
    }

    @Mapping(value="\u6838\u5fc3\u64a4\u56de\u9875\u67e5\u8be2", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectWithdrawablePagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectWithdrawablePagination", wrapper);
        return this.toMapPage(page);
    }

    @Mapping(value="\u56de\u9000\u9875\u67e5\u8be2", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectUndoablePagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectUndoablePagination", wrapper);
        return this.toMapPage(page);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u542f\u52a8", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_START")
    default public List<CoreBpmnInstanceStatusDTO<String>> startProcess(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "startProcess", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u542f\u52a8\uff08\u8fdb\u5ea6\u76d1\u63a7)", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_START")
    default public String startProcessWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "startProcessWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u64a4\u56de", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_WITHDRAW")
    default public List<CoreBpmnInstanceStatusDTO<String>> withdrawProcess(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "withdrawProcess", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u64a4\u56de\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_WITHDRAW")
    default public String withdrawProcessWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "withdrawProcessWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_COMPLETE")
    default public List<CoreBpmnInstanceStatusDTO<String>> completeTask(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "completeTask", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_COMPLETE")
    default public String completeTaskWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "completeTaskWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u8f6c\u529e", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_TRANSFER")
    default public void transferTask(RestJsonWrapperBean wrapper) {
        SpringUtils.invoke(this.getService(), "transferTask", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u8f6c\u529e\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_TRANSFER")
    default public String transferTaskWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "transferTaskWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u7ed3\u675f", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_END")
    default public List<CoreBpmnInstanceStatusDTO<String>> endTask(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "endTask", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u7ed3\u675f\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_END")
    default public String endTaskWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "endTaskWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_REJECT")
    default public List<CoreBpmnInstanceStatusDTO<String>> rejectTask(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "rejectTask", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u5ba1\u6838\u62d2\u7edd\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_REJECT")
    default public String rejectTaskWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "rejectTaskWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_ODD_REJECT")
    default public List<CoreBpmnInstanceStatusDTO<String>> oddRejectTask(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "oddRejectTask", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u5f02\u5e38\u62d2\u7edd\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_ODD_REJECT")
    default public String oddRejectTaskWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "oddRejectTaskWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u56de\u9000", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_UNDO")
    default public List<CoreBpmnInstanceStatusDTO<String>> undo(RestJsonWrapperBean wrapper) {
        return (List)SpringUtils.invoke(this.getService(), "undo", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u5de5\u4f5c\u6d41\u56de\u9000\uff08\u8fdb\u5ea6\u76d1\u63a7\uff09", type=MappingType.UPDATE)
    @Transactional
    @Audit(value="AUDIT.PROCESS_UNDO")
    default public String undoWithProgress(RestJsonWrapperBean wrapper) {
        return (String)SpringUtils.invoke(this.getService(), "undoWithProgress", wrapper);
    }

    @Mapping(value="\u6838\u5fc3\u83b7\u53d6\u5de5\u4f5c\u6d41\u4efb\u52a1\u8282\u70b9\u7684\u72b6\u6001", type=MappingType.SELECT)
    default public Map<String, Object> selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
        CoreBpmnTaskStatusDTO bpmnTaskStatusDTO = (CoreBpmnTaskStatusDTO)SpringUtils.invoke(this.getService(), "selectBpmnTaskStatus", wrapper);
        return this.toMap(bpmnTaskStatusDTO);
    }

    @Mapping(value="\u6839\u636eid\u548c\u5b57\u6bb5\u540d\u67e5\u8be2\u5b57\u6bb5\u503c", type=MappingType.SELECT)
    default public String selectColumnById(RestJsonWrapperBean wrapper) {
        String id = wrapper.getParamValue("id");
        String column = wrapper.getParamValue("column");
        return (String)SpringUtils.invoke(this.getService(), "selectColumnById", NumberUtils.isNumber(id) ? Long.valueOf(Long.parseLong(id)) : id, column);
    }

    @Mapping(value="\u6838\u5fc3\u9009\u62e9\u9875\u67e5\u8be2", type=MappingType.SELECT)
    default public Page<Map<String, Object>> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        Page page = (Page)SpringUtils.invoke(this.getService(), "selectChoosablePagination", wrapper);
        return this.toMapPage(page);
    }

    default public Map<String, Object> toMap(Object bean) {
        List childrenList;
        Method getChildrenMethod;
        if (bean == null) {
            return new HashMap<String, Object>();
        }
        if (Map.class.isAssignableFrom(bean.getClass())) {
            return (Map)bean;
        }
        Map<String, Object> map = BeanUtils.toMap(bean);
        Method getExt$Method = ReflectionUtils.getUniqueMethodByName(bean.getClass(), "getExt$");
        if (getExt$Method != null) {
            Map ext$Map = (Map)ReflectionUtils.invokeMethod(getExt$Method, bean);
            ext$Map.forEach((k, v) -> map.put((String)k, v));
        }
        if ((getChildrenMethod = ReflectionUtils.getUniqueMethodByName(bean.getClass(), "getChildren")) != null && (childrenList = (List)ReflectionUtils.invokeMethod(getChildrenMethod, bean)) != null && childrenList.size() > 0) {
            List<Map<String, Object>> childrenMapList = this.toMapList(childrenList);
            map.put("children", childrenMapList);
        }
        this.dealMapData(map);
        return LcdpWrapperParseUtils.lowerCaseKey(map);
    }

    default public List<Map<String, Object>> toMapList(List<?> beanList) {
        ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        if (beanList == null) {
            return mapList;
        }
        for (Object bean : beanList) {
            mapList.add(this.toMap(bean));
        }
        return mapList;
    }

    default public Page<Map<String, Object>> toMapPage(Page<?> beanPage) {
        if (beanPage == null) {
            return new Pagination<Map<String, Object>>();
        }
        ArrayList<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        List<?> dataList = beanPage.getRows();
        if (!dataList.isEmpty()) {
            for (Object bean : dataList) {
                mapList.add(this.toMap(bean));
            }
        }
        return new Pagination<Map<String, Object>>(beanPage.getTotal(), beanPage.getPageSize(), beanPage.getPageNumber(), mapList);
    }

    default public void dealMapData(Map<String, Object> map) {
        if (map == null) {
            return;
        }
        map.forEach((key, value) -> {
            if (LocalDate.class.isInstance(value)) {
                try {
                    value = DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(value.toString()));
                    map.put((String)key, value);
                }
                catch (Exception ex) {
                    throw new CheckedException(ex);
                }
            } else if (LocalDateTime.class.isInstance(value)) {
                try {
                    value = DateTimeUtils.formatLocalDateTime(DateTimeUtils.parseLocalDateTime(value.toString()));
                    map.put((String)key, value);
                }
                catch (Exception ex) {
                    throw new CheckedException(ex);
                }
            } else if (Timestamp.class.isInstance(value)) {
                String dateValue = value.toString();
                if (StringUtils.contains(dateValue, ".")) {
                    dateValue = dateValue.split("\\.")[0];
                }
                map.put((String)key, dateValue);
            }
        });
    }
}
