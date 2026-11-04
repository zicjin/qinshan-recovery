/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.resource;

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
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/bps"})
public interface LcdpBizProcessResource
extends GenericCloudResource<LcdpBizProcessBean, Long>,
GenericActivatableCloudResource<LcdpBizProcessBean, Long> {
    @RequestMapping(value={"/action/modify"}, method={RequestMethod.PUT})
    public void modify(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/nodes/queries"}, method={RequestMethod.POST})
    public Page<LcdpBizProcessNodeBean> selectBpNodePaginationByBpId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/nodes/action/validate-unique"}, method={RequestMethod.POST})
    public RestValidationResultBean validateBpNodeUnique(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/nodes/{bpNodeId}"}, method={RequestMethod.GET})
    public LcdpBizProcessNodeBean selectBpNodeDetail(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/nodes"}, method={RequestMethod.POST})
    public Long insertBpNode(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/nodes"}, method={RequestMethod.DELETE})
    public void deleteBpNode(RestJsonWrapperBean var1);

    @RequestMapping(value={"/nodes/{bpNodeId}/users/queries"}, method={RequestMethod.POST})
    public Page<LcdpBizProcessNodeUserBean> selectBpNodeUserPaginationByBpNodeId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/node-users"}, method={RequestMethod.POST})
    public Long insertBpNodeUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/node-users"}, method={RequestMethod.DELETE})
    public void deleteBpNodeUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/nodes/{bpNodeId}/roles/queries"}, method={RequestMethod.POST})
    public Page<LcdpBizProcessNodeRoleBean> selectBpNodeRolePaginationByBpNodeId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/node-roles"}, method={RequestMethod.POST})
    public Long insertBpNodeRole(RestJsonWrapperBean var1);

    @RequestMapping(value={"/node-roles"}, method={RequestMethod.DELETE})
    public void deleteBpNodeRole(RestJsonWrapperBean var1);

    @RequestMapping(value={"/nodes/{bpNodeId}/actions/queries"}, method={RequestMethod.POST})
    public Page<LcdpBizProcessNodeActionBean> selectBpNodeActionPaginationByBpNodeId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/node-actions"}, method={RequestMethod.POST})
    public Long insertBpNodeAction(RestJsonWrapperBean var1);

    @RequestMapping(value={"/node-actions"}, method={RequestMethod.DELETE})
    public void deleteBpNodeAction(RestJsonWrapperBean var1);

    @RequestMapping(value={"/node-actions/{bpActionId}/{scriptType}/queries"}, method={RequestMethod.GET})
    public LcdpBizProcessNodeActionBean selectBpNodeAction(@PathVariable Long var1, @PathVariable String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/node-actions/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapNodeAction(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/node-actions/action/set-default"}, method={RequestMethod.PUT})
    public void setActionDefault(RestJsonWrapperBean var1);

    @RequestMapping(value={"/node-actions/action/cancel-default"}, method={RequestMethod.PUT})
    public void cancelActionDefault(RestJsonWrapperBean var1);

    @RequestMapping(value={"/comments/queries"}, method={RequestMethod.POST})
    public Page<LcdpBizProcessCommentBean> selectBpCommentPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/node-actions/{bpActionId}/sign-users/queries"}, method={RequestMethod.POST})
    public Page<LcdpBpNodeSignUserBean> selectSignUserPaginationByBpActionId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/sign-users"}, method={RequestMethod.POST})
    public Long insertSignUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/sign-users"}, method={RequestMethod.DELETE})
    public void deleteSignUser(RestJsonWrapperBean var1);

    @RequestMapping(value={"/nodes/{bpNodeId}/menu-info"}, method={RequestMethod.GET})
    public LcdpBizProcessNodeMenuBean selectBpNodeMenuDetailByBpNodeId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/diagram/queries"}, method={RequestMethod.POST})
    public List<LcdpBizProcessDiagramDTO> selectBpDiagramById(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/node-candidators/queries"}, method={RequestMethod.POST})
    public Page<LcdpBizProcessDiagramCandidatorDTO> selectTaskCandidatorsByBpId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Override
    @RequestMapping(value={"/action/activate"}, method={RequestMethod.PUT})
    public void activate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/save-script"}, method={RequestMethod.PUT})
    public void saveScript(RestJsonWrapperBean var1);

    @RequestMapping(value={"/instances/queries"}, method={RequestMethod.POST})
    public Page<LcdpBpInstBean> selectBpInstanceList(RestJsonWrapperBean var1);
}
