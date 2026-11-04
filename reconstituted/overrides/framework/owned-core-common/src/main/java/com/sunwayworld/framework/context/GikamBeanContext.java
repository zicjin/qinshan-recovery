/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

public class GikamBeanContext {
    private final Map<String, GenericService<?, ?>> TABLE_AWARE_SERVICE_CONTAINER = new CaseInsensitiveLinkedMap();
    private final Map<String, GenericService<?, ?>> RESTFULAPI_AWARE_SERVICE_CONTAINER = new CaseInsensitiveLinkedMap();
    private final Map<String, GenericCloudResource<?, ?>> TABLE_AWARE_CLIENT_CONTAINER = new CaseInsensitiveLinkedMap();
    private static volatile GikamBeanContext context;

    private GikamBeanContext() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void init() {
        if (context != null) return;
        Thread.currentThread().setContextClassLoader(DynamicClassLoader.getInstance());
        Class<GikamBeanContext> clazz = GikamBeanContext.class;
        synchronized (GikamBeanContext.class) {
            if (context != null) return;
            context = new GikamBeanContext();
            context.extranct(Repository.class);
            context.extranct(Service.class);
            context.extranct(Component.class);
            context.extranct(RequestMapping.class);
            context.extranct(FeignClient.class);
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static <T extends GenericCloudResource<?, ?>> T getFeignClientByTable(String table) {
        if (context != null) {
            return (T)GikamBeanContext.context.TABLE_AWARE_CLIENT_CONTAINER.get(table);
        }
        return null;
    }

    public static <T extends GenericService<?, ?>> T getServiceByTable(String table) {
        if (context != null) {
            return (T)GikamBeanContext.context.TABLE_AWARE_SERVICE_CONTAINER.get(table);
        }
        return null;
    }

    public static <T extends GenericService<?, ?>> T getServiceByRESTfulAPI(String restAPI) {
        if (context != null) {
            return (T)GikamBeanContext.context.RESTFULAPI_AWARE_SERVICE_CONTAINER.get(restAPI);
        }
        return null;
    }

    private void extranct(Class<? extends Annotation> annotationType) {
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(annotationType);
        map.forEach((k, v) -> {
            block5: {
                String table;
                Class<?> clazz;
                block8: {
                    GenericCloudResource genericCloudResource;
                    block9: {
                        block7: {
                            block4: {
                                String table2;
                                GenericService service;
                                block6: {
                                    clazz = ClassUtils.getRawType(v.getClass());
                                    if (!GenericService.class.isAssignableFrom(clazz)) break block4;
                                    service = (GenericService)v;
                                    if (service.getDao() == null) {
                                        return;
                                    }
                                    table2 = service.getDao().getTable();
                                    if (StringUtils.isEmpty(table2)) break block5;
                                    if (this.TABLE_AWARE_SERVICE_CONTAINER.containsKey(table2)) break block6;
                                    this.TABLE_AWARE_SERVICE_CONTAINER.put(table2, service);
                                    break block5;
                                }
                                GenericService<?, ?> oldService = this.TABLE_AWARE_SERVICE_CONTAINER.get(table2);
                                Class<?> oldClazz = ClassUtils.getRawType(oldService.getClass());
                                if (!clazz.isAssignableFrom(oldClazz)) break block5;
                                this.TABLE_AWARE_SERVICE_CONTAINER.put(table2, service);
                                break block5;
                            }
                            if (!GenericResource.class.isAssignableFrom(clazz)) break block7;
                            RequestMapping requestMapping = ReflectionUtils.getAnnotation(clazz, RequestMapping.class);
                            GenericResource resource = (GenericResource)v;
                            if (requestMapping == null) break block5;
                            for (String value : requestMapping.value()) {
                                this.RESTFULAPI_AWARE_SERVICE_CONTAINER.put(value, (GenericService<?, ?>)resource.getService());
                            }
                            break block5;
                        }
                        if (!GenericCloudResource.class.isAssignableFrom(clazz)) break block5;
                        genericCloudResource = (GenericCloudResource)v;
                        Class<?> persistableClass = ResolvableType.forClass(genericCloudResource.getClass()).as(GenericCloudResource.class).getGeneric(0).resolve();
                        table = PersistableMetadataHelper.getTableName(persistableClass);
                        if (!FeignClient.class.equals((Object)annotationType)) break block8;
                        if (this.TABLE_AWARE_CLIENT_CONTAINER.containsKey(table)) break block9;
                        this.TABLE_AWARE_CLIENT_CONTAINER.put(table, genericCloudResource);
                        break block5;
                    }
                    GenericCloudResource<?, ?> oldGenericCloudResource = this.TABLE_AWARE_CLIENT_CONTAINER.get(table);
                    Class<?> oldClazz = ClassUtils.getRawType(oldGenericCloudResource.getClass());
                    if (!clazz.isAssignableFrom(oldClazz)) break block5;
                    this.TABLE_AWARE_CLIENT_CONTAINER.put(table, genericCloudResource);
                    break block5;
                }
                if (RequestMapping.class.equals((Object)annotationType)) {
                    RequestMapping requestMapping = ReflectionUtils.getAnnotation(clazz, RequestMapping.class);
                    for (String value : requestMapping.value()) {
                        this.RESTFULAPI_AWARE_SERVICE_CONTAINER.put(value, this.TABLE_AWARE_SERVICE_CONTAINER.get(table));
                    }
                }
            }
        });
    }
}
