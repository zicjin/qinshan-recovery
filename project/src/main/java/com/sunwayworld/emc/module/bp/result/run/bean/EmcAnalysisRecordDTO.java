package com.sunwayworld.emc.module.bp.result.run.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

import java.util.List;

public class EmcAnalysisRecordDTO extends AbstractBaseData implements BaseData {

    @Transient
    private static final long serialVersionUID = 6789636461267755298L;
    private CoreFileBean analysisRecord;// 分析记录

    private List<CoreFileBean> otherRecordList;// 其他记录

    public CoreFileBean getAnalysisRecord() {
        return analysisRecord;
    }

    public void setAnalysisRecord(CoreFileBean analysisRecord) {
        this.analysisRecord = analysisRecord;
    }

    public List<CoreFileBean> getOtherRecordList() {
        return otherRecordList;
    }

    public void setOtherRecordList(List<CoreFileBean> otherRecordList) {
        this.otherRecordList = otherRecordList;
    }
}
