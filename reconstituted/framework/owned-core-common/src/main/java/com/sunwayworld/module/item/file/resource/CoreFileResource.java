/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileCkeditorUploadResultDTO;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import com.sunwayworld.module.item.file.bean.CoreFileOfficeResultDTO;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.Serializable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value={"/core/module/item/files"})
public interface CoreFileResource
extends GenericResource<CoreFileService, CoreFileBean, Long> {
    @RequestMapping(method={RequestMethod.POST})
    public Long upload(CoreFileBean var1, MultipartFile var2);

    @Override
    @RequestMapping(method={RequestMethod.DELETE})
    public void delete(RestJsonWrapperBean var1);

    @RequestMapping(method={RequestMethod.PUT})
    public void save(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/pack/{table}/{itemId}", "/action/pack/{table}/{itemId}/add-on-func/{func}"}, method={RequestMethod.POST})
    public String pack(@PathVariable String var1, @PathVariable String var2, @PathVariable(required=false) String var3);

    @RequestMapping(value={"/action/pack/{table}"}, method={RequestMethod.POST})
    public String pack(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/pack"}, method={RequestMethod.POST})
    public String pack(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/ckeditor-upload"}, method={RequestMethod.POST})
    public CoreFileCkeditorUploadResultDTO ckeditorUpload(CoreFileBean var1, MultipartFile var2);

    @RequestMapping(value={"/{id}/action/import/{service}"}, method={RequestMethod.POST})
    public CoreFileImportResultDTO importData(@PathVariable Long var1, @PathVariable String var2);

    @RequestMapping(value={"/template-urls"}, method={RequestMethod.GET})
    public <ID extends Serializable> String getTemplateUrl(@RequestParam String var1, @RequestParam String var2, @RequestParam(required=false) ID var3);

    @RequestMapping(value={"/{id}/logs"}, method={RequestMethod.POST})
    public Long log(@PathVariable Long var1, @RequestParam String var2);

    @RequestMapping(value={"/{id}/logs/queries/searchable"}, method={RequestMethod.POST})
    public Page<CoreFileLogBean> selectLogSearchablePagination(Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/convert-html-to-pdf"}, method={RequestMethod.POST})
    public String convertHtmlToPdf(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/convert-html-to-word"}, method={RequestMethod.POST})
    public String convertHtmlToWord(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/action/sunwayoffice-invoke"}, method={RequestMethod.POST})
    public CoreFileOfficeResultDTO sunwayOfficeInvoke(@PathVariable Long var1);

    @RequestMapping(value={"/action/sunwayoffice-external-invoke"}, method={RequestMethod.POST})
    public CoreFileOfficeResultDTO sunwayOfficeExternalInvoke();

    @Deprecated
    @RequestMapping(value={"/{templateCode}/action/download-template/{service}", "/{templateCode}/action/download-template/{service}/{id}"}, method={RequestMethod.GET})
    public <ID extends Serializable> String getDownloadTemplateUrl(@PathVariable String var1, @PathVariable String var2, @PathVariable(required=false) ID var3);

    @Deprecated
    @RequestMapping(value={"/{id}/logs/action/{operationCategory}"}, method={RequestMethod.POST})
    public Long oldLog(@PathVariable Long var1, @PathVariable String var2);

    @Deprecated
    @RequestMapping(value={"/sunwayoffices/{id}/action/invoke"}, method={RequestMethod.POST})
    public CoreFileOfficeResultDTO oldSunwayOfficeInvoke(@PathVariable Long var1);
}
