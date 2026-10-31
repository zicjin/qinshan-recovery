/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.projectfunction.module.mdm.baasemdm.equiptrequisition.service.impl;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.impl.EmcEquiptRequisitionServiceImpl;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.projectfunction.module.mdm.baasemdm.equiptrequisition.service.PfEquiptRequisitionService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class PfEquiptRequisitionServiceImpl
extends EmcEquiptRequisitionServiceImpl
implements PfEquiptRequisitionService {
    @Autowired
    private EmcEquiptService emcEquiptService;

    @Override
    public void deleteInstrumentOut(RestJsonWrapperBean wrapper) {
        List<EmcEquiptRequisitionBean> equiptRequisition = wrapper.parse(EmcEquiptRequisitionBean.class);
        List<EmcEquiptRequisitionBean> updateEquiptRequisition = new ArrayList<>();
        List equiptIdList = equiptRequisition.stream().map(f -> f.getEquiptId()).collect(Collectors.toList());
        List<Long> deleteIds = new ArrayList<>();
        equiptRequisition.forEach(f -> {
            String projIds = wrapper.getParamValue("projIds");
            String projNos = wrapper.getParamValue("projNos");
            String fProjIds = f.getProjIds();
            String fProjNos = f.getProjNos();
            if (fProjIds.indexOf(",") < 0) {
                deleteIds.add(f.getId());
            } else {
                String[] projIdsSplit = projIds.split(",");
                String[] projNosSplit = projNos.split(",");
                for (int i = 0; i < projIdsSplit.length; ++i) {
                    if (fProjIds.indexOf(projIdsSplit[i]) == 0) {
                        fProjIds = fProjIds.replace(projIdsSplit[i] + ",", "");
                        fProjNos = fProjNos.replace(projNosSplit[i] + ",", "");
                        continue;
                    }
                    if (fProjIds.indexOf(projIdsSplit[i]) > 0) {
                        fProjIds = fProjIds.replace("," + projIdsSplit[i], "");
                        fProjNos = fProjNos.replace("," + projNosSplit[i], "");
                    }
                }
                f.setProjIds(fProjIds);
                f.setProjNos(fProjNos);
                updateEquiptRequisition.add(f);
            }
        });
        this.updateIfChanged(updateEquiptRequisition);
        this.getDao().deleteByIdList(deleteIds);
    }
}
