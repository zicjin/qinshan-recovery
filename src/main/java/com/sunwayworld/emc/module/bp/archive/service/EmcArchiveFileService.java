package com.sunwayworld.emc.module.bp.archive.service;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2020年10月31日
 */
public interface EmcArchiveFileService {

    String archive(RestJsonWrapperBean wrapper);

    void archiveWordReportFiles(EmcProjectBean project);

    void archiveReportAuditRecordFiles(EmcProjectBean project);

    void archiveContractReviewFiles(EmcProjectBean project);

    void archiveContractFiles(EmcProjectBean project);

    void archiveDetectSchemeFiles(EmcProjectBean project);

    void archiveCalibrationRecordFiles(EmcProjectBean project);

    void archiveSamplingRecordFiles(EmcProjectBean project);

    void archiveSamplingVideoFiles(EmcProjectBean project);

    void archiveReceiveRecordFiles(EmcProjectBean project);

    void archiveAnalysisRecordFiles(EmcProjectBean project);

    void archiveOtherFiles(EmcProjectBean project);
}
