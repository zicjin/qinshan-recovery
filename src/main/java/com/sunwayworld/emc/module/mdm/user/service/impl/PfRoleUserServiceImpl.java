package com.sunwayworld.emc.module.mdm.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.user.service.PfRoleUserService;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import com.sunwayworld.module.sys.role.service.impl.CoreRoleUserServiceImpl;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


@Repository
@GikamBean
public class PfRoleUserServiceImpl extends CoreRoleUserServiceImpl implements PfRoleUserService {

	  @Lazy
	  @Autowired
	  private CoreRoleService coreRoleService;
	  
	  @Lazy
	  @Autowired
	  private CoreUserService coreUserService;
	  
	  @Lazy
	  @Autowired
	  private CoreRoleUserService proxyInstance;
	  
	  
	  public Page<CoreRoleUserBean> selectPaginationByRole(Long roleId, RestJsonWrapperBean wrapper) {
		    MapperParameter parameter = wrapper.extractMapFilter();
	        CoreUserBean user = LocalContextHelper.getLoginUser();
	        //赋值所属单位和流程类型
	        String parentOrgId = user.getExt$Item("parentorgid");
		    parameter.setFilter(SearchFilter.instance().match("ROLEID", roleId).filter(MatchPattern.EQ)
		    		.match("ORGID", parentOrgId).filter(MatchPattern.SB));

		    PageRowBounds rowBounds = wrapper.extractPageRowBounds();
		    return selectPagination(parameter, rowBounds);
		  }
}