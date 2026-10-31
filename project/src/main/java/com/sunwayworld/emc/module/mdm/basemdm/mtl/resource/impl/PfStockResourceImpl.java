package com.sunwayworld.emc.module.mdm.basemdm.mtl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.PfMtlCheckDTO;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.resource.PfStockResource;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.PfStockService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@RestController
@GikamBean
public class PfStockResourceImpl implements PfStockResource {
    @Autowired
    private PfStockService stockService;

    @Override
    public PfStockService getService() {
        return stockService;
    }

    @Log(value = "标准物质核查计划查询", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-mtl-check", method = RequestMethod.POST)
    public Page<PfMtlCheckDTO> selectMtlCheck(RestJsonWrapperBean wrapper) {
        return stockService.selectMtlCheck(wrapper);
    }

    @Override
    @Log(value = "复制库存", type = LogType.SELECT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        getService().copy(wrapper);
    }
}
