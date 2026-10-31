package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2022年10月19日
 */
@Repository
public class PfSubpackageTaskValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);

        List<EmcOrderTaskBean> filterTaskList = new ArrayList<>();

        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        if (folderList.size() == 1) {
            if (orderList.size() > 1) {
                List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
                orderIdList.forEach(orderId -> testMethodIdList.forEach(testMethodId -> {
                    EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                    filterTask.setOrderId(orderId);
                    filterTask.setTestMethodId(testMethodId);
                    filterTaskList.add(filterTask);
                }));

                orderTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("orderId", "testMethodId"), Arrays.asList("id", "runId"));
            }
        } else {
            List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
            folderIdList.forEach(folderId -> testMethodIdList.forEach(testMethodId -> {
                EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
                filterTask.setFolderId(folderId);
                filterTask.setTestMethodId(testMethodId);
                filterTaskList.add(filterTask);
            }));

            orderTaskList = orderTaskService.getDao().selectList(filterTaskList, Arrays.asList("folderId", "testMethodId"), Arrays.asList("id", "runId"));
        }

        boolean hasCreatedRun = orderTaskList.stream().anyMatch(task -> !StringUtils.isEmpty(task.getRunId()));
        if (hasCreatedRun) {
            this.addConstraintViolation("所选的测试已创建了分析批，不允许分包");
            return false;
        }

        return true;
    }
}
