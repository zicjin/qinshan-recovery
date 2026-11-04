package com.sunwayworld.module.sys.todo.callable;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.executor.core.ThreadPriority;
import com.sunwayworld.framework.executor.core.TraceCallable;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import com.sunwayworld.module.sys.todo.bean.CoreTodoItemDTO;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SelectTodoCallable extends TraceCallable<CoreTodoDTO> {
   private static final ExpressionParser parser = new SpelExpressionParser();
   private final CoreTodoConfigBean config;
   private final String href;
   private final RestJsonWrapperBean wrapper;
   private final LoginUser loginUser;
   private boolean countOnly = true;
   private boolean effectiveOnly = true;

   public SelectTodoCallable(CoreTodoConfigBean config) {
      this(config, ApplicationContextHelper.getBean(CoreMenuService.class).selectById(config.getMenuId()));
   }

   public SelectTodoCallable(CoreTodoConfigBean config, CoreMenuBean menu) {
      super(config.getId() + "[" + config.getName() + "]", ThreadPriority.BelowNormal);
      this.config = config;
      this.loginUser = ObjectUtils.clone(LocalContextHelper.getLoginPrincipal());
      this.wrapper = new RestJsonWrapperBean();
      String jsonParam = config.getJsonParam();
      if (!StringUtils.isEmpty(jsonParam)) {
         JSON.parseObject(jsonParam).forEach((k, v) -> this.wrapper.setFilterValue(k, v));
      }

      CoreMenuService menuService = ApplicationContextHelper.getBean(CoreMenuService.class);
      this.href = menuService.getReplacedHref(menu);
      if ("1".equals(config.getInheritParam()) && StringUtils.contains(this.href, "?")) {
         String params = this.href.substring(this.href.indexOf("?") + 1);

         for (String param : StringUtils.split(params, "&")) {
            String[] values = StringUtils.split(param, "=");
            this.wrapper.setFilterValue(values[0], values.length > 1 ? values[1] : null);
         }
      }
   }

   public CoreTodoConfigBean getConfig() {
      return this.config;
   }

   public boolean isEffectiveOnly() {
      return this.effectiveOnly;
   }

   public void setEffectiveOnly(boolean effectiveOnly) {
      this.effectiveOnly = effectiveOnly;
   }

   public boolean isCountOnly() {
      return this.countOnly;
   }

   public void setCountOnly(boolean countOnly) {
      this.countOnly = countOnly;
   }

   public CoreTodoDTO call() {
      Object var15;
      try {
         LocalContextHelper.setUserLogin(this.loginUser);
         if (this.isCountOnly()) {
            MybatisPageHelper.setCountOnly();
         }

         CoreTodoDTO todo = new CoreTodoDTO();
         todo.setOrderNo(this.config.getOrderNo());
         todo.setVoiceWarning(this.config.getVoiceWarning());
         String serviceMethod = this.config.getServiceMethod();
         if (!StringUtils.isEmpty(serviceMethod)) {
            int index = serviceMethod.lastIndexOf(".");
            String serviceName = serviceMethod.substring(0, index);
            String methodName = serviceMethod.substring(index + 1);
            Class<?> serviceClazz = ClassUtils.getClass(serviceName);
            Object service = ApplicationContextHelper.getBean(serviceClazz);
            Method method = ReflectionUtils.findMethod(serviceClazz, methodName, RestJsonWrapperBean.class);
            Page<?> page = (Page<?>)ReflectionUtils.invokeMethod(method, service, this.wrapper);
            todo.setQty(page.getTotal());
            this.setItemListIfNecessary(todo, page);
         } else if (!StringUtils.isEmpty(this.config.getLcdpServiceMethod())) {
            Page<?> page = (Page<?>)SpringUtils.invoke("lcdpResourceServiceImpl", "callTodoScript", this.config.getLcdpServiceMethod(), this.wrapper);
            todo.setQty(page.getTotal());
            this.setItemListIfNecessary(todo, page);
         } else {
            String restfulAPI = this.href.substring(0, this.href.indexOf("/page/"));
            GenericService<?, ?> service = GikamBeanContext.getServiceByRESTfulAPI(restfulAPI);
            if (StringUtils.startsWith(this.href, restfulAPI + "/page/edit-list")) {
               Page<?> page = service.selectRawPagination(this.wrapper);
               todo.setQty(page.getTotal());
               this.setItemListIfNecessary(todo, page);
            } else {
               if (!StringUtils.startsWith(this.href, restfulAPI + "/page/audit-list")) {
                  throw new UnsupportedException(this.href);
               }

               Page<?> page = ((GenericAuditableService)service).selectAuditablePagination(this.wrapper);
               todo.setQty(page.getTotal());
               this.setItemListIfNecessary(todo, page);
            }
         }

         if (!this.isEffectiveOnly() || todo.getQty() >= 1) {
            todo.setMenuId(this.config.getMenuId());
            todo.setModelName(StringUtils.isEmpty(this.config.getI18nCode()) ? this.config.getName() : this.config.getI18nCode());
            todo.setHref(this.href);
            if (!StringUtils.isEmpty(this.config.getIcon())) {
               todo.setExt$Item("todoIconUrl", "/static/icon/todo/" + this.config.getIcon());
            }

            return todo;
         }

         var15 = null;
      } finally {
         LocalContextHelper.removeUserLogin();
         MybatisPageHelper.removeCountOnly();
      }

      return (CoreTodoDTO)var15;
   }

   private void setItemListIfNecessary(CoreTodoDTO todo, Page<?> page) {
      if (!this.isCountOnly() && !page.getRows().isEmpty()) {
         todo.setItemList(page.getRows().stream().map(i -> this.parseTodoItem(i, this.config.getDescExpression())).collect(Collectors.toList()));
      }
   }

   private CoreTodoItemDTO parseTodoItem(Object item, String descExpression) {
      CoreTodoItemDTO todoItem = new CoreTodoItemDTO();
      todoItem.setId(ObjectUtils.toString(ReflectionUtils.getFieldValue(item, "id")));
      todoItem.setCreatedById((String)ReflectionUtils.getFieldValue(item, "createdById"));
      todoItem.setCreatedByName((String)ReflectionUtils.getFieldValue(item, "createdByName"));
      todoItem.setCreatedTime((LocalDateTime)ReflectionUtils.getFieldValue(item, "createdTime"));
      todoItem.setCreatedByOrgId((String)ReflectionUtils.getFieldValue(item, "createdByOrgId"));
      todoItem.setCreatedByOrgName((String)ReflectionUtils.getFieldValue(item, "createdByOrgName"));
      EvaluationContext context = new StandardEvaluationContext();
      context.setVariable("item", item);
      Expression exp = parser.parseExpression(descExpression);
      todoItem.setDesc((String)exp.getValue(context));
      return todoItem;
   }
}
