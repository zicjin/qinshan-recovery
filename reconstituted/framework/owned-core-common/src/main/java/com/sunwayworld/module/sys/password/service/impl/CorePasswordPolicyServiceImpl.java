package com.sunwayworld.module.sys.password.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.PasswordException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.password.bean.CorePasswordHisBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.dao.CorePasswordPolicyDao;
import com.sunwayworld.module.sys.password.service.CorePasswordHisService;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CorePasswordPolicyServiceImpl implements CorePasswordPolicyService {
   @Autowired
   private CorePasswordPolicyDao policyDao;
   @Autowired
   @Lazy
   private CoreUserService userService;
   @Autowired
   @Lazy
   private CoreRoleUserService roleUserService;
   @Autowired
   @Lazy
   private CoreRolePermissionService rolePermissionService;
   @Autowired
   @Lazy
   private CoreRoleService roleService;
   @Autowired
   private CorePasswordHisService hisService;
   @Autowired
   @Lazy
   private PasswordEncoder passwordEncoder;

   public CorePasswordPolicyDao getDao() {
      return this.policyDao;
   }

   @Transactional
   @Override
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CorePasswordPolicyBean policy = jsonWrapper.parseUnique(CorePasswordPolicyBean.class);
      policy.setId(ApplicationContextHelper.getNextIdentity());
      policy.setType("ROLE");
      this.getDao().insert(policy);
      return policy.getId();
   }

   @Override
   public List<CorePasswordPolicyBean> selectEffectedList(String userId) {
      List<CorePasswordPolicyBean> effectedList = new ArrayList<>();
      List<CoreRoleUserBean> roleUserList = this.roleUserService.selectByUserId(userId);
      if (!roleUserList.isEmpty()) {
         List<CoreRoleBean> roleList = roleUserList.stream().map(u -> this.roleService.selectById(u.getRoleId())).collect(Collectors.toList());
         List<Long> idList = roleList.stream()
            .filter(r -> "0".equals(r.getLastSuspendedFlag()) && r.getPasswordPolicyId() != null)
            .map(r -> r.getPasswordPolicyId())
            .collect(Collectors.toList());
         if (!idList.isEmpty()) {
            idList.forEach(i -> {
               CorePasswordPolicyBean policy = this.getDao().selectByIdIfPresent(i);
               if (policy != null && !"1".equals(policy.getLastSuspendedFlag())) {
                  effectedList.add(policy);
               }
            });
         }
      }

      if (effectedList.isEmpty()) {
         effectedList.add(this.getDao().selectById(1L));
      }

      return effectedList;
   }

   @Override
   public void checkPassword(String userId, String rawPassword) {
      List<CorePasswordPolicyBean> policyList = this.selectEffectedList(userId);
      if (!policyList.isEmpty()) {
         CorePasswordHisBean hisFilter = new CorePasswordHisBean();
         hisFilter.setUserId(userId);
         List<CorePasswordHisBean> hisList = this.hisService.selectList(hisFilter, new Order[]{Order.desc("ID")});
         LocalDateTime now = LocalDateTime.now();

         for (CorePasswordPolicyBean policy : policyList) {
            StringBuilder sb = new StringBuilder();
            if (policy.getMinNumber() != null && !rawPassword.matches(".*(\\d{1}\\D*){" + policy.getMinNumber() + ",}.*")) {
               sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.MIN_NUMBER_REQUIRED")).append(":").append(policy.getMinNumber());
            }

            if (policy.getMinLowercase() != null && !rawPassword.matches(".*([a-z]{1}[^a-z]*){" + policy.getMinLowercase() + ",}.*")) {
               if (sb.length() > 0) {
                  sb.append(",");
               }

               sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.MIN_LOWERCASE_REQUIRED")).append(":").append(policy.getMinLowercase());
            }

            if (policy.getMinUppercase() != null && !rawPassword.matches(".*([A-Z]{1}[^A-Z]*){" + policy.getMinUppercase() + ",}.*")) {
               if (sb.length() > 0) {
                  sb.append(",");
               }

               sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.MIN_UPPERCASE_REQUIRED")).append(":").append(policy.getMinUppercase());
            }

            if (policy.getMinSpecialCharacters() != null
               && !rawPassword.matches(".*([^A-Za-z0-9]{1}[A-Za-z0-9]*){" + policy.getMinSpecialCharacters() + ",}.*")) {
               if (sb.length() > 0) {
                  sb.append(",");
               }

               sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.MIN_SPECIALCHARACTERS_REQUIRED")).append(":").append(policy.getMinSpecialCharacters());
            }

            if (policy.getMinLength() != null && rawPassword.length() < policy.getMinLength()) {
               if (sb.length() > 0) {
                  sb.append(",");
               }

               sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.MIN_LENGTH")).append(":").append(policy.getMinLength());
            }

            if (policy.getMaxLength() != null && rawPassword.length() > policy.getMaxLength()) {
               if (sb.length() > 0) {
                  sb.append(",");
               }

               sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.MAX_LENGTH")).append(":").append(policy.getMaxLength());
            }

            if (policy.getComposition() != null && policy.getComposition() > 0L) {
               int qty = 0;
               if (StringUtils.containsNumber(rawPassword)) {
                  qty++;
               }

               if (StringUtils.containsLowercaseLetter(rawPassword)) {
                  qty++;
               }

               if (StringUtils.containsCapitalLetter(rawPassword)) {
                  qty++;
               }

               if (StringUtils.containsSpecialCharacter(rawPassword)) {
                  qty++;
               }

               if (policy.getComposition() > qty) {
                  if (sb.length() > 0) {
                     sb.append(",");
                  }

                  sb.append(I18nHelper.getMessage("GIKAM.USER.PASSWORD.COMPOSITION")).append(":").append(policy.getComposition());
               }
            }

            if (sb.length() > 0) {
               throw new PasswordException(sb.toString());
            }

            if (!hisList.isEmpty()) {
               if (policy.getReuseInterval() != null) {
                  CorePasswordHisBean passwordHis = hisList.stream()
                     .filter(h -> this.passwordEncoder.encode(rawPassword).equals(h.getPassword()))
                     .findFirst()
                     .orElse(null);
                  if (passwordHis != null && passwordHis.getCreatedTime().plusDays(policy.getReuseInterval()).isAfter(now)) {
                     throw new PasswordException(I18nHelper.getMessage("GIKAM.USER.PASSWORD.REUSE_PASSWORD_INTERVAL") + ":" + policy.getReuseInterval());
                  }
               }

               if (policy.getHistoryLength() != null) {
                  CorePasswordHisBean oldPasswordHis = hisList.stream()
                     .filter(h -> this.passwordEncoder.encode(rawPassword).equals(h.getPassword()))
                     .findFirst()
                     .orElse(null);
                  if (oldPasswordHis != null) {
                     int count = 0;

                     for (CorePasswordHisBean passwordHis : hisList) {
                        if (this.passwordEncoder.encode(rawPassword).equals(passwordHis.getPassword())) {
                           break;
                        }

                        count++;
                     }

                     if (count < policy.getHistoryLength()) {
                        throw new PasswordException(
                           I18nHelper.getMessage("GIKAM.USER.PASSWORD.REUSE_PASSWORD_HISTORY_LENGTH") + ":" + policy.getHistoryLength()
                        );
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public CorePasswordPolicyBean selectMatchingPasswordPolicy() {
      String userId = LocalContextHelper.getLoginUserId();
      return this.selectMatchingPasswordPolicy(userId);
   }

   @Override
   public CorePasswordPolicyBean selectMatchingPasswordPolicy(String userId) {
      List<CorePasswordPolicyBean> passwordPolicyList = this.selectEffectedList(userId);
      boolean expiredPolicyMandatory = passwordPolicyList.stream().anyMatch(f -> "mandatory".equals(f.getExpiredPolicy()));
      boolean bydayFailedAttemptPolicy = passwordPolicyList.stream()
         .anyMatch(f -> "byday".equals(f.getFailedAttemptPolicy()) && f.getMaxFailedAttempts() != null && f.getMaxFailedAttempts() > 0L);
      if (passwordPolicyList.isEmpty()) {
         return null;
      } else {
         CorePasswordPolicyBean matchingPasswordPolicy = new CorePasswordPolicyBean();
         int i = 0;

         for (int j = passwordPolicyList.size(); i < j; i++) {
            CorePasswordPolicyBean passwordPolicy = passwordPolicyList.get(i);
            matchingPasswordPolicy.setMinNumber(NumberUtils.max(matchingPasswordPolicy.getMinNumber(), passwordPolicy.getMinNumber()));
            matchingPasswordPolicy.setMinLowercase(NumberUtils.max(matchingPasswordPolicy.getMinLowercase(), passwordPolicy.getMinLowercase()));
            matchingPasswordPolicy.setMinUppercase(NumberUtils.max(matchingPasswordPolicy.getMinUppercase(), passwordPolicy.getMinUppercase()));
            matchingPasswordPolicy.setMinSpecialCharacters(
               NumberUtils.max(matchingPasswordPolicy.getMinSpecialCharacters(), passwordPolicy.getMinSpecialCharacters())
            );
            matchingPasswordPolicy.setMinLength(NumberUtils.max(matchingPasswordPolicy.getMinLength(), passwordPolicy.getMinLength()));
            matchingPasswordPolicy.setMaxLength(NumberUtils.min(matchingPasswordPolicy.getMaxLength(), passwordPolicy.getMaxLength()));
            matchingPasswordPolicy.setComposition(NumberUtils.max(matchingPasswordPolicy.getComposition(), passwordPolicy.getComposition()));
            matchingPasswordPolicy.setMaxFailedAttempts(NumberUtils.min(matchingPasswordPolicy.getMaxFailedAttempts(), passwordPolicy.getMaxFailedAttempts()));
            matchingPasswordPolicy.setExpiryInterval(NumberUtils.min(matchingPasswordPolicy.getExpiryInterval(), passwordPolicy.getExpiryInterval()));
            matchingPasswordPolicy.setExpiryWarningInterval(
               NumberUtils.max(matchingPasswordPolicy.getExpiryWarningInterval(), passwordPolicy.getExpiryWarningInterval())
            );
            matchingPasswordPolicy.setExpiredMaxUse(NumberUtils.min(matchingPasswordPolicy.getExpiredMaxUse(), passwordPolicy.getExpiredMaxUse()));
            matchingPasswordPolicy.setReuseInterval(NumberUtils.min(matchingPasswordPolicy.getReuseInterval(), passwordPolicy.getReuseInterval()));
            matchingPasswordPolicy.setHistoryLength(NumberUtils.max(matchingPasswordPolicy.getHistoryLength(), passwordPolicy.getHistoryLength()));
         }

         if (matchingPasswordPolicy.getMinNumber() != null && matchingPasswordPolicy.getMinNumber() == 0L) {
            matchingPasswordPolicy.setMinNumber(null);
         }

         if (matchingPasswordPolicy.getMinLowercase() != null && matchingPasswordPolicy.getMinLowercase() == 0L) {
            matchingPasswordPolicy.setMinLowercase(null);
         }

         if (matchingPasswordPolicy.getMinUppercase() != null && matchingPasswordPolicy.getMinUppercase() == 0L) {
            matchingPasswordPolicy.setMinUppercase(null);
         }

         if (matchingPasswordPolicy.getMinSpecialCharacters() != null && matchingPasswordPolicy.getMinSpecialCharacters() == 0L) {
            matchingPasswordPolicy.setMinSpecialCharacters(null);
         }

         if (matchingPasswordPolicy.getMinLength() != null && matchingPasswordPolicy.getMinLength() == 0L) {
            matchingPasswordPolicy.setMinLength(null);
         }

         if (matchingPasswordPolicy.getMaxLength() != null && matchingPasswordPolicy.getMaxLength() == 0L) {
            matchingPasswordPolicy.setMaxLength(null);
         }

         if (matchingPasswordPolicy.getComposition() != null && matchingPasswordPolicy.getComposition() == 0L) {
            matchingPasswordPolicy.setComposition(null);
         }

         if (matchingPasswordPolicy.getMaxFailedAttempts() != null && matchingPasswordPolicy.getMaxFailedAttempts() == 0L) {
            matchingPasswordPolicy.setMaxFailedAttempts(null);
         }

         if (matchingPasswordPolicy.getExpiryInterval() != null && matchingPasswordPolicy.getExpiryInterval() == 0L) {
            matchingPasswordPolicy.setExpiryInterval(null);
         }

         if (matchingPasswordPolicy.getExpiryWarningInterval() != null && matchingPasswordPolicy.getExpiryWarningInterval() == 0L) {
            matchingPasswordPolicy.setExpiryWarningInterval(null);
         }

         if (matchingPasswordPolicy.getExpiredMaxUse() != null && matchingPasswordPolicy.getExpiredMaxUse() == 0L) {
            matchingPasswordPolicy.setExpiredMaxUse(null);
         }

         if (matchingPasswordPolicy.getReuseInterval() != null && matchingPasswordPolicy.getReuseInterval() == 0L) {
            matchingPasswordPolicy.setReuseInterval(null);
         }

         if (matchingPasswordPolicy.getHistoryLength() != null && matchingPasswordPolicy.getHistoryLength() == 0L) {
            matchingPasswordPolicy.setHistoryLength(null);
         }

         if (expiredPolicyMandatory) {
            matchingPasswordPolicy.setExpiredPolicy("mandatory");
         }

         if (bydayFailedAttemptPolicy) {
            matchingPasswordPolicy.setFailedAttemptPolicy("byday");
            Long maxFailedAttempts = passwordPolicyList.stream()
               .filter(f -> "byday".equals(f.getFailedAttemptPolicy()) && f.getMaxFailedAttempts() != null && f.getMaxFailedAttempts() > 0L)
               .mapToLong(e -> e.getMaxFailedAttempts())
               .min()
               .getAsLong();
            matchingPasswordPolicy.setMaxFailedAttempts(maxFailedAttempts);
         }

         return matchingPasswordPolicy;
      }
   }

   @Override
   public Long selectPasswordExpireRemainingDays() {
      CoreUserBean loginUser = LocalContextHelper.getLoginUser();
      List<CorePasswordPolicyBean> passwordPolicyList = this.selectEffectedList(loginUser.getId());
      return this.selectPasswordExpireRemainingDays(passwordPolicyList);
   }

   @Override
   public Long selectPasswordExpireRemainingDays(List<CorePasswordPolicyBean> passwordPolicyList) {
      CoreUserBean loginUser = LocalContextHelper.getLoginUser();
      return this.selectPasswordExpireRemainingDays(loginUser, passwordPolicyList);
   }

   @Override
   public Long selectPasswordExpireRemainingDays(CoreUserBean coreUser, List<CorePasswordPolicyBean> passwordPolicyList) {
      List<CorePasswordPolicyBean> expiryWarningIntervalPasswordPolicyList = passwordPolicyList.stream()
         .filter(p -> p.getExpiryInterval() != null && p.getExpiryInterval() > 0L && p.getExpiryWarningInterval() != null && p.getExpiryWarningInterval() > 0L)
         .collect(Collectors.toList());
      if (!expiryWarningIntervalPasswordPolicyList.isEmpty()) {
         LocalDateTime now = LocalDateTime.now();
         List<Long> passwordExpireRemainingDaysList = new ArrayList<>();

         for (CorePasswordPolicyBean passwordPolicy : expiryWarningIntervalPasswordPolicyList) {
            if (coreUser.getPasswordUpdatedTime() == null) {
               coreUser.setPasswordUpdatedTime(LocalDateTime.now());
               this.userService.getDao().update(coreUser, "PASSWORDUPDATEDTIME");
            }

            if (coreUser.getPasswordUpdatedTime().plusDays(passwordPolicy.getExpiryInterval() - passwordPolicy.getExpiryWarningInterval()).isBefore(now)) {
               long passwordExpireRemainingDays = Duration.between(now, coreUser.getPasswordUpdatedTime().plusDays(passwordPolicy.getExpiryInterval()))
                  .toDays();
               passwordExpireRemainingDaysList.add(passwordExpireRemainingDays);
            }
         }

         return passwordExpireRemainingDaysList.isEmpty() ? null : passwordExpireRemainingDaysList.stream().mapToLong(e -> e).min().getAsLong();
      } else {
         return null;
      }
   }

   @Override
   public Long selectPasswordExpiredMaxUse(List<CorePasswordPolicyBean> passwordPolicyList) {
      List<CorePasswordPolicyBean> expiredMaxUsePasswordPolicyList = passwordPolicyList.stream()
         .filter(p -> p.getExpiredMaxUse() != null && p.getExpiredMaxUse() > 0L)
         .collect(Collectors.toList());
      return !expiredMaxUsePasswordPolicyList.isEmpty()
         ? expiredMaxUsePasswordPolicyList.stream().mapToLong(e -> e.getExpiredMaxUse()).min().getAsLong()
         : null;
   }
}
