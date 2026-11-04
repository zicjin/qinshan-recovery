/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.resource.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessCommentBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessDiagramCandidatorDTO;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessDiagramDTO;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeMenuBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeRoleBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpNodeSignUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.resource.LcdpBizProcessResource;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessService;
import com.sunwayworld.cloud.module.lcdp.bp.validator.LcdpBizProcessActivateValidator;
import com.sunwayworld.cloud.module.lcdp.bp.validator.LcdpBpActionSaveValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5de5\u4f5c\u6d41")
@RestController
@GikamBean
public class LcdpBizProcessResourceImpl
implements LcdpBizProcessResource,
AbstractGenericResource<LcdpBizProcessService, LcdpBizProcessBean, Long>,
AbstractGenericActivatableResource<LcdpBizProcessService, LcdpBizProcessBean, Long> {
    @Autowired
    private LcdpBizProcessService lcdpBizProcessService;

    @Override
    public LcdpBizProcessService getService() {
        return this.lcdpBizProcessService;
    }

    @Override
    @Log(value="\u4e1a\u52a1\u5de5\u4f5c\u6d41\u53d8\u66f4", type=LogType.UPDATE)
    public void modify(RestJsonWrapperBean wrapper) {
        this.getService().modify(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41ID\u67e5\u8be2\u5de5\u4f5c\u6d41\u6d41\u7a0b\u8282\u70b9", type=LogType.SELECT)
    public Page<LcdpBizProcessNodeBean> selectBpNodePaginationByBpId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodePaginationByBpId(id, wrapper);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u6d41\u7a0b\u8282\u70b9\u552f\u4e00\u6027", type=LogType.VALIDATE)
    public RestValidationResultBean validateBpNodeUnique(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().validateBpNodeUnique(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5de5\u4f5c\u6d41\u6d41\u7a0b\u8282\u70b9", type=LogType.INSERT)
    public Long insertBpNode(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertBpNode(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5de5\u4f5c\u6d41\u6d41\u7a0b\u8282\u70b9", type=LogType.DELETE)
    public void deleteBpNode(RestJsonWrapperBean wrapper) {
        this.getService().deleteBpNode(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9ID\u67e5\u8be2\u6d41\u7a0b\u8282\u70b9\u7528\u6237", type=LogType.SELECT)
    public Page<LcdpBizProcessNodeUserBean> selectBpNodeUserPaginationByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeUserPaginationByBpNodeId(bpNodeId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6d41\u7a0b\u8282\u70b9\u7528\u6237", type=LogType.INSERT)
    public Long insertBpNodeUser(RestJsonWrapperBean wrapper) {
        return this.getService().insertBpNodeUser(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6d41\u7a0b\u8282\u70b9\u7528\u6237", type=LogType.DELETE)
    public void deleteBpNodeUser(RestJsonWrapperBean wrapper) {
        this.getService().deleteBpNodeUser(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9ID\u67e5\u8be2\u6d41\u7a0b\u8282\u70b9\u89d2\u8272", type=LogType.SELECT)
    public Page<LcdpBizProcessNodeRoleBean> selectBpNodeRolePaginationByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeRolePaginationByBpNodeId(bpNodeId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6d41\u7a0b\u8282\u70b9\u89d2\u8272", type=LogType.INSERT)
    public Long insertBpNodeRole(RestJsonWrapperBean wrapper) {
        return this.getService().insertBpNodeRole(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6d41\u7a0b\u8282\u70b9\u89d2\u8272", type=LogType.DELETE)
    public void deleteBpNodeRole(RestJsonWrapperBean wrapper) {
        this.getService().deleteBpNodeRole(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9ID\u67e5\u8be2\u6d41\u7a0b\u8282\u70b9\u52a8\u4f5c", type=LogType.SELECT)
    public Page<LcdpBizProcessNodeActionBean> selectBpNodeActionPaginationByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeActionPaginationByBpNodeId(bpNodeId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6d41\u7a0b\u8282\u70b9\u52a8\u4f5c", type=LogType.INSERT)
    public Long insertBpNodeAction(RestJsonWrapperBean wrapper) {
        return this.getService().insertBpNodeAction(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6d41\u7a0b\u8282\u70b9\u52a8\u4f5c", type=LogType.DELETE)
    public void deleteBpNodeAction(RestJsonWrapperBean wrapper) {
        this.getService().deleteBpNodeAction(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9\u52a8\u4f5cID\u67e5\u8be2\u6d41\u7a0b\u52a8\u4f5c", type=LogType.SELECT)
    public LcdpBizProcessNodeActionBean selectBpNodeAction(Long bpActionId, String scriptType, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeAction(bpActionId, scriptType, wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u8282\u70b9\u52a8\u4f5c\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    public void swapNodeAction(Long firstId, Long secondId) {
        this.getService().swapNodeAction(firstId, secondId);
    }

    @Override
    @Log(value="\u8bbe\u7f6e\u6d41\u7a0b\u52a8\u4f5c\u9ed8\u8ba4", type=LogType.UPDATE)
    public void setActionDefault(RestJsonWrapperBean wrapper) {
        this.getService().setActionDefault(wrapper);
    }

    @Override
    @Log(value="\u53d6\u6d88\u6d41\u7a0b\u52a8\u4f5c\u9ed8\u8ba4", type=LogType.UPDATE)
    public void cancelActionDefault(RestJsonWrapperBean wrapper) {
        this.getService().cancelActionDefault(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41ID\u67e5\u8be2\u5de5\u4f5c\u6d41\u5ba1\u6838\u610f\u89c1", type=LogType.SELECT)
    public Page<LcdpBizProcessCommentBean> selectBpCommentPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectBpCommentPagination(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9\u52a8\u4f5cID\u67e5\u8be2\u4f1a\u7b7e\u7528\u6237", type=LogType.SELECT)
    public Page<LcdpBpNodeSignUserBean> selectSignUserPaginationByBpActionId(Long bpActionId, RestJsonWrapperBean wrapper) {
        return this.getService().selectSignUserPaginationByBpActionId(bpActionId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u4f1a\u7b7e\u7528\u6237", type=LogType.INSERT)
    public Long insertSignUser(RestJsonWrapperBean wrapper) {
        return this.getService().insertSignUser(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4f1a\u7b7e\u7528\u6237", type=LogType.DELETE)
    public void deleteSignUser(RestJsonWrapperBean wrapper) {
        this.getService().deleteSignUser(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9ID\u67e5\u8be2\u6d41\u7a0b\u8282\u70b9\u83dc\u5355", type=LogType.SELECT)
    public LcdpBizProcessNodeMenuBean selectBpNodeMenuDetailByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeMenuDetailByBpNodeId(bpNodeId, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9ID\u67e5\u8be2\u6d41\u7a0b\u56fe", type=LogType.SELECT)
    public List<LcdpBizProcessDiagramDTO> selectBpDiagramById(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpDiagramById(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5de5\u4f5c\u6d41\u8282\u70b9ID\u67e5\u8be2\u8282\u70b9\u5bf9\u5e94\u7684\u5ba1\u6838\u7528\u6237\u548c\u5ba1\u6838\u89d2\u8272", type=LogType.SELECT)
    public Page<LcdpBizProcessDiagramCandidatorDTO> selectTaskCandidatorsByBpId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectTaskCandidatorsByBpId(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8282\u70b9ID\u67e5\u8be2\u8282\u70b9\u8be6\u60c5", type=LogType.SELECT)
    public LcdpBizProcessNodeBean selectBpNodeDetail(Long bpNodeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectBpNodeDetail(bpNodeId, wrapper);
    }

    @Override
    @Log(value="\u542f\u7528\u5de5\u4f5c\u6d41", type=LogType.ACTIVATE)
    @ValidateDataWith(value=LcdpBizProcessActivateValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }

    @Override
    @Log(value="\u4fee\u6539\u5de5\u4f5c\u6d41\u811a\u672c", type=LogType.UPDATE)
    @ValidateDataWith(value=LcdpBpActionSaveValidator.class)
    public void saveScript(RestJsonWrapperBean wrapper) {
        this.getService().saveScript(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6d41\u7a0b\u5b9e\u4f8b", type=LogType.SELECT)
    public Page<LcdpBpInstBean> selectBpInstanceList(RestJsonWrapperBean wrapper) {
        return this.getService().selectBpInstanceList(wrapper);
    }
}
