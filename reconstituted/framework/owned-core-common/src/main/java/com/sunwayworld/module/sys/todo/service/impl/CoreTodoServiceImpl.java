package com.sunwayworld.module.sys.todo.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import com.sunwayworld.module.sys.todo.callable.SelectTodoCallable;
import com.sunwayworld.module.sys.todo.service.CoreTodoConfigService;
import com.sunwayworld.module.sys.todo.service.CoreTodoService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreTodoServiceImpl implements CoreTodoService {
   @Autowired
   private CoreMenuService menuService;
   @Autowired
   private CoreTodoConfigService configService;
   @Autowired
   private GlobalMapper globalMapper;

   @Override
   public Page<CoreTodoDTO> selectCurrentUserTodoList(String category, boolean countOnly, boolean effectiveOnly) {
      if (ApplicationContextHelper.stressTesting()) {
         return new Pagination<>();
      } else if (!LocalContextHelper.isUserLogin()) {
         return new Pagination<>();
      } else {
         List<CoreTodoConfigBean> configList = this.configService.selectActivatedConfigList();
         if (!StringUtils.isEmpty(category)) {
            configList.removeIf(c -> !category.equals(c.getCategory()));
         }

         if (configList.isEmpty()) {
            return new Pagination<>();
         } else {
            List<CoreMenuBean> menuList = this.menuService.selectCurrentUserMenuList();
            if (menuList.isEmpty()) {
               return new Pagination<>();
            } else {
               List<String> menuIdList = menuList.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
               List<CoreTodoDTO> todoList = new ArrayList<>();
               List<Future<CoreTodoDTO>> futureList = new ArrayList<>();
               List<CoreTodoConfigBean> sqlTodoConfigList = configList.stream()
                  .filter(c -> !StringUtils.isBlank(c.getSelectSql()) && menuIdList.contains(c.getMenuId()))
                  .collect(Collectors.toList());
               if (!sqlTodoConfigList.isEmpty()) {
                  List<String> sqlList = new ArrayList<>();
                  sqlTodoConfigList.forEach(c -> sqlList.add(this.getSelectTodoSql(c.getId(), c.getSelectSql())));

                  for (Map<String, Object> map : this.globalMapper.selectListBySqlListUnionAll(sqlList)) {
                     Integer qty = map.entrySet()
                        .stream()
                        .filter(e -> !"ID".equalsIgnoreCase(e.getKey()))
                        .map(e -> NumberUtils.parseInt(e.getValue()))
                        .findAny()
                        .get();
                     if (qty > 0) {
                        Long configId = map.entrySet()
                           .stream()
                           .filter(e -> "ID".equalsIgnoreCase(e.getKey()))
                           .map(e -> NumberUtils.parseLong(e.getValue()))
                           .findAny()
                           .get();
                        CoreTodoConfigBean config = sqlTodoConfigList.stream().filter(c -> configId.equals(c.getId())).findAny().get();
                        CoreTodoDTO todo = new CoreTodoDTO();
                        todo.setOrderNo(config.getOrderNo());
                        todo.setVoiceWarning(config.getVoiceWarning());
                        todo.setQty(qty);
                        todo.setMenuId(config.getMenuId());
                        todo.setModelName(StringUtils.isEmpty(config.getI18nCode()) ? config.getName() : config.getI18nCode());
                        CoreMenuBean menu = menuList.stream().filter(m -> m.getId().equals(config.getMenuId())).findAny().orElse(null);
                        if (menu != null) {
                           todo.setHref(this.menuService.getReplacedHref(menu));
                           if (!StringUtils.isEmpty(config.getIcon())) {
                              todo.setExt$Item("todoIconUrl", "/static/icon/todo/" + config.getIcon());
                           }

                           todoList.add(todo);
                        }
                     }
                  }
               }

               StringBuilder sb = new StringBuilder();
               List<CoreTodoConfigBean> asyncTodoConfigList = configList.stream()
                  .filter(c -> StringUtils.isBlank(c.getSelectSql()) && menuIdList.contains(c.getMenuId()))
                  .collect(Collectors.toList());
               if (!asyncTodoConfigList.isEmpty()) {
                  for (CoreTodoConfigBean config : asyncTodoConfigList) {
                     CoreMenuBean menu = menuList.stream().filter(m -> m.getId().equals(config.getMenuId())).findAny().orElse(null);
                     if (menu != null) {
                        String jsonParam = config.getJsonParam();
                        if (!StringUtils.isEmpty(jsonParam)) {
                           config.setJsonParam(this.getReplacedParam(jsonParam));
                        }

                        SelectTodoCallable selectable = new SelectTodoCallable(config, menu);
                        selectable.setCountOnly(countOnly);
                        selectable.setEffectiveOnly(effectiveOnly);
                        futureList.add(ExecutorHelper.submit(selectable));
                     }
                  }

                  for (int i = 0; i < asyncTodoConfigList.size(); i++) {
                     Future<CoreTodoDTO> future = futureList.get(i);

                     try {
                        if (future.get() != null) {
                           todoList.add(future.get());
                        }
                     } catch (Exception var19) {
                        CoreTodoConfigBean configx = asyncTodoConfigList.get(i);
                        if (sb.length() > 0) {
                           sb.append("\n");
                        }

                        sb.append("待办（" + configx.getName() + "）查询报错:" + var19.getMessage());
                     }
                  }
               }

               if (sb.length() > 0) {
                  ExecutorHelper.submit(() -> {
                     throw new ApplicationRuntimeException(sb.toString());
                  });
               }

               CollectionUtils.sort(todoList, new Comparator<CoreTodoDTO>() {
                  public int compare(CoreTodoDTO t1, CoreTodoDTO t2) {
                     Long orderNo1 = t1.getOrderNo() == null ? 2147483647L : t1.getOrderNo();
                     Long orderNo2 = t2.getOrderNo() == null ? 2147483647L : t2.getOrderNo();
                     return NumberUtils.parseInt(orderNo1 - orderNo2);
                  }
               });
               return new Pagination<>(todoList);
            }
         }
      }
   }

   @Override
   public List<Map<String, Object>> selectCurrentUserMobileTodoList(RestJsonWrapperBean wrapperBean) {
      if (ApplicationContextHelper.stressTesting()) {
         return CollectionUtils.emptyList();
      } else if (!LocalContextHelper.isUserLogin()) {
         return CollectionUtils.emptyList();
      } else {
         List<CoreTodoConfigBean> configList = this.configService.selectActivatedConfigList();
         if (!StringUtils.isEmpty(wrapperBean.getParamValue("category"))) {
            configList.removeIf(c -> !wrapperBean.getParamValue("category").equals(c.getCategory()));
         }

         if (configList.isEmpty()) {
            return CollectionUtils.emptyList();
         } else {
            List<CoreMenuBean> menuList = this.menuService.selectCurrentUserMenuList();
            if (menuList.isEmpty()) {
               return CollectionUtils.emptyList();
            } else {
               List<String> menuIdList = menuList.stream().map(m -> m.getId()).distinct().collect(Collectors.toList());
               List<CoreTodoConfigBean> mobileSqlTodoConfigList = configList.stream()
                  .filter(c -> !StringUtils.isBlank(c.getMobileSelectSql()) && menuIdList.contains(c.getMenuId()))
                  .collect(Collectors.toList());
               if (!mobileSqlTodoConfigList.isEmpty()) {
                  List<String> sqlList = new ArrayList<>();
                  mobileSqlTodoConfigList.forEach(c -> sqlList.add(this.getSelectMobileTodoSql(c.getId(), c.getName(), c.getMobileSelectSql())));
                  MapperParameter parameter = wrapperBean.extractMapFilter();
                  parameter.put("sqlList", sqlList);
                  return this.globalMapper.selectListBySqlListUnionAllAppendWhere(parameter);
               } else {
                  return CollectionUtils.emptyList();
               }
            }
         }
      }
   }

   @Override
   public CoreTodoDTO selectCurrentUserTodo(CoreTodoConfigBean config) {
      CoreMenuBean menu = this.menuService.selectById(config.getMenuId());
      if (!StringUtils.isBlank(config.getSelectSql())) {
         CoreTodoDTO todo = new CoreTodoDTO();
         todo.setOrderNo(config.getOrderNo());
         todo.setVoiceWarning(config.getVoiceWarning());
         todo.setQty(NumberUtils.parseInt(this.globalMapper.selectOneString(this.getReplacedParam(config.getSelectSql()))));
         todo.setMenuId(config.getMenuId());
         todo.setModelName(StringUtils.isEmpty(config.getI18nCode()) ? config.getName() : config.getI18nCode());
         todo.setHref(this.menuService.getReplacedHref(menu));
         if (!StringUtils.isEmpty(config.getIcon())) {
            todo.setExt$Item("todoIconUrl", "/static/icon/todo/" + config.getIcon());
         }

         return todo;
      } else {
         return new SelectTodoCallable(config, menu).call();
      }
   }

   @Override
   public Page<CoreTodoDTO> selectTodoListByUser(String userId) {
      Page var3;
      try {
         LoginUser loginUser = new LoginUser(userId, null);
         LocalContextHelper.setUserLogin(loginUser);
         var3 = this.selectCurrentUserTodoList(null, true, true);
      } finally {
         LocalContextHelper.removeUserLogin();
      }

      return var3;
   }

   @Override
   public String getReplacedParam(String param) {
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

   private String getSelectTodoSql(Long configId, String sql) {
      return "SELECT " + configId + " ID, " + this.getReplacedParam(StringUtils.removeStartIgnoreCase(sql.trim(), "SELECT"));
   }

   private String getSelectMobileTodoSql(Long configId, String configName, String sql) {
      return "SELECT "
         + configId
         + " CONFIGID, '"
         + configName
         + "' TODONAME, "
         + this.getReplacedParam(StringUtils.removeStartIgnoreCase(sql.trim(), "SELECT"));
   }
}
