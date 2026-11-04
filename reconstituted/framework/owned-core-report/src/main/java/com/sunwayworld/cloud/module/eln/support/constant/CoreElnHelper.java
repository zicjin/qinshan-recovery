/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.support.constant;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.time.LocalDateTime;

public abstract class CoreElnHelper {
    public static CoreFileBean getInsertElnTmplVersionContentFile(CoreElnTmplVersionBean elnTmplVersion) {
        CoreFileBean file = new CoreFileBean();
        file.setId(ApplicationContextHelper.getNextIdentity());
        file.setTargetId("T_CORE_ELN_TMPL_VERSION$CONTENT$" + file.getId());
        file.setName(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmmss") + ".teln");
        file.setMd5Name(EncryptUtils.MD5Encrypt(file.getTargetId()));
        file.setTimes(0L);
        file.setFileExt("teln");
        file.setScope(FileScope.secure.name());
        file.setPermanent("1");
        file.setDownloadable("1");
        file.setCreatedTime(LocalDateTime.now());
        return file;
    }

    public static CoreFileBean getInsertElnContentFile(Long elnId) {
        CoreFileBean file = new CoreFileBean();
        file.setId(ApplicationContextHelper.getNextIdentity());
        file.setTargetId("T_CORE_ELN$INSTANT$" + elnId + "$" + file.getId());
        file.setName(DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmmss") + ".ieln");
        file.setMd5Name(EncryptUtils.MD5Encrypt(file.getTargetId()));
        file.setTimes(0L);
        file.setFileExt("ieln");
        file.setScope(FileScope.secure.name());
        file.setPermanent("1");
        file.setDownloadable("1");
        file.setCreatedTime(LocalDateTime.now());
        return file;
    }
}
