/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.interceptor;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts(value={@Signature(type=Executor.class, method="update", args={MappedStatement.class, Object.class}), @Signature(type=Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}), @Signature(type=Executor.class, method="query", args={MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MonitorInterceptor
implements Interceptor {
    private static final List<String> READONLY_PERMITTED_CUD_TABLE_LIST = Arrays.asList("T_CORE_LOG", "T_CORE_LOGIN_LOG", "T_CORE_PAGE_GF_CONFIG", "T_CORE_PASSWORD_HIS", "T_CORE_SHORTCUT_MENU", "T_CORE_SEQUENCE", "T_CORE_WECHAT_USER");
    private static final ThreadLocal<StringBuilder> MYBATIS_MESSAGE_HOLDER = new ThreadLocal();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!this.isCudPermitted(invocation)) {
            throw new ApplicationWarningException("GIKAM.TIP.NOT_PERMITTED", true, new String[0]);
        }
        try {
            Object result;
            Object object = result = invocation.proceed();
            return object;
        }
        finally {
            MYBATIS_MESSAGE_HOLDER.remove();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties prop) {
    }

    private boolean isCudPermitted(Invocation invocation) {
        if (!"update".equals(invocation.getMethod().getName())) {
            return true;
        }
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        if (loginUser == null || !"1".equals(loginUser.getReadonly())) {
            return true;
        }
        MappedStatement ms = (MappedStatement)invocation.getArgs()[0];
        String table = null;
        switch (ms.getId()) {
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.batchInsert":
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.batchDeleteById":
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.batchUpdateByIds": {
                EntityContext entityContext = (EntityContext)((MapperMethod.ParamMap)invocation.getArgs()[1]).get("param1");
                table = entityContext.getTableName();
                break;
            }
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.insert":
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.deleteById":
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.updateById":
            case "com.sunwayworld.framework.mybatis.mapper.DaoMapper.updateByIds": {
                EntityContextInstance instance = (EntityContextInstance)((MapperMethod.ParamMap)invocation.getArgs()[1]).get("param1");
                table = instance.getContext().getTableName();
                if (!"T_CORE_USER".equals(table) || !"com.sunwayworld.framework.mybatis.mapper.DaoMapper.updateById".equals(ms.getId())) break;
                return loginUser.getId().equals(instance.getIdInstance().getValue());
            }
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.insert":
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.updateById":
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.updateByIdList": {
                TableContextInstance tableContextInstance = (TableContextInstance)((MapperMethod.ParamMap)invocation.getArgs()[1]).get("param1");
                table = tableContextInstance.getContext().getTableName();
                break;
            }
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.insertList":
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.updateItemList":
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.deleteById":
            case "com.sunwayworld.framework.mybatis.mapper.MapDaoMapper.deleteByIdList": {
                TableContext tableContext = (TableContext)((MapperMethod.ParamMap)invocation.getArgs()[1]).get("param1");
                table = tableContext.getTableName();
                break;
            }
            default: {
                return true;
            }
        }
        return table == null || READONLY_PERMITTED_CUD_TABLE_LIST.contains(table);
    }
}
