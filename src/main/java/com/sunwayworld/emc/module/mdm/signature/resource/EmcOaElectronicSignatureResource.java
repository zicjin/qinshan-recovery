package com.sunwayworld.emc.module.mdm.signature.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.signature.bean.EmcOaElectronicSignatureBean;
import com.sunwayworld.emc.module.mdm.signature.service.EmcOaElectronicSignatureService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
* @author zhengxy@sunwayworld.com 2020年12月30日
*/
@RequestMapping("/secure/emc/module/mdm/signatures")
public interface EmcOaElectronicSignatureResource extends GenericResource<EmcOaElectronicSignatureService, EmcOaElectronicSignatureBean, Long>, GenericAuditableResource<EmcOaElectronicSignatureService, EmcOaElectronicSignatureBean, Long> {

}
