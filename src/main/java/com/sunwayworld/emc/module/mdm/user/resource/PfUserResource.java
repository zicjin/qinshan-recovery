package com.sunwayworld.emc.module.mdm.user.resource;


/**
 * @author licm@sunwayworld.com 2021年09月10日
 */
import org.springframework.web.bind.annotation.RequestMapping;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.mdm.user.resource.CoreUserResource;


@RequestMapping("/secure/core/module/mdm/users")
public interface PfUserResource extends CoreUserResource {
    String selectMaxId(RestJsonWrapperBean wrapper);  

    void insertReportConfigByDept(RestJsonWrapperBean wrapper);
    
    void deleteReportConfigByDept(RestJsonWrapperBean wrapper);  
    
}