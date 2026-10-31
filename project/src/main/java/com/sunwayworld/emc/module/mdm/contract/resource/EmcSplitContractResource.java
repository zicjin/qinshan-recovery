package com.sunwayworld.emc.module.mdm.contract.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcSplitContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcSplitContractService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author zhengxy@sunwayworld.com 2023年11月07日
 */
@RequestMapping("/secure/emc/module/mdm/contract/split-contracts")
public interface EmcSplitContractResource extends GenericResource<EmcSplitContractService, EmcSplitContractBean, Long>, GenericChoosableResource<EmcSplitContractService, EmcSplitContractBean, Long> {
}
