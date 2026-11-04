/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.resource.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryConditionBean;
import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryFieldBean;
import com.sunwayworld.cloud.module.item.customquery.resource.CoreCustomQueryResource;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u81ea\u5b9a\u4e49\u67e5\u8be2")
@RestController
@GikamBean
public class CoreCustomQueryResourceImpl
implements CoreCustomQueryResource,
AbstractGenericResource<CoreCustomQueryService, CoreCustomQueryBean, Long> {
    @Autowired
    private CoreCustomQueryService coreCustomQueryService;

    @Override
    public CoreCustomQueryService getService() {
        return this.coreCustomQueryService;
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u67e5\u8be2", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u67e5\u8be2\u5b57\u6bb5", type=LogType.INSERT)
    public void insertQueryFields(Long id, Long customDsId) {
        this.getService().insertQueryFields(id, customDsId);
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u67e5\u8be2\u6761\u4ef6", type=LogType.INSERT)
    public void insertQueryCondition(Long fieldId) {
        this.getService().insertQueryCondition(fieldId);
    }

    @Override
    @Log(value="\u5220\u9664\u81ea\u5b9a\u4e49\u67e5\u8be2\u6761\u4ef6", type=LogType.DELETE)
    public void deleteQueryCondition(Long fieldId) {
        this.getService().deleteQueryCondition(fieldId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5b57\u6bb5\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    public void swapField(Long firstId, Long secondId) {
        this.getService().swapField(firstId, secondId);
    }

    @Override
    @Log(value="\u6309\u94ae\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    public void swapButton(Long firstId, Long secondId) {
        this.getService().swapButton(firstId, secondId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6761\u4ef6\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    public void swapCondition(Long firstId, Long secondId) {
        this.getService().swapCondition(firstId, secondId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5b57\u6bb5\u660e\u7ec6", type=LogType.SELECT)
    public Page<CoreCustomQueryFieldBean> selectFieldPagation(RestJsonWrapperBean wrapper) {
        return this.getService().selectFieldsPagation(wrapper);
    }

    @Override
    @Log(value="\u6309\u94ae\u660e\u7ec6", type=LogType.SELECT)
    public Page<CoreCustomQueryButtonBean> selectButtonPagation(RestJsonWrapperBean wrapper) {
        return this.getService().selectButtonsPagation(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6761\u4ef6\u660e\u7ec6", type=LogType.SELECT)
    public Page<CoreCustomQueryConditionBean> selectConditionPagation(RestJsonWrapperBean wrapper) {
        return this.getService().selectConditionsPagation(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5b57\u6bb5\u9009\u62e9\u9875", type=LogType.SELECT)
    public Pagination<CoreCustomQueryFieldBean> selectFieldChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value="\u6279\u91cf\u65b0\u589e\u6309\u94ae", type=LogType.INSERT)
    public void insertQueryButtons(RestJsonWrapperBean wrapper) {
        this.getService().insertQueryButtons(wrapper);
    }

    @Override
    @Log(value="\u6279\u91cf\u65b0\u589e\u67e5\u65b0\u6761\u4ef6", type=LogType.INSERT)
    public void insertQueryConditions(RestJsonWrapperBean wrapper) {
        this.getService().insertQueryConditions(wrapper);
    }

    @Override
    @Log(value="\u6279\u91cf\u5220\u9664\u64cd\u4f5c\u6309\u94ae", type=LogType.DELETE)
    public void deleteQueryButtons(RestJsonWrapperBean wrapper) {
        this.getService().deleteQueryButtons(wrapper);
    }

    @Override
    @Log(value="\u6279\u91cf\u5220\u9664\u67e5\u8be2\u6761\u4ef6", type=LogType.DELETE)
    public void deleteQueryConditions(RestJsonWrapperBean wrapper) {
        this.getService().deleteQueryConditions(wrapper);
    }

    @Override
    @Log(value="\u53d1\u5e03\u83dc\u5355", type=LogType.INSERT)
    public void publish(Long id, RestJsonWrapperBean wrapper) {
        this.getService().publish(id, wrapper);
    }

    @Override
    @Log(value="\u64a4\u9500\u53d1\u5e03", type=LogType.DELETE)
    public void revoke(Long id) {
        this.getService().revoke(id);
    }

    @Override
    @Log(value="\u901a\u8fc7\u81ea\u5b9a\u4e49\u67e5\u8be2ID\u83b7\u53d6\u67e5\u8be2\u5b57\u6bb5", type=LogType.SELECT)
    public List<CoreCustomQueryFieldBean> selectQueryFieldList(Long id) {
        return this.getService().selectQueryFieldList(id);
    }

    @Override
    @Log(value="\u901a\u8fc7\u81ea\u5b9a\u4e49\u67e5\u8be2ID\u83b7\u53d6\u67e5\u8be2\u6309\u94ae", type=LogType.SELECT)
    public List<CoreCustomQueryButtonBean> selectQueryButtonList(Long id) {
        return this.getService().selectQueryButtonList(id);
    }

    @Override
    @Log(value="\u81ea\u5b9a\u4e49\u83dc\u5355\u5206\u9875\u67e5\u8be2", type=LogType.SELECT)
    public Pagination<Map<String, Object>> selectCustomMenuPagination(Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreCustomQueryService.selectCustomMenuPagination(id, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u67e5\u8be2\u6761\u4ef6", type=LogType.SELECT)
    public List<Map<String, Object>> buildQueryCondition(Long id) {
        return this.getService().buildHighQuery(id);
    }

    @Override
    @Log(value="\u6821\u9a8c\u81ea\u5b9a\u4e49SQL", type=LogType.VALIDATE)
    public RestValidationResultBean checkCustomSql(RestJsonWrapperBean wrapper) {
        return this.getService().checkCustomSql(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u81ea\u5b9a\u4e49SQL\u7ed3\u679c\u96c6", type=LogType.SELECT)
    public List<Map<String, Object>> getSelectItems(Long id, String field) {
        return this.getService().getSelectItems(id, field);
    }

    @Override
    @Log(value="\u83b7\u53d6\u96ea\u82b1\u7b97\u6cd5ID", type=LogType.SELECT)
    public Long getNextIdentity() {
        return ApplicationContextHelper.getNextIdentity();
    }
}
