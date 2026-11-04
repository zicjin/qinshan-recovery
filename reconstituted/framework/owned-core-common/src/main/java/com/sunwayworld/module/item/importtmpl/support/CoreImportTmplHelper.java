/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.support;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplBean;
import com.sunwayworld.module.item.importtmpl.service.CoreImportTmplService;
import java.nio.file.Path;
import java.util.Map;

public class CoreImportTmplHelper {
    public static CoreFileBean getFileByTmplCode(String tmplCode) {
        CoreImportTmplService importTmplService = SpringUtils.getBean(CoreImportTmplService.class);
        CoreImportTmplBean importTmpl = (CoreImportTmplBean)importTmplService.selectFirstByFilter(SearchFilter.instance().match("TMPLCODE", tmplCode).filter(MatchPattern.EQ), new Order[0]);
        if (ObjectUtils.isEmpty(importTmpl)) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.IMPORTTMPLS.TIP.CODE_NOT_EXISTED", new String[0]));
        }
        CoreFileService coreFileService = SpringUtils.getBean(CoreFileService.class);
        CoreFileBean coreFile = (CoreFileBean)coreFileService.selectFirstByFilter(SearchFilter.instance().match("TARGETID", "T_CORE_IMPORT_TMPL$" + importTmpl.getId()).filter(MatchPattern.EQ), new Order[0]);
        if (ObjectUtils.isEmpty(importTmpl)) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.IMPORTTMPLS.TIP.NO_FILE", new String[0]));
        }
        return coreFile;
    }

    public static String exportByCodeAndReturnDownloadUrl(String tmplCode, Map<String, Object> contentMap) {
        CoreFileBean coreFile = CoreImportTmplHelper.getFileByTmplCode(tmplCode);
        return ExcelHelper.writeDataAndGetDownLoadPath(coreFile, contentMap);
    }

    public static Path exportByCodeAndReturnLocalPath(String tmplCode, Map<String, Object> contentMap) {
        CoreFileBean coreFile = CoreImportTmplHelper.getFileByTmplCode(tmplCode);
        return ExcelHelper.writeDataAndGetLocalPath(coreFile, contentMap);
    }
}
