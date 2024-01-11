package com.sunwayworld.emc.module.mdm.contract.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcMonitorItemService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * 合同登记添加检测项目校验
 * @author zhengxy@sunwayworld.com 2020年07月21日
 */
@Repository
public class EmcContractAddTestItemValidator implements DataValidator {

    @Autowired
    private EmcMonitorItemService monitorItemService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcMonitorItemBean> newMonitorItemList = wrapper.parse(EmcMonitorItemBean.class);

        List<EmcMonitorItemBean> filterMonitorItemList = new ArrayList<>();
        newMonitorItemList.forEach(monitorItem -> {
            EmcMonitorItemBean filterMonitorItem = new EmcMonitorItemBean();
            filterMonitorItem.setContractId(monitorItem.getContractId());
            filterMonitorItem.setMonitorTmplId(monitorItem.getMonitorTmplId());
            filterMonitorItem.setExt$Item("itemgroupid", monitorItem.getExt$Item("itemgroupid"));
            filterMonitorItemList.add(filterMonitorItem);
        });

        List<EmcMonitorItemBean> monitorItemList = monitorItemService.getDao().selectList(filterMonitorItemList, Arrays.asList("CONTRACTID", "MONITORTMPLID", "ITEMGROUPID"), Collections.singletonList("ID"));
        if (!monitorItemList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.CONTRACTS.TIP.REPEATABLE_MONITOR_ITEM");
            return false;
        }

        return true;
    }
}
