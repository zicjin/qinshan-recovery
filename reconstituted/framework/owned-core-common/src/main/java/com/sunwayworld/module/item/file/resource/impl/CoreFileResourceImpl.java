/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileCkeditorUploadResultDTO;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import com.sunwayworld.module.item.file.bean.CoreFileOfficeResultDTO;
import com.sunwayworld.module.item.file.resource.CoreFileResource;
import com.sunwayworld.module.item.file.service.CoreFileImportService;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.validator.CoreFileCKEditorUploadValidator;
import com.sunwayworld.module.item.file.validator.CoreFileUploadValidator;
import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@LogModule(value="\u6587\u4ef6")
@RestController
@GikamBean
public class CoreFileResourceImpl
implements CoreFileResource {
    @Autowired
    private CoreFileService fileService;
    @Autowired
    private CoreFileLogService fileLogService;
    @Autowired
    private CoreFileImportService fileImportService;

    @Override
    public CoreFileService getService() {
        return this.fileService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Map.class, new PropertyEditorSupport(){

            @Override
            public String getAsText() {
                return this.getValue().toString();
            }

            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                HashMap<String, String> map = new HashMap<String, String>();
                if (!StringUtils.isEmpty(text)) {
                    String[] mapStrArray;
                    for (String mapStr : mapStrArray = text.replace("{", "").replace("}", "").split(",")) {
                        String[] entityStr = mapStr.split(":");
                        map.put(entityStr[0].trim(), entityStr[1].trim());
                    }
                }
                this.setValue(map);
            }
        });
    }

    @Override
    @RequestMapping(method={RequestMethod.TRACE})
    public Long insert(RestJsonWrapperBean wrapper) {
        throw new UnsupportedException();
    }

    @Override
    @Log(value="\u4e0a\u4f20", type=LogType.INSERT)
    @ValidateDataWith(value=CoreFileUploadValidator.class)
    public Long upload(CoreFileBean coreFile, MultipartFile file) {
        return this.getService().upload(coreFile, file);
    }

    @Override
    @Log(value="\u67e5\u8be2", type=LogType.SELECT)
    public Page<CoreFileBean> selectPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectSearchablePagination(wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58", type=LogType.UPDATE)
    @ResponseStatus(value=HttpStatus.OK)
    public void save(RestJsonWrapperBean jsonWrapper) {
        this.getService().save(jsonWrapper);
    }

    @Override
    @Log(value="\u5220\u9664", type=LogType.DELETE)
    @RequestMapping(method={RequestMethod.DELETE})
    @ResponseStatus(value=HttpStatus.OK)
    public void delete(RestJsonWrapperBean jsonWrapper) {
        this.getService().delete(jsonWrapper);
    }

    @Override
    @ResponseStatus(value=HttpStatus.OK)
    @Log(value="\u6253\u5305\u6307\u5b9a\u8868\u4e0b\u8868\u5355\u7684\u9644\u4ef6\uff0c\u5e76\u8fd4\u56de\u4e0b\u8f7d\u7684\u8def\u5f84", type=LogType.EXPORT)
    public String pack(@PathVariable String table, @PathVariable String itemId, @PathVariable String func) {
        return this.getService().pack(table, itemId, func);
    }

    @Override
    @ResponseStatus(value=HttpStatus.OK)
    @Log(value="\u6253\u5305\u6307\u5b9a\u8868\u7684\u4e00\u4e2a\u6216\u591a\u4e2a\u8868\u5355\u7684\u9644\u4ef6\uff0c\u5e76\u8fd4\u56de\u4e0b\u8f7d\u7684\u8def\u5f84", type=LogType.EXPORT)
    public String pack(@PathVariable String table, RestJsonWrapperBean jsonWrapper) {
        return this.getService().pack(table, jsonWrapper);
    }

    @Override
    @ResponseStatus(value=HttpStatus.OK)
    @Log(value="\u6253\u5305\u6307\u5b9a\u7684\u4e00\u4e2a\u6216\u591a\u4e2a\u9644\u4ef6\uff0c\u5e76\u8fd4\u56de\u4e0b\u8f7d\u7684\u8def\u5f84", type=LogType.EXPORT)
    public String pack(RestJsonWrapperBean jsonWrapper) {
        return this.getService().pack(jsonWrapper);
    }

    @Override
    @Log(value="CKEDITOR\u4e0a\u4f20\u56fe\u7247", type=LogType.INSERT)
    @ValidateDataWith(value=CoreFileCKEditorUploadValidator.class)
    public CoreFileCkeditorUploadResultDTO ckeditorUpload(CoreFileBean coreFile, MultipartFile file) {
        return this.getService().ckeditorUpload(coreFile, file);
    }

    @Override
    @Log(value="\u901a\u8fc7\u9644\u4ef6\u7f16\u7801\u5bfc\u5165\u6570\u636e", type=LogType.IMPORT)
    public CoreFileImportResultDTO importData(@PathVariable Long id, @PathVariable String service) {
        return this.fileImportService.importData(id, service);
    }

    @Override
    @Log(value="\u901a\u8fc7\u9644\u4ef6\u6a21\u677f\u7f16\u7801\u4e0b\u8f7d\u6a21\u677f", type=LogType.EXPORT)
    public <ID extends Serializable> String getTemplateUrl(@RequestParam String templateCode, @RequestParam String service, @RequestParam(required=false) ID id) {
        return GenericTemplateService.getInstance(service).getDownloadUrl(templateCode, id);
    }

    @Override
    @Log(value="HTML\u5185\u5bb9\u8f6c\u4e3aPDF", type=LogType.UPDATE)
    public String convertHtmlToPdf(RestJsonWrapperBean wrapper) {
        return this.getService().convertHtmlToPdf(wrapper);
    }

    @Override
    @Log(value="HTML\u5185\u5bb9\u8f6c\u4e3aWORD", type=LogType.UPDATE)
    public String convertHtmlToWord(RestJsonWrapperBean wrapper) {
        return this.getService().convertHtmlToWord(wrapper);
    }

    @Override
    @Log(value="SunwayOffice\u64cd\u4f5c", type=LogType.UPDATE)
    public CoreFileOfficeResultDTO sunwayOfficeInvoke(@PathVariable Long id) {
        return this.getService().sunwayOfficeInvoke(id);
    }

    @Override
    @Log(value="SunwayOffice\u9884\u89c8\u975e\u6838\u5fc3\u9644\u4ef6\u8868\u6302\u8f7d\u9644\u4ef6", type=LogType.SERVICE)
    public CoreFileOfficeResultDTO sunwayOfficeExternalInvoke() {
        return this.getService().sunwayOfficeExternalInvoke();
    }

    @Override
    @Log(value="\u8bb0\u5f55\u6587\u4ef6\u64cd\u4f5c", type=LogType.EXPORT)
    public Long log(@PathVariable Long id, @PathVariable String operationCategory) {
        return this.fileLogService.insert(id, operationCategory);
    }

    @Override
    @Log(value="\u8bb0\u5f55\u6587\u4ef6\u64cd\u4f5c\u67e5\u8be2\u9875\u67e5\u8be2", type=LogType.SELECT)
    public Page<CoreFileLogBean> selectLogSearchablePagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectLogSearchablePagination(id, wrapper);
    }

    @Override
    @Deprecated
    @Log(value="\u901a\u8fc7\u9644\u4ef6\u6a21\u677f\u7f16\u7801\u4e0b\u8f7d\u6a21\u677f\uff08\u8001\u65b9\u6cd5\uff0c\u5efa\u8bae\u66ff\u6362\uff09", type=LogType.EXPORT)
    public <ID extends Serializable> String getDownloadTemplateUrl(@PathVariable String templateCode, @PathVariable String service, @PathVariable(required=false) ID id) {
        return this.getTemplateUrl(templateCode, service, id);
    }

    @Override
    @Deprecated
    @Log(value="\u8bb0\u5f55\u6587\u4ef6\u64cd\u4f5c\uff08\u8001\u65b9\u6cd5\uff0c\u5efa\u8bae\u66ff\u6362\uff09", type=LogType.EXPORT)
    public Long oldLog(@PathVariable Long id, @PathVariable String operationCategory) {
        return this.fileLogService.insert(id, operationCategory);
    }

    @Override
    @Deprecated
    @Log(value="\u8bb0\u5f55\u6587\u4ef6\u64cd\u4f5c\uff08\u8001\u65b9\u6cd5\uff0c\u5efa\u8bae\u66ff\u6362\uff09", type=LogType.EXPORT)
    public CoreFileOfficeResultDTO oldSunwayOfficeInvoke(@PathVariable Long id) {
        return this.getService().sunwayOfficeInvoke(id);
    }
}
