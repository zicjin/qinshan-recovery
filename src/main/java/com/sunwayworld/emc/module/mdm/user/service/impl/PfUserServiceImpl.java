package com.sunwayworld.emc.module.mdm.user.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.user.service.PfUserService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.dao.CoreUserDao;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.mdm.user.service.impl.CoreUserServiceImpl;
import com.sunwayworld.module.sys.password.bean.CorePasswordHisBean;
import com.sunwayworld.module.sys.password.service.CorePasswordHisService;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;

/**
 * @author licm@sunwayworld.com 2021年09月10日
 */
@Repository
@GikamBean
public class PfUserServiceImpl extends CoreUserServiceImpl implements PfUserService {
    @Autowired
    private CoreUserDao coreUserDao;
	
    @Autowired
    @Lazy
    private CoreUserService proxyInstance;
    
    @Autowired
    @Lazy
    private CorePasswordPolicyService passwordPolicyService;
    @Autowired
    @Lazy
    private CorePasswordHisService passwordHisService;
    @Autowired
    @Lazy
    private CoreFileService coreFileService;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
 
    @Autowired
    @Lazy
    private CoreUserService userService;
    
    @Autowired
    @Lazy
    CoreUserPermissionService userPermissionService;
    

    @Override
    public String selectMaxId(RestJsonWrapperBean wrapper) {
        List<CoreUserBean> userList = coreUserDao.selectAll();
        //查管理员所属单位最大id,志科的新增按志科的最大id来、其他公司按其他公司id来
        CoreUserBean orgUser = LocalContextHelper.getLoginUser();
        String filterOrgId = orgUser.getExt$Item("parentorgid");
        // 排除非数字的用户
        List<CoreUserBean> validUserList =  userList.stream().filter(user ->StringUtils.contains(user.getOrgId(), filterOrgId) &&NumberUtils.isNumber(user.getId())).collect(Collectors.toList());
        validUserList.sort(Comparator.comparing(user -> NumberUtils.parseLong(((CoreUserBean) user).getId())).reversed());
        //倒序排列
        String orgId="";
        if(validUserList.size() == 0){
            orgId = "0";
        }else{
            orgId = validUserList.get(0).getId();
        }
        long beforeId = Long.parseLong(orgId)+1;
        String resultId0 = String.valueOf(beforeId);
        String resultId = StringUtils.leftPad("0" + resultId0, 3); 
        return resultId;
    }
    
    @SuppressWarnings("deprecation")
    public RestHttpTextEntity changePassword(String userId, String newRawPassword) {

        CoreUserBean user = BeanUtils.getPropertyListenerProxy(selectById(userId));

        String encoedNewPassword = passwordEncoder.encode(newRawPassword);
        if(StringUtils.isEmpty(user.getPassword())){
            // 验证密码是否被更新
            if (user.getPassword().equals(encoedNewPassword)) {
                ServletUtils.responseError("GIKAM.USER.PASSWORD.NEW_OLD_PASSWORD_SAME");

                return new RestHttpTextEntity(HttpStatus.FORBIDDEN);
            }
        }


        String oldPassword = user.getPassword();

        // 验证密码策略
        passwordPolicyService.checkPassword(user.getId(), newRawPassword);

        // 更新密码
        user.setPassword(encoedNewPassword);
        user.setFailedLoginAttempts(0L); // 修改密码时重置输入密码错误次数
        user.setExpiredPasswordUses(0L); // 过期密码使用次数
        user.setPasswordUpdatedTime(LocalDateTime.now()); // 密码更新时间
        proxyInstance.update(user);

        CorePasswordHisBean passwordHis = new CorePasswordHisBean();
        passwordHis.setId(ApplicationContextHelper.getNextIdentity());
        passwordHis.setUserId(user.getId());
        passwordHis.setPassword(oldPassword);
        passwordHisService.getDao().insert(passwordHis);

        return new RestHttpTextEntity(HttpStatus.OK);
    }
    
    @Override
    public void insertReportConfigByDept(RestJsonWrapperBean wrapper) {
    	//页面选择的模板
    	CoreReportConfigLineBean crlBean = wrapper.parseUnique(CoreReportConfigLineBean.class);
    	List<CoreOrgBean> orgList = wrapper.parse(CoreOrgBean.class);
    	//前端选的科室
    	List<String> orgIdList = orgList.stream().map(CoreOrgBean ::getId).distinct().collect(Collectors.toList());
    	//传的参数
        Long targetId = crlBean.getId();
        String permissionTypeId = "T_CORE_REPORT_CONFIG_LINE";
        if(orgIdList.isEmpty()){
    		return;
        }
    	//科室下的用户
    	List<CoreUserBean> userList = userService.getDao().selectListByOneColumnValues(orgIdList, "orgId");
    	if(userList.isEmpty()){
    		return;
    	}
        List<CoreUserPermissionBean> userPermissionList = new ArrayList<>();
        
    	for(int i=0;i<userList.size();i++){
    		CoreUserPermissionBean cup = new CoreUserPermissionBean();
    		cup.setId(ApplicationContextHelper.getNextIdentity()+i);
    		cup.setUserId(userList.get(i).getId());
    		cup.setTargetId(targetId.toString());
    		cup.setPermissionTypeId(permissionTypeId);
    		cup.setDefaultFlag("0");
    		cup.setCreatedById(LocalContextHelper.getLoginUserId());
    		cup.setCreatedByName(LocalContextHelper.getLoginUserName());
    		cup.setCreatedTime(LocalDateTime.now());
    		userPermissionList.add(cup);
    	}
    	userPermissionService.getDao().insert(userPermissionList);
    	}
    
    @Override
    public void deleteReportConfigByDept(RestJsonWrapperBean wrapper) {
    	//页面选择的模板
    	CoreReportConfigLineBean crlBean = wrapper.parseUnique(CoreReportConfigLineBean.class);
    	List<CoreOrgBean> orgList = wrapper.parse(CoreOrgBean.class);
    	//前端选的科室
    	List<String> orgIdList = orgList.stream().map(CoreOrgBean ::getId).distinct().collect(Collectors.toList());
    	//传的参数
        Long targetId = crlBean.getId();
        String permissionTypeId = "T_CORE_REPORT_CONFIG_LINE";
        if(orgIdList.isEmpty()){
    		return;
        }
    	//科室下的用户
    	List<CoreUserBean> userList = userService.getDao().selectListByOneColumnValues(orgIdList, "orgId");
    	List<String> userIdList = userList.stream().map(CoreUserBean :: getId).distinct().collect(Collectors.toList());
    	if(userList.isEmpty()){
    		return;
    	}
    	//选中报表对应选的科室的人员
        List<CoreUserPermissionBean> userPermissionList = userPermissionService
        		.selectListByFilter(SearchFilter.instance().match("userId",userIdList).filter(MatchPattern.OR)
        				.match("permissionTypeId",permissionTypeId).filter(MatchPattern.SEQ).match("targetId",targetId.toString()).filter(MatchPattern.SEQ));
    	userPermissionService.getDao().deleteBy(userPermissionList);
    	
    	}


}
