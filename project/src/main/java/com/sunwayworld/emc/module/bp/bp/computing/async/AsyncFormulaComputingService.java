package com.sunwayworld.emc.module.bp.bp.computing.async;

import java.util.List;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;

/**
 * @author zhengxy@sunwayworld.com 2022年03月25日
 */
public interface AsyncFormulaComputingService {

    void asyncComputing(String runId, List<Long> resultIdList, String userId);

    void asyncComputing(List<EmcRunBean> runList, List<Long> resultIdList);

    void asyncComputing(String runId, List<Long> resultIdList, String userId, boolean notification);

    void asyncComputing(List<EmcRunBean> runList, List<Long> resultIdList, boolean notification);
}
