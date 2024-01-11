package com.sunwayworld.emc.module.bp.archive.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 归档文件明细表
 * @author zhengxy@sunwayworld.com 2020年10月31日
 */
@Table("T_EMC_ARCHIVE_LINE")
public class EmcArchiveLineBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -7024579853440416124L;

    @Id
    private Long id;  // 主键
    private Long archiveCategoryId;  // 归档类型ID
    private Long projectId;  // 项目ID

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getArchiveCategoryId() {
        return archiveCategoryId;
    }

    public void setArchiveCategoryId(Long archiveCategoryId) {
        this.archiveCategoryId = archiveCategoryId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

}
