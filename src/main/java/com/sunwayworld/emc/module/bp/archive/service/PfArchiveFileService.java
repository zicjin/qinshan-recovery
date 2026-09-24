package com.sunwayworld.emc.module.bp.archive.service;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;

import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2022年04月06日
 */
public interface PfArchiveFileService {

    void archiveWeighingRecordFiles(EmcProjectBean project);

    void archiveTaskRecordFiles(EmcProjectBean project);

    void archiveSamplingContactRecordFiles(EmcProjectBean project);

    void archiveDeliveryRecordFiles(EmcProjectBean project);

    void archive(List<EmcArchiveCategoryBean> archiveCategoryList, EmcProjectBean project, Boolean merge, Boolean withCoreFile);
}
