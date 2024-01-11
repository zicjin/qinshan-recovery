package com.sunwayworld.emc.module.bp.bp.computing.async.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.sunwayworld.cloud.gikam.notification.event.CoreNotificationEvent;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.computing.async.AsyncFormulaComputingService;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2022年03月25日
 */
@Service
public class AsyncFormulaComputingServiceImpl implements AsyncFormulaComputingService {

    private static final Logger logger = LogManager.getLogger(AsyncFormulaComputingServiceImpl.class);

    private static final String RUN_FORMULA_COMPUTING_SUCCESS = "runFormulaComputingSuccess";
    private static final String RUN_FORMULA_COMPUTING_FAILURE = "runFormulaComputingFailure";

    private static final String ACQ_FORMULA_COMPUTING_SUCCESS = "acqFormulaComputingSuccess";
    private static final String ACQ_FORMULA_COMPUTING_FAILURE = "acqFormulaComputingFailure";

    @Override
    public void asyncComputing(String runId, List<Long> resultIdList, String userId) {
        ExecutorHelper.submit(() -> {
            boolean success = true;
            long startTime = System.currentTimeMillis();

            Map<String, Object> paramMap = new HashMap<>(4);
            paramMap.put("runId", runId);

            try {
                EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
            } catch (Exception e) {
                logger.error("用户【{}】下的批次【{}】计算失败，错误原因【{}】", userId, runId, e.getMessage());
                success = false;
                paramMap.put("errorMsg", e.getMessage());
            }

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1_000.0;

            paramMap.put("duration", duration);

            // 发送通知
            CoreNotificationEvent completeEvent = new CoreNotificationEvent(success ? RUN_FORMULA_COMPUTING_SUCCESS : RUN_FORMULA_COMPUTING_FAILURE,
                    Collections.singletonList(userId), null, LocalDateTime.now(), paramMap);
            ApplicationContextHelper.getApplicationContext().publishEvent(completeEvent);
        });
    }

    @Override
    public void asyncComputing(List<EmcRunBean> runList, List<Long> resultIdList) {
        ExecutorHelper.submit(() -> {
            boolean success = true;
            long startTime = System.currentTimeMillis();

            Map<String, Object> paramMap = new HashMap<>(4);
            try {
                EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
            } catch (Exception e) {
                String joinedRunId = runList.stream().map(EmcRunBean::getId).collect(Collectors.joining(","));
                String joinedUserId = runList.stream().map(EmcRunBean::getAnalysisUserId).distinct().collect(Collectors.joining(","));
                logger.error("用户【{}】下的批次【{}】计算失败，错误原因【{}】", joinedUserId, joinedRunId, e.getMessage());
                success = false;
                paramMap.put("errorMsg", e.getMessage());
            }

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1_000.0;

            paramMap.put("duration", duration);

            Map<String, List<EmcRunBean>> runMap = runList.stream().filter(run -> !StringUtils.isEmpty(run.getAnalysisUserId())).collect(Collectors.groupingBy(EmcRunBean::getAnalysisUserId));
            for (Map.Entry<String, List<EmcRunBean>> entry : runMap.entrySet()) {
                String userId = entry.getKey();
                String joinedRunId = entry.getValue().stream().map(EmcRunBean::getId).collect(Collectors.joining(","));
                paramMap.put("runId", joinedRunId);

                CoreNotificationEvent completeEvent = new CoreNotificationEvent(success ? ACQ_FORMULA_COMPUTING_SUCCESS : ACQ_FORMULA_COMPUTING_FAILURE,
                        Collections.singletonList(userId), null, LocalDateTime.now(), paramMap);
                ApplicationContextHelper.getApplicationContext().publishEvent(completeEvent);
            }
        });
    }

    @Override
    public void asyncComputing(String runId, List<Long> resultIdList, String userId, boolean notification) {
        if (notification) {
            asyncComputing(runId, resultIdList, userId);
            return;
        }

        ExecutorHelper.submit(() -> EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute());
    }

    @Override
    public void asyncComputing(List<EmcRunBean> runList, List<Long> resultIdList, boolean notification) {
        if (notification) {
            asyncComputing(runList, resultIdList);
            return;
        }

        ExecutorHelper.submit(() -> EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute());
    }

}
