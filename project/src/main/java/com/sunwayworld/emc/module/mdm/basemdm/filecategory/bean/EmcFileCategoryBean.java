package com.sunwayworld.emc.module.mdm.basemdm.filecategory.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@Table("T_EMC_FILE_CATEGORY")
public class EmcFileCategoryBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -3475763047998500501L;

    @Id
    private Long id;  // 主键
    private String fileCategoryNo;  // 文件类型编码
    private String fileCategoryName;  // 文件类型名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFileCategoryNo() {
        return fileCategoryNo;
    }

    public void setFileCategoryNo(String fileCategoryNo) {
        this.fileCategoryNo = fileCategoryNo;
    }

    public String getFileCategoryName() {
        return fileCategoryName;
    }

    public void setFileCategoryName(String fileCategoryName) {
        this.fileCategoryName = fileCategoryName;
    }
}
