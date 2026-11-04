package com.sunwayworld.module.sys.attorney.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyBean;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyDTO;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import com.sunwayworld.module.sys.attorney.dao.CoreAttorneyDao;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyMenuService;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyService;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import com.sunwayworld.module.sys.todo.service.CoreTodoService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAttorneyServiceImpl implements CoreAttorneyService {
   private static final Logger logger = LogManager.getLogger(CoreAttorneyService.class);
   @Autowired
   private CoreAttorneyDao attorneyDao;
   @Lazy
   @Autowired
   private CoreAttorneyMenuService attorneyMenuService;
   @Lazy
   @Autowired
   private CoreTodoService todoService;
   @Lazy
   @Autowired
   private CoreUserService userService;

   public CoreAttorneyDao getDao() {
      return this.attorneyDao;
   }

   public Long insert(RestJsonWrapperBean wrapper) {
      CoreAttorneyBean attorney = wrapper.parseUnique(CoreAttorneyBean.class);
      if (StringUtils.isBlank(attorney.getTrustor())) {
         attorney.setTrustor(LocalContextHelper.getLoginUserId());
      } else if (!LocalContextHelper.isAdminLogin() && !LocalContextHelper.getLoginUserId().equals(attorney.getTrustor())) {
         throw new ApplicationWarningException("CORE.MODULE.SYS.T_CORE_ATTORNEY.CANNOT_CHANGE_TRUSTOR");
      }

      attorney.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(attorney);
      return attorney.getId();
   }

   @Override
   public Page<CoreAttorneyBean> selectPagination(MapperParameter parameter, PageRowBounds rowBounds) {
      if (!LocalContextHelper.isAdminLogin()) {
         parameter.setFilter(SearchFilter.instance().match(new String[]{"TRUSTOR", "TRUSTEE"}, LocalContextHelper.getLoginUserId()).filter(MatchPattern.OR));
      }

      if (parameter.isRawQueries()) {
         parameter.setFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "0").filter(MatchPattern.EQ));
      } else {
         parameter.setFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ));
      }

      return this.selectPagination(() -> this.getDao().selectByCondition(parameter), rowBounds);
   }

   @Override
   public Page<CoreAttorneyMenuBean> selectAttorneyMenuPagination(Long id, RestJsonWrapperBean wrapper) {
      wrapper.addExtStdFilter("ATTORNEYID", id, MatchPattern.EQ);
      return this.attorneyMenuService.selectPagination(wrapper);
   }

   @Transactional
   @Override
   public void insertAttorneyMenus(Long id, RestJsonWrapperBean wrapper) {
      List<CoreAttorneyMenuBean> attorneyMenuList = wrapper.parse(CoreAttorneyMenuBean.class);
      CoreAttorneyMenuBean menuFilter = new CoreAttorneyMenuBean();
      menuFilter.setAttorneyId(id);
      List<CoreAttorneyMenuBean> existingAttorneyMenuList = this.attorneyMenuService.selectList(menuFilter, new Order[0]);
      attorneyMenuList.removeIf(m -> existingAttorneyMenuList.stream().anyMatch(e -> e.getMenuId().equals(m.getMenuId())));
      attorneyMenuList.forEach(m -> {
         m.setId(ApplicationContextHelper.getNextIdentity());
         m.setAttorneyId(id);
      });
      this.attorneyMenuService.getDao().insert(attorneyMenuList);
   }

   @Transactional
   @Override
   public void deleteAttorneyMenus(Long id, RestJsonWrapperBean wrapper) {
      List<CoreAttorneyMenuBean> attorneyMenuList = wrapper.parse(CoreAttorneyMenuBean.class);
      this.attorneyMenuService.getDao().deleteBy(attorneyMenuList);
   }

   @Override
   public LoginUser makeTrustorLoginUser(String trustorId) {
      if (LocalContextHelper.isTrusteeLogin()) {
         throw new ApplicationWarningException("CORE.MODULE.SYS.ATTORNEYS.TIP.UNABLE_SWITCH_IF_SWITCHED");
      } else {
         List<Long> attorneyIdList = this.getDao().selectExecutableAttorneyIdList(trustorId, LocalContextHelper.getLoginUserId());
         if (CollectionUtils.isEmpty(attorneyIdList)) {
            throw new ApplicationWarningException("CORE.MODULE.SYS.ATTORNEYS.TIP.UNABLE_SWITCH_IF_NOT_TRUSTOR");
         } else {
            LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
            LoginUser trustorUser = new LoginUser(trustorId, loginUser.getPassword());
            trustorUser.setOrgId(loginUser.getOrgId());
            trustorUser.setOrgName(loginUser.getOrgName());
            trustorUser.setRoleId(loginUser.getRoleId());
            trustorUser.setRoleName(loginUser.getRoleName());
            trustorUser.setTrustee(loginUser);
            return trustorUser;
         }
      }
   }

   @Override
   public void switchTrustor(String trustorId) {
      LoginUser trustorUser = this.makeTrustorLoginUser(trustorId);
      String token = AuthenticationHelper.getLoginUserToken();
      Authentication authentication = AuthenticationHelper.getCacheAuthentication(token);
      UsernamePasswordAuthenticationToken trustorAuthentication = new UsernamePasswordAuthenticationToken(trustorUser, authentication.getCredentials());
      AuthenticationHelper.updateCacheAuthentication(token, trustorAuthentication);
      logger.info("TRUSTEE: " + trustorUser.getTrustee().getUsername() + " switched to TRUSTOR: " + trustorId);
   }

   @Override
   public void switchTrustee() {
      LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
      if (loginUser.getTrustee() == null) {
         throw new ApplicationWarningException("CORE.MODULE.SYS.ATTORNEYS.TIP.UNABLE_SWITCH_IF_NOT_TRUSTEE");
      } else {
         String token = AuthenticationHelper.getLoginUserToken();
         Authentication authentication = AuthenticationHelper.getCacheAuthentication(token);
         UsernamePasswordAuthenticationToken trustorAuthentication = new UsernamePasswordAuthenticationToken(
            loginUser.getTrustee(), authentication.getCredentials()
         );
         AuthenticationHelper.updateCacheAuthentication(token, trustorAuthentication);
         logger.info("TRUSTOR: " + loginUser.getUsername() + " switched back to TRUSTEE: " + loginUser.getTrustee().getUsername());
      }
   }

   @Override
   public List<String> selectEntrustedMenuIdList() {
      LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
      if (loginUser.getTrustee() == null) {
         throw new ApplicationWarningException("CORE.MODULE.SYS.ATTORNEYS.TIP.UNABLE_GET_ENTRUST_MENU_IF_NOT_SWITCHED_TRUSTEE");
      } else {
         List<Long> attorneyIdList = this.getDao().selectExecutableAttorneyIdList(loginUser.getUsername(), loginUser.getTrustee().getUsername());
         return attorneyIdList.stream()
            .map(i -> this.attorneyMenuService.selectListByAttoryneyId(i).stream().map(m -> m.getMenuId()).collect(Collectors.toList()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
      }
   }

   @Override
   public List<CoreAttorneyDTO> selectAttorneyTodoList() {
      if (ApplicationContextHelper.stressTesting()) {
         return CollectionUtils.emptyList();
      } else {
         LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
         if (loginUser.getTrustee() != null) {
            throw new ApplicationWarningException("CORE.MODULE.SYS.ATTORNEYS.TIP.UNABLE_GET_TRUSTOR_TODOLIST_IF_NOT_TRUSTEE");
         } else {
            List<CoreAttorneyBean> executableAttorneyList = this.getDao().selectExecutableAttorneyList(LocalContextHelper.getLoginUserId());
            List<String> trustorIdList = executableAttorneyList.stream().map(CoreAttorneyBean::getTrustor).distinct().collect(Collectors.toList());
            List<CoreAttorneyDTO> attorneyList = new ArrayList<>();

            for (String trustorId : trustorIdList) {
               Page<CoreTodoDTO> todoList = this.todoService.selectTodoList(this.makeTrustorLoginUser(trustorId), null, true, true);
               CoreAttorneyDTO attorney = new CoreAttorneyDTO();
               attorney.setTrustor(trustorId);
               attorney.setTrustorName(this.userService.selectById(trustorId).getUserName());
               attorney.setTodoQty(todoList.getRows().stream().mapToInt(CoreTodoDTO::getQty).sum());
               attorneyList.add(attorney);
            }

            return attorneyList;
         }
      }
   }
}
