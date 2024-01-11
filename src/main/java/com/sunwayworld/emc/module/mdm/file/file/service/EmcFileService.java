package com.sunwayworld.emc.module.mdm.file.file.service;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionDistBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
public interface EmcFileService extends GenericService<EmcFileBean, Long>,
                                        GenericChoosableService<EmcFileBean, Long> {
    Page<EmcFileVersionBean> selectFileVersionPaginationByFileId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcFileVersionDistBean> selectFileVersionDistPaginationByVersionId(Long versionId, RestJsonWrapperBean wrapper);

    Long insertFileVersion(Long id, RestJsonWrapperBean wrapper);

    void deleteFileVersion(Long id, RestJsonWrapperBean wrapper);

    Long insertFileVersionDist(Long versionId, RestJsonWrapperBean wrapper);

    void deleteFileVersionDist(Long versionId, RestJsonWrapperBean wrapper);
    
    String getDownloadUrl(Long id, RestJsonWrapperBean wrapper);
}
