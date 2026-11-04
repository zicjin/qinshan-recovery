/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.resource.impl;

import com.sunwayworld.cloud.module.lcdp.base.LcdpResultDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpJavaStructureDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBacthCheckoutDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCompareDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpServerScriptMethodBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpWorkspaceDTO;
import com.sunwayworld.cloud.module.lcdp.resource.resource.LcdpResourceResource;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.validator.LcpdResourceBatchDeleteableValidator;
import com.sunwayworld.cloud.module.lcdp.resource.validator.LcpdResourceCreateableByTmplValidator;
import com.sunwayworld.cloud.module.lcdp.resource.validator.LcpdResourceDeleteableValidator;
import com.sunwayworld.cloud.module.lcdp.resource.validator.LcpdResourceMoveableValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.validator.CoreFileUploadValidator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90")
@RestController
@GikamBean
public class LcdpResourceResourceImpl
implements LcdpResourceResource,
AbstractGenericResource<LcdpResourceService, LcdpResourceBean, Long>,
AbstractGenericChoosableResource<LcdpResourceService, LcdpResourceBean, Long> {
    @Autowired
    private LcdpResourceService lcdpResourceService;

    @Override
    public LcdpResourceService getService() {
        return this.lcdpResourceService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90", type=LogType.INSERT)
    @ValidateDataWith(value=LcpdResourceDeleteableValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u7f16\u8f91\u9875\u9762\u67e5\u8be2\u6811", type=LogType.SELECT)
    public List<LcdpResourceTreeNodeDTO> selectEditTree(String parentId, RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectEditTree(parentId, jsonWrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90\u8868ID\u67e5\u8be2\u529f\u80fd\u6a21\u5757\u9875\u9762/\u524d\u540e\u7aef\u811a\u672c\u5185\u5bb9", type=LogType.SELECT)
    public LcdpResourceDTO selectResourceContent(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectResourceContent(id, wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u68c0\u51fa", type=LogType.UPDATE)
    public LcdpResourceDTO checkout(Long id) {
        return this.getService().checkout(id);
    }

    @Override
    @Log(value="\u8d44\u6e90\u66ff\u6362", type=LogType.UPDATE)
    public void replace(RestJsonWrapperBean wrapper) {
        this.getService().replace(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u6240\u6709\u7684\u5206\u7c7b\u7528\u4e8e\u590d\u5236\uff0c\u79fb\u52a8\u7b49\u64cd\u4f5c", type=LogType.SELECT)
    public List<LcdpResourceDTO> selectCategorySelectableList() {
        return this.getService().selectCategorySelectableList();
    }

    @Override
    @Log(value="\u8d44\u6e90\u590d\u5236(\u9875\u9762\uff0c\u524d\u540e\u7aef\u811a\u672c)", type=LogType.INSERT)
    public Long copyResource(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().copyResource(id, wrapper);
    }

    @Override
    @Log(value="\u6a21\u5757\u590d\u5236", type=LogType.UPDATE)
    public Long copyModule(Long moduleId, RestJsonWrapperBean wrapper) {
        return this.getService().copyModule(moduleId, wrapper);
    }

    @Override
    @Log(value="\u6a21\u5757\u91cd\u547d\u540d", type=LogType.UPDATE)
    public void renameModule(Long moduleId, RestJsonWrapperBean wrapper) {
        this.getService().renameModule(moduleId, wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u6587\u4ef6\u91cd\u547d\u540d", type=LogType.UPDATE)
    public void renameResource(Long id, RestJsonWrapperBean wrapper) {
        this.getService().renameResource(id, wrapper);
    }

    @Override
    @Log(value="\u6a21\u5757\u79fb\u52a8", type=LogType.UPDATE)
    @ValidateDataWith(value=LcpdResourceMoveableValidator.class)
    public Long moveModule(Long moduleId, RestJsonWrapperBean wrapper) {
        return this.getService().moveModule(moduleId, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u672a\u63d0\u4ea4\u7684\u8d44\u6e90", type=LogType.SELECT)
    public List<LcdpResourceTreeNodeDTO> selectUncommittedTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectUncommittedTree(wrapper);
    }

    @Override
    @Log(value="\u8c03\u7528\u540e\u7aef\u811a\u672c", type=LogType.SELECT)
    public String callScript(RestJsonWrapperBean wrapper) {
        return this.getService().callScript(wrapper);
    }

    @Override
    @Log(value="\u8c03\u7528\u540e\u7aef\u4e0b\u8f7d\u811a\u672c", type=LogType.SELECT)
    public void callDownloadScript(String path, String env) {
        this.getService().callDownloadScript(path, env);
    }

    @Override
    @Log(value="\u8c03\u7528\u540e\u7aef\u6587\u4ef6\u4e0a\u4f20\u811a\u672c", type=LogType.SELECT)
    @ValidateDataWith(value=CoreFileUploadValidator.class)
    public String fileUploadCallScript(CoreFileBean coreFileBean, MultipartFile file) {
        return this.getService().callScript(coreFileBean, file);
    }

    @Override
    @Log(value="\u624b\u52a8\u4fdd\u5b58", type=LogType.UPDATE)
    public void manualSave(RestJsonWrapperBean wrapper) {
        this.getService().manualSave(wrapper);
    }

    @Override
    @Log(value="\u5185\u5bb9\u5bf9\u6bd4", type=LogType.SELECT)
    public LcdpResourceCompareDTO compare(RestJsonWrapperBean wrapper) {
        return this.getService().compare(wrapper);
    }

    @Override
    @Log(value="\u5386\u53f2\u8d44\u6e90\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    public LcdpResourceCompareDTO selectHistoryDetail(Long historyId, RestJsonWrapperBean wrapper) {
        return this.getService().selectHistoryDetail(historyId, wrapper);
    }

    @Override
    @Log(value="\u6a21\u5757\u6811\u67e5\u8be2\uff0c\u4e24\u7ea7\u8282\u70b9 \u5206\u7c7b/\u6a21\u5757\u6811", type=LogType.SELECT)
    public List<LcdpResourceTreeNodeDTO> selectModuleTree() {
        return this.getService().selectModuleTree();
    }

    @Override
    @Log(value="\u8d44\u6e90\u79fb\u52a8", type=LogType.INSERT)
    @ValidateDataWith(value=LcpdResourceMoveableValidator.class)
    public Long moveResource(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().moveResource(id, wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u5bfc\u51fa\u6811", type=LogType.SELECT)
    public List<LcdpResourceTreeNodeDTO> selectExportTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectExportTree(wrapper);
    }

    @Override
    @Log(value="\u540e\u7aef\u811a\u672c\u65b9\u6cd5\u8def\u5f84\u4e0b\u62c9\u6846\u67e5\u8be2", type=LogType.SELECT)
    public List<LcdpServerScriptMethodBean> selectServerScriptMethodSelectableList(RestJsonWrapperBean wrapper) {
        return this.getService().selectServerScriptMethodSelectableList(wrapper);
    }

    @Override
    public Page<LcdpServerScriptMethodBean> selectServerScriptApiMethodList(RestJsonWrapperBean wrapper) {
        return this.getService().selectServerScriptApiMethodList(wrapper);
    }

    @Override
    @Log(value="\u64a4\u9500\u68c0\u51fa", type=LogType.UPDATE)
    public void revert(RestJsonWrapperBean wrapper) {
        this.getService().revert(wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u5bfc\u51fa", type=LogType.UPDATE)
    public String export(RestJsonWrapperBean wrapper) {
        return this.getService().export(wrapper, null);
    }

    @Override
    @Log(value="\u67e5\u8be2\u8d44\u6e90\u5bfc\u5165\u6811", type=LogType.SELECT)
    public LcdpExportLogFileDTO importByFile(Long fileId, RestJsonWrapperBean wrapper) {
        return this.getService().importByFile(fileId, wrapper);
    }

    @Override
    @Log(value="\u5bfc\u5165\u8d44\u6e90\u5206\u6790", type=LogType.SELECT)
    public LcdpExportLogFileDTO importAnalyse(RestJsonWrapperBean wrapper) {
        return this.getService().importAnalyse(wrapper);
    }

    @Override
    @Log(value="\u5bfc\u5165\u8d44\u6e90", type=LogType.INSERT)
    public String importData(RestJsonWrapperBean wrapper) {
        return this.getService().importData(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u811a\u672c\u8def\u5f84\u67e5\u8be2\u811a\u672c", type=LogType.SELECT)
    public LcdpResourceDTO selectContentByPath(RestJsonWrapperBean wrapper) {
        return this.getService().selectContentByPath(wrapper);
    }

    @Override
    @Log(value="\u9875\u9762\u8d44\u6e90\u53d1\u5e03\u4e3a\u83dc\u5355", type=LogType.INSERT)
    public void publishMenu(RestJsonWrapperBean wrapper) {
        this.getService().publishMenu(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6a21\u677f\u65b0\u589e\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90", type=LogType.INSERT)
    @ValidateDataWith(value=LcpdResourceCreateableByTmplValidator.class)
    public Long insertByModuleTmpl(RestJsonWrapperBean wrapper) {
        return this.getService().insertByModuleTmpl(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u540d\u79f0\u641c\u7d22\u8d44\u6e90\u6811", type=LogType.SELECT)
    public List<LcdpResourceTreeNodeDTO> selectResourceTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectResourceTree(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5de5\u4f5c\u533a\u6570\u636e", type=LogType.SELECT)
    public LcdpWorkspaceDTO selectWorkSpaceData(RestJsonWrapperBean wrapper) {
        return this.getService().selectWorkSpaceData(wrapper);
    }

    @Override
    @Log(value="\u6a21\u5757\u4e0b\u8d44\u6e90\u6279\u91cf\u68c0\u51fa", type=LogType.UPDATE)
    public LcdpResourceBacthCheckoutDTO batchCheckout(RestJsonWrapperBean wrapper) {
        return this.getService().batchCheckout(wrapper);
    }

    @Override
    @Log(value="\u8d44\u6e90\u6279\u91cf\u5220\u9664", type=LogType.DELETE)
    @ValidateDataWith(value=LcpdResourceBatchDeleteableValidator.class)
    public List<LcdpResourceBean> batchDelete(RestJsonWrapperBean wrapper) {
        return this.getService().batchDelete(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6240\u6709\u7528\u6237\u68c0\u51fa\u6570\u636e", type=LogType.DELETE)
    public List<LcdpResourceTreeNodeDTO> selectAllCheckoutTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectAllCheckoutTree(wrapper);
    }

    @Override
    @Log(value="\u6a21\u5757\u4e0b\u8d44\u6e90\u64a4\u9500\u68c0\u51fa", type=LogType.UPDATE)
    public void moduleRevert(RestJsonWrapperBean wrapper) {
        this.getService().moduleRevert(wrapper);
    }

    @Override
    @Log(value="java\u811a\u672c\u4fdd\u5b58", type=LogType.UPDATE)
    public LcdpResultDTO saveJavaCodes(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().saveJavaCodes(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u53ef\u9009\u9875\u9762", type=LogType.SELECT)
    public Page<LcdpResourceBean> selectPageChoosablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectPageChoosablePagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2java\u811a\u672c\u7ed3\u6784", type=LogType.SELECT)
    public List<LcdpJavaStructureDTO> selectJavaScriptStructure(RestJsonWrapperBean wrapper) {
        return this.getService().selectJavaScriptStructure(wrapper);
    }

    @Override
    @Log(value="\u5bfc\u5165\u8d44\u6e90\u5bf9\u6bd4", type=LogType.SELECT)
    public LcdpResourceCompareDTO importResourcesCompare(RestJsonWrapperBean wrapper) {
        return this.getService().importResourcesCompare(wrapper);
    }

    @Override
    @Log(value="\u5168\u5c40\u5c5e\u6027\u5386\u53f2\u6570\u636e\u5904\u7406", type=LogType.UPDATE)
    public void dealPageComps(RestJsonWrapperBean wrapper) {
        this.getService().dealPageComps(wrapper);
    }

    @Override
    public LcdpResultDTO compileJavaCodes(RestJsonWrapperBean wrapper) {
        return this.getService().compileJavaCodes(wrapper);
    }

    @Override
    public List<String> generatePackageImport(RestJsonWrapperBean wrapper) {
        return this.getService().generatePackageImport(wrapper);
    }

    @Override
    public String getCodeSource(String className) {
        return this.getService().getJavaCodeSource(className);
    }

    @Override
    @Log(value="\u901a\u8fc7\u7c7b\u540d\u83b7\u53d6\u6216\u53cd\u7f16\u8bd1\u6e90\u7801", type=LogType.UPDATE)
    public LcdpResourceDTO selectResourceByClassName(RestJsonWrapperBean wrapper) {
        return this.getService().selectResourceByClassName(wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u9875\u9762\u6a21\u677f\u65b0\u5efa\u9875\u9762", type=LogType.INSERT)
    public Long insertByPageTmpl(RestJsonWrapperBean wrapper) {
        return this.getService().insertByPageTmpl(wrapper);
    }

    @Override
    public List<LcdpResourceTreeNodeDTO> selectCheckoutTree(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectCheckoutTree(jsonWrapper);
    }

    @Override
    public Set<String> getCodeHintsKeyWords() {
        return this.getService().getCodeHintsKeyWords();
    }

    @Override
    @Log(value="\u6839\u636e\u8d44\u6e90\u8def\u5f84\u67e5\u8be2\u811a\u672cID", type=LogType.SELECT)
    public Long selectByPath(RestJsonWrapperBean wrapper) {
        return this.getService().selectByPath(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u9875\u9762\u8def\u5f84\u67e5\u8be2\u9875\u9762ID", type=LogType.SELECT)
    public Long selectPageIdByPath(RestJsonWrapperBean wrapper) {
        return this.getService().selectPageIdByPath(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u4e0a\u4e00\u6b21\u6253\u5f00\u7684\u8d44\u6e90Id\u96c6\u5408\u67e5\u8be2\u6811", type=LogType.SELECT)
    public List<LcdpResourceTreeNodeDTO> selectOpenedResourceTree(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectOpenedResourceTree(jsonWrapper);
    }
}
