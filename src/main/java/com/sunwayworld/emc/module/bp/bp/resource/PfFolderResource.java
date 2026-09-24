package com.sunwayworld.emc.module.bp.bp.resource;



import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author licm@sunwayworld.com
 * @date 2021/11/04 
 */
@RequestMapping("/secure/emc/module/bp/bp/folders")
public interface PfFolderResource extends EmcFolderResource {
    String submitRemind (RestJsonWrapperBean wrapper);

    Long submitProject (RestJsonWrapperBean wrapper);
}
