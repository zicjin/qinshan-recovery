/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service;

import com.sunwayworld.cloud.module.lcdp.base.LcdpResultDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpExportLogFileDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpJavaStructureDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBacthCheckoutDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCompareDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpServerScriptMethodBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpWorkspaceDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;
import java.util.Set;

public interface LcdpResourceService
extends GenericService<LcdpResourceBean, Long>,
GenericChoosableService<LcdpResourceBean, Long> {
    public List<LcdpResourceTreeNodeDTO> selectEditTree(String var1, RestJsonWrapperBean var2);

    public LcdpResourceDTO selectResourceContent(Long var1, RestJsonWrapperBean var2);

    public LcdpResourceDTO checkout(Long var1);

    public void replace(RestJsonWrapperBean var1);

    public List<LcdpResourceDTO> selectCategorySelectableList();

    public Long copyResource(Long var1, RestJsonWrapperBean var2);

    public Long copyModule(Long var1, RestJsonWrapperBean var2);

    public void renameResource(Long var1, RestJsonWrapperBean var2);

    public void renameModule(Long var1, RestJsonWrapperBean var2);

    public Long moveModule(Long var1, RestJsonWrapperBean var2);

    public List<LcdpResourceTreeNodeDTO> selectUncommittedTree(RestJsonWrapperBean var1);

    public void submitResource(List<String> var1);

    public String callScript(Object ... var1);

    public String callScheduleScript(String var1, Object ... var2);

    public void callDownloadScript(String var1, String var2);

    public void manualSave(RestJsonWrapperBean var1);

    public String getResourceClassName(String var1);

    public String getDevResourceContent(String var1);

    public LcdpResourceCompareDTO compare(RestJsonWrapperBean var1);

    public List<LcdpResourceTreeNodeDTO> selectModuleTree();

    public Long moveResource(Long var1, RestJsonWrapperBean var2);

    public void activateResource(String var1, Long var2);

    public List<LcdpResourceTreeNodeDTO> selectExportTree(RestJsonWrapperBean var1);

    public List<LcdpServerScriptMethodBean> selectServerScriptMethodSelectableList(RestJsonWrapperBean var1);

    public Page<LcdpServerScriptMethodBean> selectServerScriptApiMethodList(RestJsonWrapperBean var1);

    public void revert(RestJsonWrapperBean var1);

    public String export(RestJsonWrapperBean var1, LcdpCheckoutRecordBean var2);

    public LcdpExportLogFileDTO importAnalyse(RestJsonWrapperBean var1);

    public LcdpExportLogFileDTO importByFile(Long var1, RestJsonWrapperBean var2);

    public String importData(RestJsonWrapperBean var1);

    public LcdpResourceCompareDTO selectHistoryDetail(Long var1, RestJsonWrapperBean var2);

    public LcdpResourceDTO selectContentByPath(RestJsonWrapperBean var1);

    public void publishMenu(RestJsonWrapperBean var1);

    public Long insertByModuleTmpl(RestJsonWrapperBean var1);

    public List<LcdpModulePageCompBean> generatePageComps(List<LcdpModulePageCompBean> var1);

    public void insertServerScriptMethod(List<LcdpResourceHistoryBean> var1);

    public List<LcdpResourceTreeNodeDTO> selectResourceTree(RestJsonWrapperBean var1);

    public LcdpWorkspaceDTO selectWorkSpaceData(RestJsonWrapperBean var1);

    public LcdpResourceBacthCheckoutDTO batchCheckout(RestJsonWrapperBean var1);

    public List<LcdpResourceBean> batchDelete(RestJsonWrapperBean var1);

    public List<LcdpResourceTreeNodeDTO> selectAllCheckoutTree(RestJsonWrapperBean var1);

    public void moduleRevert(RestJsonWrapperBean var1);

    public void saveContent(LcdpResourceHistoryBean var1);

    public LcdpResultDTO saveJavaCodes(Long var1, RestJsonWrapperBean var2);

    public Page<LcdpResourceBean> selectPageChoosablePagination(RestJsonWrapperBean var1);

    public List<LcdpJavaStructureDTO> selectJavaScriptStructure(RestJsonWrapperBean var1);

    public String replaceScriptContent(String var1);

    public LcdpResourceCompareDTO importResourcesCompare(RestJsonWrapperBean var1);

    public void dealPageComps(RestJsonWrapperBean var1);

    public LcdpResultDTO compileJavaCodes(RestJsonWrapperBean var1);

    public List<String> generatePackageImport(RestJsonWrapperBean var1);

    public String getJavaCodeSource(String var1);

    public LcdpResourceDTO selectResourceByClassName(RestJsonWrapperBean var1);

    public Long insertByPageTmpl(RestJsonWrapperBean var1);

    public List<LcdpResourceTreeNodeDTO> selectCheckoutTree(RestJsonWrapperBean var1);

    public Page<?> callTodoScript(String var1, RestJsonWrapperBean var2);

    public Set<String> getCodeHintsKeyWords();

    public Long selectByPath(RestJsonWrapperBean var1);

    public List<LcdpResourceTreeNodeDTO> selectOpenedResourceTree(RestJsonWrapperBean var1);

    public Long selectPageIdByPath(RestJsonWrapperBean var1);
}
