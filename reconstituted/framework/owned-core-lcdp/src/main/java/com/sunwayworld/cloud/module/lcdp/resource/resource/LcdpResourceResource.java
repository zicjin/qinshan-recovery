/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.resource;

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
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.javassist.CannotCompileException;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value={"/secure/core/module/lcdp/resources"})
public interface LcdpResourceResource
extends GenericCloudResource<LcdpResourceBean, Long>,
GenericChoosableCloudResource<LcdpResourceBean, Long> {
    @RequestMapping(value={"/edit-tree/{parentId}"}, method={RequestMethod.POST})
    public List<LcdpResourceTreeNodeDTO> selectEditTree(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/content"}, method={RequestMethod.GET})
    public LcdpResourceDTO selectResourceContent(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/checkout"}, method={RequestMethod.POST})
    public LcdpResourceDTO checkout(@PathVariable Long var1);

    @RequestMapping(value={"/action/replace"}, method={RequestMethod.PUT})
    public void replace(RestJsonWrapperBean var1);

    @RequestMapping(value={"/categories/queries/selectable"}, method={RequestMethod.GET})
    public List<LcdpResourceDTO> selectCategorySelectableList();

    @RequestMapping(value={"/modules/{moduleId}/action/move"}, method={RequestMethod.PUT})
    public Long moveModule(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/uncommitted/tree/queries"}, method={RequestMethod.GET})
    public List<LcdpResourceTreeNodeDTO> selectUncommittedTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/call-script"}, method={RequestMethod.POST})
    public String callScript(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/call-script"}, method={RequestMethod.GET})
    public void callDownloadScript(@RequestParam(value="script-path") String var1, @RequestParam(value="lcdp-env", required=false) String var2);

    @RequestMapping(value={"/action/file-upload/call-script"}, method={RequestMethod.POST})
    public String fileUploadCallScript(CoreFileBean var1, MultipartFile var2);

    @RequestMapping(value={"/action/manual-save"}, method={RequestMethod.PUT})
    public void manualSave(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/compare"}, method={RequestMethod.POST})
    public LcdpResourceCompareDTO compare(RestJsonWrapperBean var1);

    @RequestMapping(value={"/histories/{historyId}"}, method={RequestMethod.POST})
    public LcdpResourceCompareDTO selectHistoryDetail(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/modules/tree"}, method={RequestMethod.GET})
    public List<LcdpResourceTreeNodeDTO> selectModuleTree();

    @RequestMapping(value={"/{id}/action/copy"}, method={RequestMethod.POST})
    public Long copyResource(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/modules/{moduleId}/action/copy"}, method={RequestMethod.POST})
    public Long copyModule(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/rename"}, method={RequestMethod.POST})
    public void renameResource(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/modules/{moduleId}/action/rename"}, method={RequestMethod.POST})
    public void renameModule(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/move"}, method={RequestMethod.PUT})
    public Long moveResource(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/export-tree"}, method={RequestMethod.POST})
    public List<LcdpResourceTreeNodeDTO> selectExportTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/server-script-methods/queries/selectable"}, method={RequestMethod.POST})
    public List<LcdpServerScriptMethodBean> selectServerScriptMethodSelectableList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/server-script-methods/api-method/queries"}, method={RequestMethod.POST})
    public Page<LcdpServerScriptMethodBean> selectServerScriptApiMethodList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/revert"}, method={RequestMethod.POST})
    public void revert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/export"}, method={RequestMethod.POST})
    public String export(RestJsonWrapperBean var1);

    @RequestMapping(value={"/files/{fileId}/action/import"}, method={RequestMethod.GET})
    public LcdpExportLogFileDTO importByFile(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/import-analyse"}, method={RequestMethod.POST})
    public LcdpExportLogFileDTO importAnalyse(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/import"}, method={RequestMethod.POST})
    public String importData(RestJsonWrapperBean var1);

    @RequestMapping(value={"/source-module-tmpl"}, method={RequestMethod.POST})
    public Long insertByModuleTmpl(RestJsonWrapperBean var1);

    @RequestMapping(value={"/search-by-path"}, method={RequestMethod.POST})
    public LcdpResourceDTO selectContentByPath(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/publish-menu"}, method={RequestMethod.POST})
    public void publishMenu(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/tree"}, method={RequestMethod.POST})
    public List<LcdpResourceTreeNodeDTO> selectResourceTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/workspace"}, method={RequestMethod.POST})
    public LcdpWorkspaceDTO selectWorkSpaceData(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/batch-checkout"}, method={RequestMethod.POST})
    public LcdpResourceBacthCheckoutDTO batchCheckout(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/batch-delete"}, method={RequestMethod.POST})
    public List<LcdpResourceBean> batchDelete(RestJsonWrapperBean var1);

    @RequestMapping(value={"/all-checkout/queries/tree"}, method={RequestMethod.GET})
    public List<LcdpResourceTreeNodeDTO> selectAllCheckoutTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/module-revert"}, method={RequestMethod.POST})
    public void moduleRevert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/java-codes/{id}"}, method={RequestMethod.PUT})
    public LcdpResultDTO saveJavaCodes(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/pages/queries/choosable"}, method={RequestMethod.POST})
    public Page<LcdpResourceBean> selectPageChoosablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/java-structures/queries"}, method={RequestMethod.POST})
    public List<LcdpJavaStructureDTO> selectJavaScriptStructure(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/import-resources/compare"}, method={RequestMethod.POST})
    public LcdpResourceCompareDTO importResourcesCompare(RestJsonWrapperBean var1);

    @RequestMapping(value={"/page-comps/action/deal"}, method={RequestMethod.POST})
    public void dealPageComps(RestJsonWrapperBean var1);

    @RequestMapping(value={"/java-codes/action/compile"}, method={RequestMethod.POST})
    public LcdpResultDTO compileJavaCodes(RestJsonWrapperBean var1);

    @RequestMapping(value={"/java-codes/action/package-import-generate"}, method={RequestMethod.POST})
    public List<String> generatePackageImport(RestJsonWrapperBean var1);

    @RequestMapping(value={"/code-sources/{className}"}, method={RequestMethod.GET})
    public String getCodeSource(@PathVariable String var1) throws NotFoundException, IOException, CannotCompileException;

    @RequestMapping(value={"/search-by-classname"}, method={RequestMethod.POST})
    public LcdpResourceDTO selectResourceByClassName(RestJsonWrapperBean var1);

    @RequestMapping(value={"/source-page-tmpl"}, method={RequestMethod.POST})
    public Long insertByPageTmpl(RestJsonWrapperBean var1);

    @RequestMapping(value={"/checkout-tree"}, method={RequestMethod.POST})
    public List<LcdpResourceTreeNodeDTO> selectCheckoutTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/code-hints-keywords"}, method={RequestMethod.GET})
    public Set<String> getCodeHintsKeyWords();

    @RequestMapping(value={"/search-id-by-path"}, method={RequestMethod.POST})
    public Long selectByPath(RestJsonWrapperBean var1);

    @RequestMapping(value={"/search-page-id-by-path"}, method={RequestMethod.POST})
    public Long selectPageIdByPath(RestJsonWrapperBean var1);

    @RequestMapping(value={"/opened-resource-tree/queries"}, method={RequestMethod.POST})
    public List<LcdpResourceTreeNodeDTO> selectOpenedResourceTree(RestJsonWrapperBean var1);
}
