/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service;

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
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface LcdpBizProcessService
extends GenericService<LcdpBizProcessBean, Long>,
GenericActivatableService<LcdpBizProcessBean, Long> {
    public void modify(RestJsonWrapperBean var1);

    public Page<LcdpBizProcessNodeBean> selectBpNodePaginationByBpId(Long var1, RestJsonWrapperBean var2);

    public Long insertBpNode(Long var1, RestJsonWrapperBean var2);

    public void deleteBpNode(RestJsonWrapperBean var1);

    public Page<LcdpBizProcessNodeUserBean> selectBpNodeUserPaginationByBpNodeId(Long var1, RestJsonWrapperBean var2);

    public Long insertBpNodeUser(RestJsonWrapperBean var1);

    public void deleteBpNodeUser(RestJsonWrapperBean var1);

    public Page<LcdpBizProcessNodeRoleBean> selectBpNodeRolePaginationByBpNodeId(Long var1, RestJsonWrapperBean var2);

    public Long insertBpNodeRole(RestJsonWrapperBean var1);

    public void deleteBpNodeRole(RestJsonWrapperBean var1);

    public Page<LcdpBizProcessNodeActionBean> selectBpNodeActionPaginationByBpNodeId(Long var1, RestJsonWrapperBean var2);

    public Long insertBpNodeAction(RestJsonWrapperBean var1);

    public void deleteBpNodeAction(RestJsonWrapperBean var1);

    public LcdpBizProcessNodeActionBean selectBpNodeAction(Long var1, String var2, RestJsonWrapperBean var3);

    public void swapNodeAction(Long var1, Long var2);

    public void setActionDefault(RestJsonWrapperBean var1);

    public void cancelActionDefault(RestJsonWrapperBean var1);

    public Page<LcdpBizProcessCommentBean> selectBpCommentPagination(RestJsonWrapperBean var1);

    public Page<LcdpBpNodeSignUserBean> selectSignUserPaginationByBpActionId(Long var1, RestJsonWrapperBean var2);

    public Long insertSignUser(RestJsonWrapperBean var1);

    public void deleteSignUser(RestJsonWrapperBean var1);

    public LcdpBizProcessNodeMenuBean selectBpNodeMenuDetailByBpNodeId(Long var1, RestJsonWrapperBean var2);

    public List<LcdpBizProcessNodeActionBean> selectBpNodeActionList(RestJsonWrapperBean var1);

    public void approval(RestJsonWrapperBean var1);

    public void approval(List<String> var1, String var2, String var3, String var4, String var5);

    public List<LcdpBizProcessDiagramDTO> selectBpDiagramById(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpBizProcessDiagramCandidatorDTO> selectTaskCandidatorsByBpId(Long var1, RestJsonWrapperBean var2);

    public void submit(List<Map<String, Object>> var1, String var2, String var3);

    public LcdpBizProcessNodeBean selectBpNodeDetail(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpBpInstBean> selectBpInstanceList(RestJsonWrapperBean var1);

    public boolean determineInitialNode(String var1, String var2);

    public RestValidationResultBean validateBpNodeUnique(Long var1, RestJsonWrapperBean var2);

    public void saveScript(RestJsonWrapperBean var1);
}
