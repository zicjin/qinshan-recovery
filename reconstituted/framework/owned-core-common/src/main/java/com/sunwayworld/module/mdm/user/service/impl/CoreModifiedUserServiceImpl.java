/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserPermissionBean;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserRoleBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.dao.CoreModifiedUserDao;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserPermissionService;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserRoleService;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserService;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.addomain.bean.CoreAdDomainUserBean;
import com.sunwayworld.module.sys.addomain.service.CoreAdDomainUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreModifiedUserServiceImpl
implements CoreModifiedUserService {
    private static final Logger logger = LogManager.getLogger(CoreModifiedUserService.class);
    @Autowired
    private CoreModifiedUserDao modifiedUserDao;
    @Autowired
    private CoreModifiedUserPermissionService modifiedUserPermissionService;
    @Autowired
    private CoreModifiedUserRoleService modifiedUserRoleService;
    @Lazy
    @Autowired
    private CoreUserService userService;
    @Autowired
    private CoreAdDomainUserService adDomainUserService;
    @Lazy
    @Autowired
    private CoreUserPermissionService userPermissionService;
    @Lazy
    @Autowired
    private CoreRoleUserService roleUserService;
    @Autowired
    private CoreFileService fileService;
    @Autowired
    private CoreFileManager fileManager;

    @Override
    public CoreModifiedUserDao getDao() {
        return this.modifiedUserDao;
    }

    @Override
    @Transactional
    public Long insertByUserId(String userId) {
        CoreModifiedUserBean filter = new CoreModifiedUserBean();
        filter.setUserId(userId);
        filter.setActivatedFlag("0");
        if (this.getDao().countBy(filter, new String[0]) > 0) {
            throw new ApplicationWarningException("T_CORE_M_USER.WARNING.USERID_IN_ALTERATION", new String[0]);
        }
        CoreUserBean user = (CoreUserBean)this.userService.selectById(userId);
        CoreModifiedUserBean modifiedUser = new CoreModifiedUserBean();
        BeanUtils.copyProperties((Object)user, (Object)modifiedUser, "ID");
        modifiedUser.setUserId(userId);
        modifiedUser.setActivatedFlag("0");
        PersistableHelper.removeCreatedUserData(modifiedUser);
        modifiedUser.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(modifiedUser);
        CoreAdDomainUserBean adDomainUser = (CoreAdDomainUserBean)this.adDomainUserService.selectByIdIfPresent(userId);
        if (adDomainUser != null) {
            this.adDomainUserService.insert("" + modifiedUser.getId(), adDomainUser.getAdUserId());
        }
        CoreFileBean fileFilter = new CoreFileBean();
        fileFilter.setTargetId("T_CORE_USER$" + userId);
        fileFilter.setBizCategory("SIGNATURE");
        List<CoreFileBean> fileList = this.fileService.selectList(fileFilter, new Order[0]);
        if (!fileList.isEmpty()) {
            ArrayList<CoreFileBean> modifiedFileList = new ArrayList<CoreFileBean>();
            for (CoreFileBean file : fileList) {
                try {
                    CoreFileBean modifiedFile = new CoreFileBean();
                    BeanUtils.copyProperties((Object)file, (Object)modifiedFile, new String[0]);
                    modifiedFile.setId(ApplicationContextHelper.getNextIdentity());
                    modifiedFile.setTargetId("T_CORE_M_USER$" + modifiedUser.getId());
                    PersistableHelper.removeCreatedUserData(modifiedFile);
                    PersistableHelper.assignCreatedUserData(modifiedFile);
                    this.fileManager.copyFile(file, modifiedFile);
                    modifiedFileList.add(modifiedFile);
                }
                catch (Exception ex) {
                    logger.warn(ex.getMessage());
                }
            }
            this.fileService.getDao().insert(modifiedFileList);
        }
        CoreUserPermissionBean permissionFilter = new CoreUserPermissionBean();
        permissionFilter.setUserId(userId);
        List<CoreUserPermissionBean> permissionList = this.userPermissionService.selectList(permissionFilter, new Order[0]);
        if (!permissionList.isEmpty()) {
            List modifiedUserPermissionList = permissionList.stream().map(p -> {
                CoreModifiedUserPermissionBean modifiedUserPermission = new CoreModifiedUserPermissionBean();
                modifiedUserPermission.setId(ApplicationContextHelper.getNextIdentity());
                modifiedUserPermission.setModifiedUserId(modifiedUser.getId());
                modifiedUserPermission.setTargetId(p.getTargetId());
                modifiedUserPermission.setDefaultFlag(p.getDefaultFlag());
                modifiedUserPermission.setPermissionTypeId(p.getPermissionTypeId());
                return modifiedUserPermission;
            }).collect(Collectors.toList());
            this.modifiedUserPermissionService.getDao().insert(modifiedUserPermissionList);
        }
        CoreRoleUserBean roleUserFilter = new CoreRoleUserBean();
        roleUserFilter.setUserId(userId);
        List<CoreRoleUserBean> roleUserList = this.roleUserService.selectList(roleUserFilter, new Order[0]);
        if (!roleUserList.isEmpty()) {
            List modifiedUserRoleList = roleUserList.stream().map(r -> {
                CoreModifiedUserRoleBean modifiedUserRole = new CoreModifiedUserRoleBean();
                modifiedUserRole.setId(ApplicationContextHelper.getNextIdentity());
                modifiedUserRole.setModifiedUserId(modifiedUser.getId());
                modifiedUserRole.setRoleId(r.getRoleId());
                modifiedUserRole.setDefaultFlag(r.getDefaultFlag());
                return modifiedUserRole;
            }).collect(Collectors.toList());
            this.modifiedUserRoleService.getDao().insert(modifiedUserRoleList);
        }
        return modifiedUser.getId();
    }

    @Override
    public void insertUserPermission(Long id, String permissionTypeId, RestJsonWrapperBean wrapper) {
        this.modifiedUserPermissionService.insert(id, permissionTypeId, wrapper);
    }

    @Override
    public void deleteUserPermission(Long id, RestJsonWrapperBean wrapper) {
        this.modifiedUserPermissionService.delete(id, wrapper);
    }

    @Override
    public Page<CoreModifiedUserPermissionBean> selectUserPermissionPagination(Long id, String permissionType, RestJsonWrapperBean wrapper) {
        return this.modifiedUserPermissionService.selectPaginationByPermissionType(id, permissionType, wrapper);
    }

    @Override
    public Page<CoreModifiedUserRoleBean> selectUserRolePagination(Long id, RestJsonWrapperBean wrapper) {
        return this.modifiedUserRoleService.selectPaginationByModifiedUserId(id, wrapper);
    }

    @Override
    public Page<CoreModifiedUserBean> selectActivatedPagination(RestJsonWrapperBean wrapper) {
        return this.selectPaginationByFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ).match("EXECUTED", "0").filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<CoreModifiedUserBean> selectExecutedPagination(RestJsonWrapperBean wrapper) {
        return this.selectPaginationByFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ).match("EXECUTED", "1").filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void insertUserRoles(Long id, RestJsonWrapperBean wrapper) {
        this.modifiedUserRoleService.insert(id, wrapper);
    }

    @Override
    public void deleteUserRoles(Long id, RestJsonWrapperBean wrapper) {
        this.modifiedUserRoleService.delete(id, wrapper);
    }

    @Override
    public void setUserRoleDefault(Long id, Long userRoleId) {
        this.modifiedUserRoleService.setUserRoleDefault(id, userRoleId);
    }

    @Override
    public void setUserPermissionDefault(Long id, Long userPermissionId) {
        this.modifiedUserPermissionService.setUserPermissionDefault(id, userPermissionId);
    }

    @Override
    @Transactional
    public void executeScheduled(Long id) {
        CoreModifiedUserBean modifiedUser = (CoreModifiedUserBean)this.getDao().selectByIdIfPresent(id);
        if (modifiedUser == null) {
            return;
        }
        CoreUserBean user = (CoreUserBean)BeanUtils.getPropertyListenerProxy(this.userService.selectByIdIfPresent(modifiedUser.getUserId()));
        if (user == null) {
            return;
        }
        BeanUtils.copyProperties((Object)modifiedUser, (Object)user, PersistableHelper.ignoreProperties("ID", "USERID"));
        this.userService.update(user);
        this.adDomainUserService.delete(modifiedUser.getUserId());
        CoreAdDomainUserBean adDomainUser = (CoreAdDomainUserBean)this.adDomainUserService.selectByIdIfPresent("" + modifiedUser.getId());
        if (adDomainUser != null) {
            this.adDomainUserService.insert(modifiedUser.getUserId(), adDomainUser.getAdUserId());
        }
        CoreFileBean fileFilter = new CoreFileBean();
        fileFilter.setTargetId("T_CORE_USER$" + user.getId());
        fileFilter.setBizCategory("SIGNATURE");
        List<CoreFileBean> fileList = this.fileService.selectList(fileFilter, new Order[0]);
        if (!fileList.isEmpty()) {
            this.fileService.deleteList(fileList);
        }
        CoreFileBean modifiedFileFilter = new CoreFileBean();
        modifiedFileFilter.setTargetId("T_CORE_M_USER$" + id);
        modifiedFileFilter.setBizCategory("SIGNATURE");
        List<CoreFileBean> modifiedFileList = this.fileService.selectList(modifiedFileFilter, new Order[0]);
        if (!modifiedFileList.isEmpty()) {
            ArrayList<CoreFileBean> insertFileList = new ArrayList<CoreFileBean>();
            for (CoreFileBean modifiedFile : modifiedFileList) {
                try {
                    CoreFileBean file = new CoreFileBean();
                    BeanUtils.copyProperties((Object)modifiedFile, (Object)file, new String[0]);
                    file.setId(ApplicationContextHelper.getNextIdentity());
                    file.setTargetId("T_CORE_USER$" + modifiedUser.getUserId());
                    PersistableHelper.removeCreatedUserData(file);
                    PersistableHelper.assignCreatedUserData(file);
                    this.fileManager.copyFile(modifiedFile, file);
                    insertFileList.add(file);
                }
                catch (Exception ex) {
                    logger.warn(ex.getMessage());
                }
            }
            this.fileService.getDao().insert(insertFileList);
        }
        CoreUserPermissionBean permissionFilter = new CoreUserPermissionBean();
        permissionFilter.setUserId(user.getId());
        this.userPermissionService.getDao().deleteBy((CoreUserPermissionBean)permissionFilter, "USERID");
        this.roleUserService.deleteByUserId(user.getId());
        CoreModifiedUserPermissionBean modifiedPermissionFilter = new CoreModifiedUserPermissionBean();
        modifiedPermissionFilter.setModifiedUserId(id);
        List<CoreModifiedUserPermissionBean> modifiedUserPermissionList = this.modifiedUserPermissionService.selectList(modifiedPermissionFilter, new Order[0]);
        if (!modifiedUserPermissionList.isEmpty()) {
            ArrayList<CoreUserPermissionBean> insertPermissionList = new ArrayList<CoreUserPermissionBean>();
            for (CoreModifiedUserPermissionBean modifiedUserPermission : modifiedUserPermissionList) {
                CoreUserPermissionBean userPermission = new CoreUserPermissionBean();
                userPermission.setId(ApplicationContextHelper.getNextIdentity());
                userPermission.setUserId(user.getId());
                userPermission.setTargetId(modifiedUserPermission.getTargetId());
                userPermission.setPermissionTypeId(modifiedUserPermission.getPermissionTypeId());
                userPermission.setDefaultFlag(modifiedUserPermission.getDefaultFlag());
                insertPermissionList.add(userPermission);
            }
            this.userPermissionService.getDao().insert(insertPermissionList);
        }
        CoreModifiedUserRoleBean modifiedUserRoleFilter = new CoreModifiedUserRoleBean();
        modifiedUserRoleFilter.setModifiedUserId(id);
        List<CoreModifiedUserRoleBean> modifiedUserRoleList = this.modifiedUserRoleService.selectList(modifiedUserRoleFilter, new Order[0]);
        if (!modifiedUserRoleList.isEmpty()) {
            ArrayList<CoreRoleUserBean> insertRoleUserList = new ArrayList<CoreRoleUserBean>();
            for (CoreModifiedUserRoleBean modifiedUserRole : modifiedUserRoleList) {
                CoreRoleUserBean roleUser = new CoreRoleUserBean();
                roleUser.setId(ApplicationContextHelper.getNextIdentity());
                roleUser.setUserId(user.getId());
                roleUser.setRoleId(modifiedUserRole.getRoleId());
                roleUser.setDefaultFlag(modifiedUserRole.getDefaultFlag());
                insertRoleUserList.add(roleUser);
            }
            this.roleUserService.getDao().insert(insertRoleUserList);
        }
        modifiedUser.setExecuted("1");
        this.getDao().update(modifiedUser, new String[]{"EXECUTED"});
    }
}
