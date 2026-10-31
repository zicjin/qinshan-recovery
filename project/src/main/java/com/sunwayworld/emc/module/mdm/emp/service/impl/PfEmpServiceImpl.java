package com.sunwayworld.emc.module.mdm.emp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.emp.service.PfEmpService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpDao;
import com.sunwayworld.emc.module.mdm.emp.emp.service.impl.EmcEmpServiceImpl;


@Repository
@GikamBean
public class PfEmpServiceImpl extends EmcEmpServiceImpl implements PfEmpService {

    @Autowired
    private EmcEmpDao emcEmpDao;



    @SuppressWarnings("unchecked")
    @Override
    public EmcEmpDao  getDao() {
        return emcEmpDao;
    }
    
    @Override
    public String insert(RestJsonWrapperBean wrapper) {
        List<EmcEmpBean> empList = wrapper.parse(EmcEmpBean.class);
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        empList.forEach(e -> e.setExt$Item("orgid", parentOrgId));
        getDao().insert(empList);

        return empList.isEmpty() ? null : empList.get(0).getId();
    }
    
}