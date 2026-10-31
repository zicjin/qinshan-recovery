package com.sunwayworld.module.sys.todo.callable;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import com.sunwayworld.module.sys.todo.bean.CoreTodoItemDTO;

public class SelectTodoCallable implements Callable<CoreTodoDTO> {
    private static final Logger logger = LogManager.getLogger(SelectTodoCallable.class);
    
    private static final ExpressionParser parser = new SpelExpressionParser();

    private final CoreTodoConfigBean config;
    private final String href;
    private final RestJsonWrapperBean wrapper;
    private final LoginUser loginUser;
    private boolean countOnly = true; // 是否只查询项数
    private boolean effectiveOnly = true; // 是否只显示有效待办（待办数>0）

    public SelectTodoCallable(CoreTodoConfigBean config) {
        this(config, ApplicationContextHelper.getBean(CoreMenuService.class).selectById(config.getMenuId()));
    }

    public SelectTodoCallable(CoreTodoConfigBean config, CoreMenuBean menu) {
        this.config = config;
        this.loginUser = ObjectUtils.clone(LocalContextHelper.getLoginPrincipal());

        this.wrapper = new RestJsonWrapperBean();
        // JSON格式的参数
        String jsonParam = config.getJsonParam();
        if (!StringUtils.isEmpty(jsonParam)) {
            JSON.parseObject(getReplacedParam(jsonParam)).forEach((k, v) -> wrapper.setFilterValue(k, v));
        }

        CoreMenuService menuService = ApplicationContextHelper.getBean(CoreMenuService.class);
        href = menuService.getReplacedHref(menu);
        if (Constant.YES.equals(config.getInheritParam()) && StringUtils.contains(href, "?")) {
            String params = href.substring(href.indexOf("?") + 1);

            for (String param : StringUtils.split(params, "&")) {
                String[] values = StringUtils.split(param, "=");

                wrapper.setFilterValue(values[0], (values.length > 1 ? values[1] : null));
            }
        }
    }

    public CoreTodoConfigBean getConfig() {
        return this.config;
    }

    public boolean isEffectiveOnly() {
        return effectiveOnly;
    }

    public void setEffectiveOnly(boolean effectiveOnly) {
        this.effectiveOnly = effectiveOnly;
    }

    public boolean isCountOnly() {
        return countOnly;
    }

    public void setCountOnly(boolean countOnly) {
        this.countOnly = countOnly;
    }

    @Override
    public CoreTodoDTO call() {
        long now = System.currentTimeMillis();
        
        try {
            LocalContextHelper.setUserLogin(loginUser); // 模拟用户登录

            if (isCountOnly()) { // 只查询总项数
            	// LocalContextHelper.setSelectCountOnly();
            }

            CoreTodoDTO todo = new CoreTodoDTO();
            todo.setOrderNo(config.getOrderNo());
            String serviceMethod = config.getServiceMethod();
            if (!StringUtils.isEmpty(serviceMethod)) {
                int index = serviceMethod.lastIndexOf(".");
                String serviceName = serviceMethod.substring(0, index);
                String methodName = serviceMethod.substring(index + 1);

                Class<?> serviceClazz = ClassUtils.getClass(serviceName);

                Object service = ApplicationContextHelper.getBean(serviceClazz);

                Method method = ReflectionUtils.findMethod(serviceClazz, methodName, RestJsonWrapperBean.class);

                Page<?> page = (Page<?>) ReflectionUtils.invokeMethod(method, service, wrapper);

                todo.setQty(page.getTotal());

                setItemListIfNecessary(todo, page);
            } else {
                String restfulAPI = href.substring(0, href.indexOf("/page/"));

                GenericService<?, ?> service = GikamBeanContext.getServiceByRESTfulAPI(restfulAPI);

                if (StringUtils.startsWith(href, restfulAPI + "/page/edit-list")) {
                    Page<?> page = service.selectRawPagination(wrapper);

                    todo.setQty(page.getTotal());

                    setItemListIfNecessary(todo, page);
                } else if (StringUtils.startsWith(href, restfulAPI + "/page/audit-list")) {
                    Page<?> page = ((GenericAuditableService<?, ?>)service).selectAuditablePagination(wrapper);

                    todo.setQty(page.getTotal());

                    setItemListIfNecessary(todo, page);
                } else {
                    throw new UnsupportedException();
                }
            }

            if (isEffectiveOnly() && todo.getQty() < 1) {
                return null;
            }

            todo.setMenuId(config.getMenuId());
            todo.setModelName(config.getName());
            todo.setHref(href);

            // 设置待办的icon
            if (!StringUtils.isEmpty(config.getIcon())) {
                todo.setExt$Item("todoIconUrl", Constant.SYSTEM_STYLE_TODOICON_PATH + config.getIcon());
            }

            return todo;
        } finally {
            if (System.currentTimeMillis() - now > 2000) {
                StringBuilder sb = new StringBuilder("待办查询时间超过2秒，请检查下原因：")
                        .append("待办的ID：").append(config.getId()+config.getName())
                        .append("查询人员：").append(loginUser.getUsername())
                        .append("查询耗时：").append((System.currentTimeMillis() - now));
                
                logger.warn(sb.toString());
            }
            
            LocalContextHelper.removeUserLogin(); // 清除用户模拟登录信息
            // LocalContextHelper.removeSelectCountOnly(); // 清除只查询项数的标记
        }
    }

    //-----------------------------------------------------------------------------
    // 私有方法
    //-----------------------------------------------------------------------------
    private String getReplacedParam(String param) {
        if (StringUtils.contains(param, "${loginUserId}")) {
            param = StringUtils.replace(param, "${loginUserId}", LocalContextHelper.getLoginUserId());
        }
        if (StringUtils.contains(param, "${loginOrgId}")) {
            param = StringUtils.replace(param, "${loginOrgId}", LocalContextHelper.getLoginOrgId());
        }
        if (StringUtils.contains(param, "${loginRoleId}")) {
            param = StringUtils.replace(param, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
        }

        return param;
    }

    private void setItemListIfNecessary(CoreTodoDTO todo, Page<?> page) {
        if (!isCountOnly()
                && !page.getRows().isEmpty()) {
            todo.setItemList(page.getRows().stream().map(i -> parseTodoItem(i, config.getDescExpression())).collect(Collectors.toList()));
        }
    }

    private CoreTodoItemDTO parseTodoItem(Object item, String descExpression) {
        CoreTodoItemDTO todoItem = new CoreTodoItemDTO();
        todoItem.setId(ObjectUtils.toString(ReflectionUtils.getFieldValue(item, "id")));
        todoItem.setCreatedById((String) ReflectionUtils.getFieldValue(item, "createdById"));
        todoItem.setCreatedByName((String) ReflectionUtils.getFieldValue(item, "createdByName"));
        todoItem.setCreatedTime((LocalDateTime) ReflectionUtils.getFieldValue(item, "createdTime"));
        todoItem.setCreatedByOrgId((String) ReflectionUtils.getFieldValue(item, "createdByOrgId"));
        todoItem.setCreatedByOrgName((String) ReflectionUtils.getFieldValue(item, "createdByOrgName"));

        EvaluationContext context= new StandardEvaluationContext();
        context.setVariable("item", item);
        Expression exp = parser.parseExpression(descExpression);
        todoItem.setDesc((String) exp.getValue(context));

        return todoItem;
    }
}
