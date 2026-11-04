/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceDetailedCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceGroupingCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnInstanceServiceImpl
implements CoreBpmnInstanceService {
    @Autowired
    private CoreBpmnInstanceDao instanceDao;

    @Override
    public CoreBpmnInstanceDao getDao() {
        return this.instanceDao;
    }

    @Override
    public List<CoreBpmnInstanceCandidatorDTO> selectInstanceCandidatorList(List<Long> idList) {
        return this.getDao().selectInstanceCandidatorList(idList);
    }

    @Override
    public List<CoreBpmnInstanceTaskCandidatorDTO> selectInstanceTaskAuditorList(List<Long> idList) {
        return this.getDao().selectInstanceTaskAuditorList(idList);
    }

    @Override
    public List<CoreBpmnInstanceGroupingCandidatorDTO> selectInstanceGroupingCandidatorList(Long id) {
        List<CoreBpmnInstanceDetailedCandidatorDTO> detailedCandidatorList = this.getDao().selectInstanceDetailedCandidatorList(Arrays.asList(id));
        if (CollectionUtils.isEmpty(detailedCandidatorList)) {
            return CollectionUtils.emptyList();
        }
        ArrayList<CoreBpmnInstanceGroupingCandidatorDTO> groupingCandidatorList = new ArrayList<CoreBpmnInstanceGroupingCandidatorDTO>();
        for (CoreBpmnInstanceDetailedCandidatorDTO detailedCandidator : detailedCandidatorList) {
            CoreBpmnInstanceGroupingCandidatorDTO groupingCandidator = groupingCandidatorList.stream().filter(p -> p.getTaskId().equals(detailedCandidator.getTaskId())).findAny().orElse(null);
            if (groupingCandidator == null) {
                groupingCandidator = new CoreBpmnInstanceGroupingCandidatorDTO();
                groupingCandidator.setTaskId(detailedCandidator.getTaskId());
                groupingCandidator.setTaskName(detailedCandidator.getTaskName());
                groupingCandidator.setCandidator(this.gatCandidator(detailedCandidator));
                groupingCandidatorList.add(groupingCandidator);
                continue;
            }
            groupingCandidator.setCandidator(groupingCandidator.getCandidator() + "," + this.gatCandidator(detailedCandidator));
        }
        return groupingCandidatorList;
    }

    private String gatCandidator(CoreBpmnInstanceDetailedCandidatorDTO detailedCandidator) {
        return detailedCandidator.getCandidatorName() + "(" + detailedCandidator.getCandidatorId() + ")";
    }
}
