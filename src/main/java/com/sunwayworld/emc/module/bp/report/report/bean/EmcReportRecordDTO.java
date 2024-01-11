package com.sunwayworld.emc.module.bp.report.report.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

import java.util.List;

public class EmcReportRecordDTO extends AbstractBaseData implements BaseData {

    @Transient
    private static final long serialVersionUID = 6789636441267755298L;
    private CoreFileBean reportFile;// 分析记录

    private List<CoreFileBean> otherRecordList;// 其他记录(采样记录、分析记录)

    public CoreFileBean getReportFile() {
        return reportFile;
    }

    public void setReportFile(CoreFileBean reportFile) {
        this.reportFile = reportFile;
    }

    public List<CoreFileBean> getOtherRecordList() {
        return otherRecordList;
    }

    public void setOtherRecordList(List<CoreFileBean> otherRecordList) {
        this.otherRecordList = otherRecordList;
    }
}
