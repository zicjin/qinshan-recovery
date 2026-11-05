package com.sunwayworld.module.mdm.user.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.org.service.CoreOrgUserService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserSignatureDTO;
import com.sunwayworld.module.mdm.user.dao.CoreUserDao;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.addomain.bean.CoreAdDomainUserBean;
import com.sunwayworld.module.sys.addomain.service.CoreAdDomainUserService;
import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.helper.SystemConfigHelper;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import com.sunwayworld.module.sys.password.bean.CorePasswordHisBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordHisService;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreUserServiceImpl implements CoreUserService {
   private static final List<String> INVALID_USERID_LIST = Arrays.asList("DEFAULT", "SYSTEM", "ADMIN", "ROOT");
   @Autowired
   private CoreUserDao userDao;
   @Autowired
   @Lazy
   private CoreUserService proxyInstance;
   @Autowired
   @Lazy
   private CoreRoleUserService roleUserService;
   @Autowired
   @Lazy
   private CoreOrgUserService orgUserService;
   @Autowired
   private CoreUserPermissionService userPermissionService;
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
   private CoreAdDomainUserService coreAdDomainUserService;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;
   @Autowired
   @Lazy
   private CoreSystemConfigService systemConfigService;
   public static final String USER_INCORRECT_PASSWORD_CACHE_NAME = "USER_INCORRECT_PASSWORD_NAME";

   public CoreUserDao getDao() {
      return this.userDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public String insert(RestJsonWrapperBean wrapper) {
      CoreUserBean user = wrapper.parseUnique(CoreUserBean.class);
      if (CollectionUtils.containsIgnoreCase(INVALID_USERID_LIST, user.getId())) {
         throw new InvalidDataException("GIKAM.USER.INVALID_USER_ID");
      } else {
         user.setPassword(this.getDefaultPassword(user.getId()));
         LocalDateTime now = LocalDateTime.now();
         user.setPasswordUpdatedTime(now);
         user.setStatus("locked");
         long expiryInterval = NumberUtils.parseLong(ApplicationContextHelper.getConstantValue("ACCOUNT_EXPIRY_INTERVAL"));
         if (expiryInterval > 0L) {
            user.setExpiryDate(now.plusDays(expiryInterval));
         }

         CoreLoginConfigBean loginConfig = SystemConfigHelper.getLoginConfig();
         if ("adDomain".equals(loginConfig.getLoginAuthMethod()) || "systemOrAdDomain".equals(loginConfig.getLoginAuthMethod())) {
            CoreAdDomainUserBean adDomainUser = new CoreAdDomainUserBean();
            adDomainUser.setId(user.getId());
            if ("1".equals(loginConfig.getAdUserIdIgnoreCase())) {
               adDomainUser.setAdUserId(StringUtils.lowerCase(user.getId()));
            } else {
               adDomainUser.setAdUserId(user.getId());
            }

            this.coreAdDomainUserService.getDao().insert(adDomainUser);
         }

         this.getDao().insert(user);
         return user.getId();
      }
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void postUpdate(List<String> updatedColumnNameList, List<String> idList) {
      if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "status")) {
         List<CoreUserBean> updatedUserList = this.selectListByIds(idList);
         List<CoreUserBean> updatedStatusUserList = updatedUserList.stream().filter(u -> "activated".equals(u.getStatus())).map(u -> {
            u.setExpiredPasswordUses(0L);
            u.setFailedLoginAttempts(0L);
            return (CoreUserBean)u;
         }).collect(Collectors.toList());
         if (!updatedStatusUserList.isEmpty()) {
            this.getDao().update(updatedStatusUserList, new String[]{"EXPIREDPASSWORDUSES", "FAILEDLOGINATTEMPTS"});
            updatedStatusUserList.forEach(
               e -> {
                  List<CorePasswordPolicyBean> effectedList = this.passwordPolicyService.selectEffectedList(e.getId());
                  if (!effectedList.isEmpty()) {
                     boolean bydayFailedAttemptPolicy = effectedList.stream()
                        .anyMatch(f -> "byday".equals(f.getFailedAttemptPolicy()) && f.getMaxFailedAttempts() != null && f.getMaxFailedAttempts() > 0L);
                     if (bydayFailedAttemptPolicy) {
                        Long maxFailedAttempts = effectedList.stream()
                           .filter(f -> "byday".equals(f.getFailedAttemptPolicy()) && f.getMaxFailedAttempts() != null && f.getMaxFailedAttempts() > 0L)
                           .mapToLong(item -> item.getMaxFailedAttempts())
                           .min()
                           .getAsLong();
                        this.setLoginUserIncorrectPasswordAttempts(e.getId(), maxFailedAttempts);
                     }
                  }
               }
            );
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      wrapper.parse(CoreUserBean.class).forEach(u -> this.proxyInstance.delete(u.getId()));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(String id) {
      this.getDao().delete(id);
      this.roleUserService.deleteByUserId(id);
   }

   public CoreUserBean selectById(String id) {
      return this.getDao().selectByIdIfPresent(id);
   }

   @Override
   public CoreUserBean selectByLoginId(String loginId) {
      if (StringUtils.isBlank(loginId)) {
         return null;
      }
      CoreUserBean selectedUser = this.proxyInstance.selectById(loginId);
      if (selectedUser != null) {
         return selectedUser;
      } else {
         String loginMode = ApplicationContextHelper.getConstantValue("LOGIN.LOGINMODE");
         CoreUserBean search = new CoreUserBean();
         if (!StringUtils.isEmpty(loginMode) && loginMode.length() == 5) {
            if ("1".equals(loginMode.substring(0, 1))) {
               search.setId(loginId);
            }

            if ("1".equals(loginMode.substring(1, 2))) {
               search.setMobile(loginId);
            }

            if ("1".equals(loginMode.substring(2, 3))) {
               search.setIdCard(loginId);
            }

            if ("1".equals(loginMode.substring(3, 4))) {
               search.setEmail(loginId);
            }

            if ("1".equals(loginMode.substring(4, 5))) {
               search.setUserNo(loginId);
            }

            return this.getDao().selectOneUnionIfPresent(search, new Order[0]);
         } else {
            return null;
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void resetPassword(RestJsonWrapperBean wrapper) {
      wrapper.parse(CoreUserBean.class).forEach(u -> this.proxyInstance.resetPassword(u.getId()));
   }

   @Override
   public void resetPassword(String id) {
      CoreUserBean user = BeanUtils.getPropertyListenerProxy(this.getDao().selectById(id));
      String oldPassword = user.getPassword();
      user.setId(id);
      user.setPassword(this.getDefaultPassword(id));
      user.setFailedLoginAttempts(0L);
      user.setExpiredPasswordUses(0L);
      user.setPasswordUpdatedTime(LocalDateTime.now());
      this.getDao().update(user, new String[0]);
      CorePasswordHisBean passwordHis = new CorePasswordHisBean();
      passwordHis.setId(ApplicationContextHelper.getNextIdentity());
      passwordHis.setUserId(user.getId());
      passwordHis.setPassword(oldPassword);
      this.passwordHisService.getDao().insert(passwordHis);
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public RestHttpTextEntity changePassword(RestJsonWrapperBean wrapper) {
      String oldPassword = AuthenticationHelper.getRawPassword(wrapper.getParamValue("oldPassword"));
      String newRawPassword = AuthenticationHelper.getRawPassword(wrapper.getParamValue("newRawPassword"));
      if (!StringUtils.isEmpty(oldPassword) && !StringUtils.isEmpty(newRawPassword)) {
         CoreUserBean user = BeanUtils.getPropertyListenerProxy(LocalContextHelper.getLoginUser());
         String encodedOldPassword = this.passwordEncoder.encode(oldPassword);
         if (!user.getPassword().equals(encodedOldPassword)
            && !user.getPassword().equals(EncryptUtils.MD5Encrypt(oldPassword))
            && !user.getPassword().equals(EncryptUtils.SM3Encrypt(oldPassword))) {
            ServletUtils.responseError("GIKAM.USER.PASSWORD.INVALID_OLD_PASSWORD");
            return new RestHttpTextEntity(HttpStatus.FORBIDDEN);
         } else {
            return this.changePassword(user.getId(), newRawPassword);
         }
      } else {
         ServletUtils.responseError("GIKAM.USER.PASSWORD.REQUIRE_NOT_EMPTY");
         return new RestHttpTextEntity(HttpStatus.BAD_REQUEST);
      }
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public RestHttpTextEntity changeNewPassword(RestJsonWrapperBean wrapper) {
      String newRawPassword = AuthenticationHelper.getRawPassword(wrapper.getParamValue("newRawPassword"));
      if (StringUtils.isEmpty(newRawPassword)) {
         ServletUtils.responseError("GIKAM.USER.PASSWORD.REQUIRE_NOT_EMPTY");
         return new RestHttpTextEntity(HttpStatus.BAD_REQUEST);
      } else {
         return this.changePassword(LocalContextHelper.getLoginUserId(), newRawPassword);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public RestHttpTextEntity changePassword(String id, RestJsonWrapperBean wrapper) {
      if (!StringUtils.isEmpty(wrapper.getParamValue("oldPassword"))) {
         String oldPassword = AuthenticationHelper.getRawPassword(wrapper.getParamValue("oldPassword"));
         if (!StringUtils.isEmpty(oldPassword)) {
            CoreUserBean user = BeanUtils.getPropertyListenerProxy(LocalContextHelper.getLoginUser());
            String encodedOldPassword = this.passwordEncoder.encode(oldPassword);
            if (!user.getPassword().equals(encodedOldPassword)) {
               ServletUtils.responseError("GIKAM.USER.PASSWORD.INVALID_OLD_PASSWORD");
               return new RestHttpTextEntity(HttpStatus.FORBIDDEN);
            }
         }
      }

      String newRawPassword = AuthenticationHelper.getRawPassword(wrapper.getParamValue("newRawPassword"));
      if (StringUtils.isEmpty(newRawPassword)) {
         ServletUtils.responseError("GIKAM.USER.PASSWORD.REQUIRE_NOT_EMPTY");
         return new RestHttpTextEntity(HttpStatus.BAD_REQUEST);
      } else {
         return this.changePassword(id, newRawPassword);
      }
   }

   @Override
   public Page<CoreUserBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      parameter.setSuspendedFlag("0");
      parameter.setChoosableQueries();
      return this.selectPagination(parameter, rowBounds);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void deprecate(RestJsonWrapperBean wrapper) {
      List<CoreUserBean> userList = wrapper.parse(CoreUserBean.class);
      List<String> idList = userList.stream().map(e -> e.getId()).collect(Collectors.toList());
      CoreUserBean user = new CoreUserBean();
      user.setStatus("deprecated");
      this.getDao().updateByIds(user, idList, new String[0]);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertUserPermission(String id, String permissionTypeId, RestJsonWrapperBean wrapper) {
      this.userPermissionService.insert(id, permissionTypeId, wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteUserPermission(String id, String permissionTypeId, RestJsonWrapperBean wrapper) {
      List<String> idList = wrapper.parseId(String.class);
      if (idList.stream().allMatch(i -> NumberUtils.isNumber(i))) {
         List<CoreUserPermissionBean> permissionList = wrapper.parse(CoreUserPermissionBean.class);

         for (CoreUserPermissionBean permission : permissionList) {
            permission.setPermissionTypeId(permissionTypeId);
            permission.setUserId(id);
         }

         if (permissionList.stream().allMatch(p -> StringUtils.isEmpty(p.getTargetId()))) {
            permissionList.forEach(p -> p.setTargetId("" + p.getId()));
         }

         this.userPermissionService.getDao().deleteBy(permissionList, "USERID", "TARGETID", "PERMISSIONTYPEID");
      } else {
         List<CoreUserPermissionBean> permissionList = new ArrayList<>();

         for (String strId : idList) {
            CoreUserPermissionBean permission = new CoreUserPermissionBean();
            permission.setTargetId(strId);
            permission.setPermissionTypeId(permissionTypeId);
            permission.setUserId(id);
            permissionList.add(permission);
         }

         this.userPermissionService.getDao().deleteBy(permissionList, "USERID", "TARGETID", "PERMISSIONTYPEID");
      }
   }

   @Override
   public boolean defaultPassword() {
      String userId = LocalContextHelper.getLoginUserId();
      return this.proxyInstance.selectById(userId).getPassword().equals(this.getDefaultPassword(userId));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void setDefaultUserPermission(String id, String permissionTypeId, RestJsonWrapperBean wrapper) {
      List<CoreUserPermissionBean> updateUserPermissionList = new ArrayList<>();
      CoreUserPermissionBean filter = new CoreUserPermissionBean();
      filter.setUserId(id);
      filter.setPermissionTypeId(permissionTypeId);
      filter.setDefaultFlag("1");
      List<CoreUserPermissionBean> userPermissionList = this.userPermissionService.selectList(filter, new Order[0]);
      if (userPermissionList.size() > 0) {
         CoreUserPermissionBean userPermissionBean = userPermissionList.get(0);
         userPermissionBean.setDefaultFlag("0");
         updateUserPermissionList.add(userPermissionBean);
      }

      CoreUserPermissionBean defaultUserPermission = wrapper.parseUnique(CoreUserPermissionBean.class);
      defaultUserPermission.setDefaultFlag("1");
      updateUserPermissionList.add(defaultUserPermission);
      this.userPermissionService.getDao().update(updateUserPermissionList, "DEFAULTFLAG");
   }

   @Override
   public CoreUserSignatureDTO sign(RestJsonWrapperBean wrapper) {
      String username = wrapper.getParamValue("username");
      String password = wrapper.getParamValue("password");
      DefaultUserDetailsChecker.checkPassword(username, this.getEncodedPassword(password));
      CoreUserSignatureDTO userSignature = new CoreUserSignatureDTO();
      CoreUserBean signUser = this.selectById(username);
      userSignature.setSignUserId(signUser.getId());
      userSignature.setSignUserName(signUser.getUserName());
      userSignature.setSignDate(LocalDate.now());
      userSignature.setSignTime(LocalDateTime.now());
      userSignature.setSignPicUrl(this.selectSignatureUrl(username));
      return userSignature;
   }

   @Override
   public String selectSignatureUrl(String userId) {
      CoreFileBean signatureFile = this.coreFileService
         .selectFirstByFilter(
            SearchFilter.instance()
               .match("TARGETID", this.getDao().getTable() + "$" + userId)
               .filter(MatchPattern.EQ)
               .match("BIZCATEGORY", "SIGNATURE")
               .filter(MatchPattern.EQ),
            new Order[]{Order.desc("ID")}
         );
      return signatureFile == null ? null : this.fileManager.getDownloadUrl(signatureFile);
   }

   @Override
   public String selectAvatarUrl(String userId) {
      CoreFileBean avatarFile = this.coreFileService
         .selectFirstByFilter(
            SearchFilter.instance()
               .match("TARGETID", this.getDao().getTable() + "$" + userId)
               .filter(MatchPattern.EQ)
               .match("SCOPE", FileScope.avatar)
               .filter(MatchPattern.EQ),
            new Order[]{Order.desc("ID")}
         );
      return avatarFile == null ? null : this.fileManager.getDownloadUrl(avatarFile);
   }

   @Override
   public Map<String, String> selectAbsoluteAvatarUrl(List<String> userIdList) {
      List<String> targetIdList = userIdList.stream().map(e -> this.getDao().getTable() + "$" + e).collect(Collectors.toList());
      List<CoreFileBean> allAvatarFileList = this.coreFileService
         .selectListByFilter(
            SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("SCOPE", FileScope.avatar).filter(MatchPattern.EQ),
            new Order[]{Order.desc("ID")}
         );
      List<CoreFileBean> effectiveAvatarFileList = new ArrayList<>();
      Map<String, List<CoreFileBean>> avatarFileMap = allAvatarFileList.stream().collect(Collectors.groupingBy(CoreFileBean::getTargetId));
      avatarFileMap.forEach((k, v) -> {
         CoreFileBean effectiveAvatarFile = v.stream().sorted((e, f) -> f.getId().compareTo(e.getId())).findFirst().get();
         effectiveAvatarFileList.add(effectiveAvatarFile);
      });
      Map<String, String> avatarMap = new HashMap<>();
      effectiveAvatarFileList.forEach(e -> {
         String userId = userIdList.stream().filter(f -> (this.getDao().getTable() + "$" + f).equals(e.getTargetId())).findFirst().get();
         String avatarUrl = this.fileManager.getAbsoluteDownloadUrl(e);
         avatarMap.put(userId, avatarUrl);
      });
      return avatarMap;
   }

   @Override
   public RestHttpTextEntity changePassword(String userId, String newRawPassword) {
      CoreUserBean user = BeanUtils.getPropertyListenerProxy(this.selectById(userId));
      String encoedNewPassword = this.passwordEncoder.encode(newRawPassword);
      if (!user.getPassword().equals(encoedNewPassword)
         && !user.getPassword().equals(EncryptUtils.MD5Encrypt(newRawPassword))
         && !user.getPassword().equals(EncryptUtils.SM3Encrypt(newRawPassword))) {
         String oldPassword = user.getPassword();
         this.passwordPolicyService.checkPassword(user.getId(), newRawPassword);
         user.setPassword(encoedNewPassword);
         user.setFailedLoginAttempts(0L);
         user.setExpiredPasswordUses(0L);
         user.setPasswordUpdatedTime(LocalDateTime.now());
         this.proxyInstance.update(user);
         CorePasswordHisBean passwordHis = new CorePasswordHisBean();
         passwordHis.setId(ApplicationContextHelper.getNextIdentity());
         passwordHis.setUserId(user.getId());
         passwordHis.setPassword(oldPassword);
         this.passwordHisService.getDao().insert(passwordHis);
         return new RestHttpTextEntity(HttpStatus.OK);
      } else {
         ServletUtils.responseError("GIKAM.USER.PASSWORD.NEW_OLD_PASSWORD_SAME");
         return new RestHttpTextEntity(HttpStatus.FORBIDDEN);
      }
   }

   @Override
   public Long selectLoginUserIncorrectPasswordAttempts(String userId, List<CorePasswordPolicyBean> passwordPolicyList) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate nowDate = now.toLocalDate();
      String nowDateStr = DateTimeUtils.formatLocalDate(nowDate);
      Long todayRemainfailedAttempts = RedisHelper.get("USER_INCORRECT_PASSWORD_NAME", userId + "::" + nowDateStr);
      if (todayRemainfailedAttempts == null) {
         todayRemainfailedAttempts = passwordPolicyList.stream()
            .filter(f -> "byday".equals(f.getFailedAttemptPolicy()) && f.getMaxFailedAttempts() != null)
            .mapToLong(e -> e.getMaxFailedAttempts())
            .min()
            .getAsLong();
         long millis = Duration.between(now, LocalDateTime.of(nowDate, LocalTime.MAX)).toMillis();
         int remainSecond = NumberUtils.parseInt(ArithUtils.div(NumberUtils.parseDouble(millis), 1000.0));
         RedisHelper.put("USER_INCORRECT_PASSWORD_NAME", userId + "::" + nowDateStr, todayRemainfailedAttempts, remainSecond);
      }

      return todayRemainfailedAttempts;
   }

   @Override
   public void setLoginUserIncorrectPasswordAttempts(String userId, Long remainAttempts) {
      LocalDateTime now = LocalDateTime.now();
      LocalDate nowDate = now.toLocalDate();
      String nowDateStr = DateTimeUtils.formatLocalDate(nowDate);
      long millis = Duration.between(now, LocalDateTime.of(nowDate, LocalTime.MAX)).toMillis();
      int remainSecond = NumberUtils.parseInt(ArithUtils.div(NumberUtils.parseDouble(millis), 1000.0));
      RedisHelper.put("USER_INCORRECT_PASSWORD_NAME", userId + "::" + nowDateStr, remainAttempts, remainSecond);
   }

   @Transactional
   @Override
   public void tenantTriggerInsertOrActivateUser(List<CoreUserBean> userList) {
      List<String> userIdList = userList.stream().map(CoreUserBean::getId).collect(Collectors.toList());
      List<CoreUserBean> existingUserList = this.getDao().selectListByIds(userIdList, new Order[0]);
      if (!existingUserList.isEmpty()) {
         existingUserList.forEach(u -> {
            u.setStatus("activated");
            u.setLastSuspendedFlag("0");
         });
         this.getDao().update(existingUserList, new String[]{"STATUS", "LASTSUSPENDEDFLAG"});
         userList.removeIf(u -> existingUserList.stream().anyMatch(e -> e.getId().equals(u.getId())));
      }

      if (!userList.isEmpty()) {
         userList.forEach(u -> {
            u.setStatus("activated");
            u.setLastSuspendedFlag("0");
         });
         this.getDao().insert(userList);
      }
   }

   @Transactional
   @Override
   public void tenantTriggerDeactivateUser(List<String> idList) {
      List<CoreUserBean> userList = idList.stream().map(i -> {
         CoreUserBean user = new CoreUserBean();
         user.setId(i);
         user.setStatus("deprecated");
         user.setLastSuspendedFlag("1");
         return user;
      }).collect(Collectors.toList());
      this.getDao().update(userList, new String[]{"STATUS", "LASTSUSPENDEDFLAG"});
   }

   private String getEncodedPassword(String rawPassword) {
      return this.passwordEncoder.encode(rawPassword);
   }

   private String getDefaultPassword(String userId) {
      CoreSystemConfigBean systemConfig = SystemConfigHelper.getSystemConfig();
      return "customPassword".equals(systemConfig.getInitPasswordPolicy()) && !StringUtils.isEmpty(systemConfig.getDefaultPassword())
         ? this.getEncodedPassword(systemConfig.getDefaultPassword())
         : this.getEncodedPassword(userId);
   }
}
