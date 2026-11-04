/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.listener;

import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.constant.CoreFileOperation;
import com.sunwayworld.module.item.file.event.CoreFileEvent;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LcdpFileEventApplicationListener
implements ApplicationListener<CoreFileEvent> {
    @Autowired
    private CoreTableService coreTableService;

    @Override
    public void onApplicationEvent(CoreFileEvent event) {
        String tableName;
        CoreTableBean coreTableBean;
        CoreFileBean coreFile;
        if (CoreFileOperation.INSERT.equals(event.getSource()) && (coreFile = event.getCoreFile()).getTargetId() != null && coreFile.getTargetId().contains("$") && !ObjectUtils.isEmpty(coreTableBean = this.coreTableService.selectTable(tableName = this.parseTableName(coreFile.getTargetId()))) && StringUtils.equals(coreTableBean.getAttachment(), "0")) {
            coreTableBean.setAttachment("1");
            this.coreTableService.updateIfChanged(coreTableBean);
        }
    }

    private String parseTableName(String targetId) {
        return targetId.substring(0, targetId.indexOf("$"));
    }
}
