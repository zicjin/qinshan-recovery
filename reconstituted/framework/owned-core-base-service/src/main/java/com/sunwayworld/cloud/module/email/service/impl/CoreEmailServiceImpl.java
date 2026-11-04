package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcUserBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigReceipentBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailFileBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailReceipentBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailTemplateBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcRoleService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcUserService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigRecRoleService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigReceipentService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigService;
import com.sunwayworld.cloud.module.email.service.CoreEmailFileService;
import com.sunwayworld.cloud.module.email.service.CoreEmailReceipentService;
import com.sunwayworld.cloud.module.email.service.CoreEmailService;
import com.sunwayworld.cloud.module.email.service.CoreEmailTemplateService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mail.ExchangeMailHelper;
import com.sunwayworld.framework.mail.MailHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreEmailServiceImpl implements CoreEmailService {
   private static final String EMAIL_APPEND_BODY_STYLE_PREFIX_TAG = "<div style='MARGIN-BOTTOM:30px;BORDER-BOTTOM:#000000 1px dashed;COLOR:#333399;padding:0 0 20px 20px; font:normal bold 16px/16px 楷体,楷体_GB2312;'>";
   private static final String EMAIL_APPEND_BODY_STYLE_SUFFIX_TAG = "</div>";
   private static final String SUNWAY_EMAIL_PROTOCOL = "sunway.mail.protocol.type";
   private static final String EMAIL_PROTOCOL = getEmailProtocol();
   @Autowired
   private CoreEmailDao emailDao;
   @Autowired
   private CoreEmailReceipentService emailReceipentService;
   @Autowired
   private CoreEmailTemplateService emailTemplateService;
   @Autowired
   private CoreEmailConfigService emailConfigService;
   @Autowired
   private CoreEmailConfigReceipentService emailConfigReceipentService;
   @Autowired
   private CoreEmailConfigRecRoleService emailConfigRecRoleService;
   @Autowired
   private CoreEmailConfigCcUserService emailConfigCcUserService;
   @Autowired
   private CoreEmailConfigCcRoleService emailConfigCcRoleService;
   @Autowired
   private CoreUserService userService;
   @Autowired
   @Lazy
   private CoreEmailService proxyInstance;
   @Autowired
   @Lazy
   private CoreFileService fileService;
   @Autowired
   @Lazy
   private CoreEmailFileService coreEmailFileService;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;

   public CoreEmailDao getDao() {
      return this.emailDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insert(String configId, List<String> receipentIdList, Map<String, Object> replacementContainer, String... ccUserIds) {
      CoreEmailTemplateBean template = this.getEmailTemplateByConfigId(configId);
      if (template != null) {
         CoreEmailConfigReceipentBean filter = new CoreEmailConfigReceipentBean();
         filter.setConfigId(configId);
         List<CoreEmailConfigReceipentBean> configReceipentList = this.emailConfigReceipentService.selectList(filter, new Order[0]);
         List<String> configReceipentIdList = configReceipentList.stream().map(r -> r.getReceipentId()).collect(Collectors.toList());
         if (receipentIdList != null) {
            configReceipentIdList.addAll(receipentIdList);
         }

         List<String> configRecRoleUserIdList = this.emailConfigRecRoleService.selectUserListByConfig(configId);
         if (!configRecRoleUserIdList.isEmpty()) {
            configReceipentIdList.addAll(configRecRoleUserIdList);
         }

         if (!configReceipentIdList.isEmpty()) {
            List<String> distinctConfigReceipentIdList = configReceipentIdList.stream().distinct().collect(Collectors.toList());
            CoreEmailConfigCcUserBean ccUserfilter = new CoreEmailConfigCcUserBean();
            ccUserfilter.setConfigId(configId);
            List<CoreEmailConfigCcUserBean> configCcUserList = this.emailConfigCcUserService.selectList(ccUserfilter, new Order[0]);
            List<String> configCcUserIdList = configCcUserList.stream().map(r -> r.getUserId()).collect(Collectors.toList());
            List<String> configCcRoleUserIdList = this.emailConfigCcRoleService.selectUserListByConfig(configId);
            if (!configCcRoleUserIdList.isEmpty()) {
               configCcUserIdList.addAll(configCcRoleUserIdList);
            }

            if (ccUserIds != null && ccUserIds.length > 0) {
               configCcUserIdList.addAll(Arrays.asList(ccUserIds));
            }

            List<String> distinctConfigCcUserIdList = configCcUserIdList.stream().distinct().collect(Collectors.toList());
            List<String> userIdList = new ArrayList<>();
            userIdList.addAll(distinctConfigReceipentIdList);
            userIdList.addAll(distinctConfigCcUserIdList);
            List<String> distinctUserIdIdList = userIdList.stream().distinct().collect(Collectors.toList());
            List<CoreUserBean> receipentList = this.userService.selectListByIds(distinctUserIdIdList);
            CoreEmailBean email = this.getEmailByTemplateAndConfigId(configId, template, replacementContainer);
            this.getDao().insert(email);
            List<CoreEmailReceipentBean> emailReceipentList = this.buildEmailReceipentList(
               email.getId(), distinctConfigReceipentIdList, distinctConfigCcUserIdList, receipentList
            );
            this.emailReceipentService.getDao().insert(emailReceipentList);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insert(String configId, Map<String, Object> replacementContainer, List<CoreEmailReceipentBean> additionalReceipentList, List<Long> fileIdList) {
      CoreEmailTemplateBean template = this.getEmailTemplateByConfigId(configId);
      if (template != null) {
         CoreEmailConfigReceipentBean filter = new CoreEmailConfigReceipentBean();
         filter.setConfigId(configId);
         List<CoreEmailConfigReceipentBean> configReceipentList = this.emailConfigReceipentService.selectList(filter, new Order[0]);
         List<String> configReceipentIdList = configReceipentList.stream().map(r -> r.getReceipentId()).collect(Collectors.toList());
         List<String> configRecRoleUserIdList = this.emailConfigRecRoleService.selectUserListByConfig(configId);
         if (!configRecRoleUserIdList.isEmpty()) {
            configReceipentIdList.addAll(configRecRoleUserIdList);
         }

         if (!configReceipentIdList.isEmpty() || !additionalReceipentList.stream().noneMatch(f -> "receipent".equals(f.getReceipentCategory()))) {
            List<String> distinctConfigReceipentIdList = configReceipentIdList.stream().distinct().collect(Collectors.toList());
            CoreEmailConfigCcUserBean ccUserfilter = new CoreEmailConfigCcUserBean();
            ccUserfilter.setConfigId(configId);
            List<CoreEmailConfigCcUserBean> configCcUserList = this.emailConfigCcUserService.selectList(ccUserfilter, new Order[0]);
            List<String> configCcUserIdList = configCcUserList.stream().map(r -> r.getUserId()).collect(Collectors.toList());
            List<String> configCcRoleUserIdList = this.emailConfigCcRoleService.selectUserListByConfig(configId);
            if (!configCcRoleUserIdList.isEmpty()) {
               configCcUserIdList.addAll(configCcRoleUserIdList);
            }

            List<String> distinctConfigCcUserIdList = configCcUserIdList.stream().distinct().collect(Collectors.toList());
            List<String> userIdList = new ArrayList<>();
            userIdList.addAll(distinctConfigReceipentIdList);
            userIdList.addAll(distinctConfigCcUserIdList);
            List<String> distinctUserIdIdList = userIdList.stream().distinct().collect(Collectors.toList());
            List<CoreUserBean> receipentList = this.userService.selectListByIds(distinctUserIdIdList);
            CoreEmailBean email = this.getEmailByTemplateAndConfigId(configId, template, replacementContainer);
            this.getDao().insert(email);
            List<CoreEmailReceipentBean> emailReceipentList = this.buildEmailReceipentList(
               email.getId(), distinctConfigReceipentIdList, distinctConfigCcUserIdList, receipentList
            );
            additionalReceipentList.forEach(
               e -> {
                  if (!StringUtils.isBlank(e.getReceipentName())
                     && !StringUtils.isBlank(e.getReceipentCategory())
                     && !StringUtils.isBlank(e.getReceipentEmail())) {
                     e.setId(ApplicationContextHelper.getNextIdentity());
                     e.setEmailId(email.getId());
                     emailReceipentList.add(e);
                  }
               }
            );
            this.emailReceipentService.getDao().insert(emailReceipentList);
            if (fileIdList != null && !fileIdList.isEmpty()) {
               List<CoreEmailFileBean> emailFileList = new ArrayList<>();
               fileIdList.forEach(e -> {
                  CoreEmailFileBean emailFile = new CoreEmailFileBean();
                  emailFile.setId(ApplicationContextHelper.getNextIdentity());
                  emailFile.setEmailId(email.getId());
                  emailFile.setFileId(e);
                  emailFileList.add(emailFile);
               });
               this.coreEmailFileService.getDao().insert(emailFileList);
            }
         }
      }
   }

   @Override
   public void startEmailJob() {
      SearchFilter filter = SearchFilter.instance()
         .match("SUCCESS", "0")
         .filter(MatchPattern.SEQ)
         .match("REATTEMPT", Constant.SENDING_EMAIL_MAX_REATTEMPT)
         .filter(MatchPattern.NL);
      List<CoreEmailReceipentBean> readyToSendList = this.emailReceipentService.selectListByFilter(filter, new Order[0]);
      List<Long> emailIdList = readyToSendList.stream().map(CoreEmailReceipentBean::getEmailId).distinct().collect(Collectors.toList());
      if (!emailIdList.isEmpty()) {
         List<CoreEmailBean> emailList = this.getDao().selectListByIds(emailIdList, new Order[0]);
         List<CoreEmailFileBean> coreEmailFileList = this.coreEmailFileService
            .selectListByFilter(SearchFilter.instance().match("EMAILID", emailIdList).filter(MatchPattern.OR), new Order[0]);
         LinkedHashMap<Long, List<CoreEmailReceipentBean>> readyToSendGroupMap = readyToSendList.stream()
            .collect(Collectors.groupingBy(e -> e.getEmailId(), LinkedHashMap::new, Collectors.toList()));
         readyToSendGroupMap.forEach((k, v) -> {
            CoreEmailBean email = emailList.stream().filter(e -> e.getId().equals(k)).findFirst().get();
            List<CoreEmailReceipentBean> toList = v.stream().filter(e -> "receipent".equals(e.getReceipentCategory())).distinct().collect(Collectors.toList());
            List<CoreEmailReceipentBean> ccList = v.stream().filter(e -> "cc".equals(e.getReceipentCategory())).distinct().collect(Collectors.toList());
            List<CoreEmailFileBean> subCoreEmailFileList = coreEmailFileList.stream().filter(f -> f.getEmailId().equals(k)).collect(Collectors.toList());
            this.proxyInstance.sendEmailForList(email, toList, ccList, subCoreEmailFileList);
         });
      }
   }

   @Transactional
   @Override
   public void sendEmail(CoreEmailBean email, CoreEmailReceipentBean receipent) {
      CoreEmailReceipentBean proxy = BeanUtils.getPropertyListenerProxy(receipent);
      String subject = this.getReplacedText(email.getEmailSubject(), receipent);
      String body = this.getReplacedText(email.getEmailBody(), receipent);

      try {
         if ("generic".equals(EMAIL_PROTOCOL)) {
            MailHelper.sendHtmlMail(subject, body, Arrays.asList(receipent.getReceipentEmail()));
         } else if ("exchange".equals(EMAIL_PROTOCOL)) {
            ExchangeMailHelper.sendHtmlMail(subject, body, Arrays.asList(receipent.getReceipentEmail()));
         }

         proxy.setSuccess("1");
      } catch (Exception var7) {
         proxy.setReattempt(receipent.getReattempt() + 1);
         if (StringUtils.isEmpty(proxy.getLog())) {
            proxy.setLog(ObjectUtils.getStackTrace(var7));
         } else {
            proxy.setLog(proxy.getLog() + "\n" + ObjectUtils.getStackTrace(var7));
         }
      }

      this.emailReceipentService.update(proxy);
   }

   @Transactional
   @Override
   public void sendEmail(CoreEmailBean email, CoreEmailReceipentBean receipent, List<CoreEmailReceipentBean> otherList) {
      CoreEmailReceipentBean proxy = BeanUtils.getPropertyListenerProxy(receipent);
      String subject = this.getReplacedText(email.getEmailSubject(), receipent);
      String body = this.getReplacedText(email.getEmailBody(), receipent);
      subject = this.getReplacedSubject(subject, receipent.getReceipentCategory());
      body = this.getReplacedBody(body, receipent, otherList);

      try {
         if ("generic".equals(EMAIL_PROTOCOL)) {
            MailHelper.sendHtmlMail(subject, body, Arrays.asList(receipent.getReceipentEmail()));
         } else if ("exchange".equals(EMAIL_PROTOCOL)) {
            ExchangeMailHelper.sendHtmlMail(subject, body, Arrays.asList(receipent.getReceipentEmail()));
         }

         proxy.setSuccess("1");
      } catch (Exception var8) {
         proxy.setReattempt(receipent.getReattempt() + 1);
         if (StringUtils.isEmpty(proxy.getLog())) {
            proxy.setLog(ObjectUtils.getStackTrace(var8));
         } else {
            proxy.setLog(proxy.getLog() + "\n" + ObjectUtils.getStackTrace(var8));
         }
      }

      this.emailReceipentService.update(proxy);
   }

   @Transactional
   @Override
   public void sendEmail(CoreEmailBean email, CoreEmailReceipentBean receipent, List<CoreEmailReceipentBean> otherList, List<CoreEmailFileBean> emailFileList) {
      CoreEmailReceipentBean proxy = BeanUtils.getPropertyListenerProxy(receipent);
      String subject = this.getReplacedText(email.getEmailSubject(), receipent);
      String body = this.getReplacedText(email.getEmailBody(), receipent);
      subject = this.getReplacedSubject(subject, receipent.getReceipentCategory());
      body = this.getReplacedBody(body, receipent, otherList);
      List<Long> coreFileIdList = emailFileList.stream().map(e -> e.getFileId()).collect(Collectors.toList());
      List<CoreFileBean> coreFileList = this.fileService.selectListByIds(coreFileIdList);
      List<File> fileList = new ArrayList<>();
      coreFileList.forEach(e -> fileList.add(this.downloadFile(e)));

      try {
         if ("generic".equals(EMAIL_PROTOCOL)) {
            MailHelper.sendHtmlMail(subject, body, Arrays.asList(receipent.getReceipentEmail()), fileList.toArray(new File[0]));
         } else if ("exchange".equals(EMAIL_PROTOCOL)) {
            ExchangeMailHelper.sendHtmlMail(subject, body, Arrays.asList(receipent.getReceipentEmail()), fileList.toArray(new File[0]));
         }

         proxy.setSuccess("1");
      } catch (Exception var15) {
         proxy.setReattempt(receipent.getReattempt() + 1);
         if (StringUtils.isEmpty(proxy.getLog())) {
            proxy.setLog(ObjectUtils.getStackTrace(var15));
         } else {
            proxy.setLog(proxy.getLog() + "\n" + ObjectUtils.getStackTrace(var15));
         }
      } finally {
         if (!fileList.isEmpty()) {
            fileList.forEach(e -> FileUtils.deleteQuietly(e));
         }
      }

      this.emailReceipentService.update(proxy);
   }

   @Transactional
   @Override
   public void sendEmailForList(
      CoreEmailBean email, List<CoreEmailReceipentBean> toReceipentList, List<CoreEmailReceipentBean> ccReceipentList, List<CoreEmailFileBean> emailFileList
   ) {
      List<CoreEmailReceipentBean> updateList = new ArrayList<>();
      updateList.addAll(toReceipentList);
      updateList.addAll(ccReceipentList);
      List<String> toEmailList = toReceipentList.stream().map(m -> m.getReceipentEmail()).collect(Collectors.toList());
      List<String> ccEmailList = ccReceipentList.stream().map(m -> m.getReceipentEmail()).collect(Collectors.toList());
      List<Long> coreFileIdList = emailFileList.stream().map(e -> e.getFileId()).collect(Collectors.toList());
      List<CoreFileBean> coreFileList = this.fileService.selectListByIds(coreFileIdList);
      List<File> fileList = new ArrayList<>();
      coreFileList.forEach(e -> fileList.add(this.downloadFile(e)));

      try {
         if ("generic".equals(EMAIL_PROTOCOL)) {
            MailHelper.sendHtmlMailForList(email.getEmailSubject(), email.getEmailBody(), toEmailList, ccEmailList, fileList.toArray(new File[0]));
         } else if ("exchange".equals(EMAIL_PROTOCOL)) {
            ExchangeMailHelper.sendHtmlMailForList(email.getEmailSubject(), email.getEmailBody(), toEmailList, ccEmailList, fileList.toArray(new File[0]));
         }

         updateList.forEach(e -> e.setSuccess("1"));
      } catch (Exception var15) {
         updateList.forEach(e -> {
            e.setReattempt(e.getReattempt() + 1);
            if (StringUtils.isEmpty(e.getLog())) {
               e.setLog(ObjectUtils.getStackTrace(var15));
            } else {
               e.setLog(e.getLog() + "\n" + ObjectUtils.getStackTrace(var15));
            }
         });
      } finally {
         if (!fileList.isEmpty()) {
            fileList.forEach(e -> FileUtils.deleteQuietly(e));
         }
      }

      this.emailReceipentService.updateIfChanged(updateList);
   }

   private static final String getEmailProtocol() {
      String emailSendMethod = ApplicationContextHelper.getEnvironment().getProperty("sunway.mail.protocol.type");
      return StringUtils.isBlank(emailSendMethod) ? "generic" : emailSendMethod;
   }

   private String getReplacedText(String text, CoreEmailReceipentBean receipent) {
      Map<String, Object> map = new HashMap<>();
      map.put("receipentId", receipent.getReceipentId());
      map.put("receipentName", receipent.getReceipentName());
      map.put("receipentEmail", receipent.getReceipentEmail());
      return StringUtils.replaceBetween(text, "[{", "}]", map);
   }

   private String getReplacedSubject(String text, String receipentCategory) {
      return "receipent".equals(receipentCategory) ? text : I18nHelper.getMessage("CORE.MODULE.EMAILS.APPEND_SUBJECT_CC") + text;
   }

   private String getReplacedBody(String text, CoreEmailReceipentBean receipent, List<CoreEmailReceipentBean> otherReceipentList) {
      StringBuilder sb = new StringBuilder();
      Pair<String, String> appendReceipentAndCcPair = this.getAppendReceipentAndCc(receipent, otherReceipentList);
      sb.append(
         "<div style='MARGIN-BOTTOM:30px;BORDER-BOTTOM:#000000 1px dashed;COLOR:#333399;padding:0 0 20px 20px; font:normal bold 16px/16px 楷体,楷体_GB2312;'>"
      );
      if (!StringUtils.isEmpty(appendReceipentAndCcPair.getSecond())) {
         sb.append(I18nHelper.getMessage("CORE.MODULE.EMAILS.APPEND_BODY_WITH_CC", appendReceipentAndCcPair.getFirst(), appendReceipentAndCcPair.getSecond()));
      } else {
         sb.append(I18nHelper.getMessage("CORE.MODULE.EMAILS.APPEND_BODY_WITHOUT_CC", appendReceipentAndCcPair.getFirst()));
      }

      sb.append("</div>");
      sb.append(text);
      return sb.toString();
   }

   private Pair<String, String> getAppendReceipentAndCc(CoreEmailReceipentBean receipent, List<CoreEmailReceipentBean> otherReceipentList) {
      StringBuilder receipentBuilder = new StringBuilder();
      if ("receipent".equals(receipent.getReceipentCategory())) {
         receipentBuilder.append(receipent.getReceipentName()).append("(").append(receipent.getReceipentEmail()).append(")");
         if (!otherReceipentList.isEmpty()) {
            otherReceipentList.forEach(e -> {
               if ("receipent".equals(e.getReceipentCategory())) {
                  receipentBuilder.append("、").append(e.getReceipentName()).append("(").append(e.getReceipentEmail()).append(")");
               }
            });
            if (otherReceipentList.stream().anyMatch(f -> "cc".equals(f.getReceipentCategory()))) {
               StringBuilder ccBuilder = new StringBuilder();
               otherReceipentList.forEach(e -> {
                  if ("cc".equals(e.getReceipentCategory())) {
                     ccBuilder.append(e.getReceipentName()).append("(").append(e.getReceipentEmail()).append(")").append("、");
                  }
               });
               return Pair.of(receipentBuilder.toString(), ccBuilder.substring(0, ccBuilder.length() - 1));
            } else {
               return Pair.of(receipentBuilder.toString(), null);
            }
         } else {
            return Pair.of(receipentBuilder.toString(), null);
         }
      } else {
         otherReceipentList.forEach(e -> {
            if ("receipent".equals(e.getReceipentCategory())) {
               receipentBuilder.append(e.getReceipentName()).append("(").append(e.getReceipentEmail()).append(")").append("、");
            }
         });
         StringBuilder ccBuilder = new StringBuilder();
         ccBuilder.append(receipent.getReceipentName()).append("(").append(receipent.getReceipentEmail()).append(")");
         if (otherReceipentList.stream().anyMatch(f -> "cc".equals(f.getReceipentCategory()))) {
            otherReceipentList.forEach(e -> {
               if ("cc".equals(e.getReceipentCategory())) {
                  ccBuilder.append("、").append(e.getReceipentName()).append("(").append(e.getReceipentEmail()).append(")");
               }
            });
         }

         return StringUtils.isBlank(receipentBuilder.toString())
            ? Pair.of("", ccBuilder.toString())
            : Pair.of(receipentBuilder.substring(0, receipentBuilder.length() - 1), ccBuilder.toString());
      }
   }

   private CoreEmailTemplateBean getEmailTemplateByConfigId(String configId) {
      CoreEmailConfigBean config = this.emailConfigService.selectById(configId);
      if ("1".equals(config.getLastSuspendedFlag())) {
         return null;
      } else if (StringUtils.isEmpty(config.getTemplateId())) {
         return null;
      } else {
         CoreEmailTemplateBean template = this.emailTemplateService.selectByIdIfPresent(config.getTemplateId());
         return template == null ? null : template;
      }
   }

   private CoreEmailBean getEmailByTemplateAndConfigId(String configId, CoreEmailTemplateBean template, Map<String, Object> replacementContainer) {
      String emailSubject = StringUtils.replaceBetween(template.getEmailSubject(), "[{", "}]", replacementContainer);
      String emailBody = StringUtils.replaceBetween(template.getEmailBody(), "[{", "}]", replacementContainer);
      CoreEmailBean email = new CoreEmailBean();
      email.setId(ApplicationContextHelper.getNextIdentity());
      email.setConfigId(configId);
      email.setEmailSubject(emailSubject);
      email.setEmailBody(emailBody);
      return email;
   }

   private List<CoreEmailReceipentBean> buildEmailReceipentList(
      Long emailId, List<String> receipentIdList, List<String> ccUserIdList, List<CoreUserBean> userList
   ) {
      List<CoreEmailReceipentBean> emailReceipentList = new ArrayList<>();
      receipentIdList.forEach(e -> {
         CoreUserBean receipent = userList.stream().filter(f -> f.getId().equals(e)).findFirst().get();
         CoreEmailReceipentBean emailReceipent = new CoreEmailReceipentBean();
         emailReceipent.setId(ApplicationContextHelper.getNextIdentity());
         emailReceipent.setReceipentId(receipent.getId());
         emailReceipent.setEmailId(emailId);
         emailReceipent.setReceipentCategory("receipent");
         emailReceipent.setReceipentName(receipent.getUserName());
         emailReceipent.setReceipentEmail(receipent.getEmail());
         emailReceipentList.add(emailReceipent);
      });
      ccUserIdList.forEach(e -> {
         CoreUserBean cc = userList.stream().filter(f -> f.getId().equals(e)).findFirst().get();
         CoreEmailReceipentBean emailCc = new CoreEmailReceipentBean();
         emailCc.setId(ApplicationContextHelper.getNextIdentity());
         emailCc.setReceipentId(cc.getId());
         emailCc.setEmailId(emailId);
         emailCc.setReceipentCategory("cc");
         emailCc.setReceipentName(cc.getUserName());
         emailCc.setReceipentEmail(cc.getEmail());
         emailReceipentList.add(emailCc);
      });
      return emailReceipentList;
   }

   private File downloadFile(CoreFileBean coreFile) {
      File tempFile = FileUtils.createTempFile();

      File var4;
      try {
         File downloadFile = new File(tempFile.getParent() + File.separator + coreFile.getName());
         this.fileManager.download(coreFile, downloadFile.toPath());
         var4 = downloadFile;
      } catch (Exception var8) {
         throw new UnexpectedException(var8);
      } finally {
         if (tempFile != null) {
            FileUtils.deleteQuietly(tempFile);
         }
      }

      return var4;
   }
}
