package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 生物查表明细
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Table("T_EMC_FETCH_DATA_LINE")
public class EmcFetchDataLineBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8760015868666258503L;

    @Id
    private Long id;  // 主键
    private Long fetchDataId;  // 生物查表ID
    private String fieldOne;  // 字段1
    private String fieldTwo;  // 字段2
    private String fieldThree;  // 字段3
    private String content;  // 内容

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFetchDataId() {
        return fetchDataId;
    }

    public void setFetchDataId(Long fetchDataId) {
        this.fetchDataId = fetchDataId;
    }

    public String getFieldOne() {
        return fieldOne;
    }

    public void setFieldOne(String fieldOne) {
        this.fieldOne = fieldOne;
    }

    public String getFieldTwo() {
        return fieldTwo;
    }

    public void setFieldTwo(String fieldTwo) {
        this.fieldTwo = fieldTwo;
    }

    public String getFieldThree() {
        return fieldThree;
    }

    public void setFieldThree(String fieldThree) {
        this.fieldThree = fieldThree;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
