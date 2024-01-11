package com.sunwayworld.emc.module.mdm.contract.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcMonitorItemService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

/**
 * @author zhengxy@sunwayworld.com 2020年05月20日
 */
@Repository
public class EmcContractStartProcessValidator implements DataValidator {

    @Autowired
    private EmcMonitorItemService monitorItemService;

    @Autowired
    private CoreFileService fileService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcContractBean> contractList = wrapper.parse(EmcContractBean.class);
        List<Long> contractIdList = contractList.stream().map(EmcContractBean::getId).collect(Collectors.toList());

        List<String> targetIdList = contractIdList.stream().map(contractId -> "T_EMC_CONTRACT$" + contractId).collect(Collectors.toList());
        List<CoreFileBean> fileList = fileService.getDao().selectListByOneColumnValues(targetIdList, "TARGETID", Arrays.asList("ID", "TARGETID"));
        List<String> fileTargetIdList = fileList.stream().map(CoreFileBean::getTargetId).collect(Collectors.toList());

        // 过滤出没有上传附件的项目
        List<Long> nonFileContractIdList = contractIdList.stream().filter(contractId -> !fileTargetIdList.contains("T_EMC_CONTRACT$" + contractId)).collect(Collectors.toList());
        if (!nonFileContractIdList.isEmpty()) {
            List<EmcMonitorItemBean> monitorItemList = monitorItemService.getDao().selectListByOneColumnValues(nonFileContractIdList, "CONTRACTID", Arrays.asList("ID", "CONTRACTID"));
            List<Long> existContractIdList = monitorItemList.stream().map(EmcMonitorItemBean::getContractId).distinct().collect(Collectors.toList());

            if (existContractIdList.size() != contractIdList.size()) {
                this.addConstraintViolation("EMC.MODULE.MDM.CONTRACTS.TIP.ADD_MONITOR_ITEM_FIRST");
                return false;
            }
        }

        return true;
    }
}
