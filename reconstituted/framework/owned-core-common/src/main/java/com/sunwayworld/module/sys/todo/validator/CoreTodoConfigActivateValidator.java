package com.sunwayworld.module.sys.todo.validator;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.service.CoreTodoConfigService;
import com.sunwayworld.module.sys.todo.service.CoreTodoService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreTodoConfigActivateValidator implements DataValidator {
   @Autowired
   private CoreTodoConfigService configService;
   @Autowired
   private CoreTodoService todoService;
   private static final Logger logger = LogManager.getLogger(CoreTodoConfigActivateValidator.class);

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> idList = wrapper.parseId(Long.class);

      for (CoreTodoConfigBean config : this.configService.selectListByIds(idList)) {
         if (!this.testNoneNull(config.getName(), "T_CORE_TODO_CONFIG", "MENUID", config.getMenuId())
            || !this.testJson(config.getName(), "T_CORE_TODO_CONFIG", "JSONPARAM", config.getJsonParam())
            || !this.test(config.getName(), "T_CORE_TODO_CONFIG", "SERVICEMETHOD", config, t -> {
               if (StringUtils.isEmpty(t.getServiceMethod())) {
                  return true;
               } else {
                  String serviceMethod = t.getServiceMethod();

                  try {
                     int index = serviceMethod.lastIndexOf(".");
                     String serviceName = serviceMethod.substring(0, index);
                     String methodName = serviceMethod.substring(index + 1);
                     Class<?> serviceClazz = ClassUtils.getClass(serviceName);
                     ApplicationContextHelper.getBean(serviceClazz);
                     return ReflectionUtils.findMethod(serviceClazz, methodName, RestJsonWrapperBean.class) != null;
                  } catch (Exception var6x) {
                     return false;
                  }
               }
            }, "CORE.MODULE.SYS.TODO_CONFIGS.TIP.SERVICEMETHOD_INCORRECT")) {
            return false;
         }

         try {
            this.todoService.selectCurrentUserTodo(config);
         } catch (Exception var8) {
            logger.warn(var8.getMessage(), (Throwable)var8);
            this.addConstraintViolation("CORE.MODULE.SYS.TODO_CONFIGS.TIP.CONFIG_FAILURE");
            return false;
         }
      }

      return true;
   }
}
