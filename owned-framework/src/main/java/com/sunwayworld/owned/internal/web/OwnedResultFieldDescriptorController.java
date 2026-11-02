package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Owned Java metadata replacement for the result-field chooser. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedResultFieldDescriptorController {
    private static final String PATH = "/secure/emc/module/mdm/bizmdm/std-set-categorys/result-fields/queries/choosable";
    private final OwnedIdentityHttpBoundary boundary;

    public OwnedResultFieldDescriptorController(OwnedIdentityHttpBoundary boundary) {
        if (boundary == null) throw new IllegalArgumentException("result-field dependencies are required");
        this.boundary = boundary;
    }

    @PostMapping(path = PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ); return ok(envelope()); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (Exception failure) { return unavailable(); }
    }

    private static String envelope() throws Exception {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        try {
            Class<?> bean = Class.forName("com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean");
            BeanInfo info = Introspector.getBeanInfo(bean, Object.class);
            for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) continue;
                rows.add(descriptor(bean.getName(), descriptor.getName(), descriptor.getPropertyType() == null ? "java.lang.Object" : descriptor.getPropertyType().getName()));
            }
        } catch (ClassNotFoundException absentProjectClass) {
            for (String name : FALLBACK_FIELDS) rows.add(descriptor("com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean", name, type(name)));
        }
        Map<String,Object> result = new LinkedHashMap<String,Object>(); result.put("columnTypeMap", new LinkedHashMap<String,Object>()); result.put("first", true); result.put("last", true); result.put("numberOfElements", rows.size()); result.put("pageNumber", 1); result.put("pageSize", rows.size()); result.put("rows", rows); result.put("total", rows.size()); result.put("totalMap", new LinkedHashMap<String,Object>()); result.put("totalPages", rows.isEmpty() ? 0 : 1); return JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String,Object> descriptor(String declaringType, String name, String propertyType) { Map<String,Object> row=new LinkedHashMap<String,Object>(); row.put("declaringType",declaringType); row.put("ext$BeanProperty",false); row.put("name",name); row.put("propertyType",propertyType); row.put("validBeanProperty",true); return row; }
    private static String type(String name) { if ("id".equals(name) || name.endsWith("Id") || name.endsWith("No") || name.endsWith("Qty") || name.endsWith("Frequency") || name.endsWith("Cycle") || name.endsWith("OrderNo")) return "java.lang.Long"; if ("createdTime".equals(name)) return "java.time.LocalDateTime"; return "java.lang.String"; }
    private static final String[] FALLBACK_FIELDS = {"id","folderId","orderId","orderNo","parentOrderNo","projId","orderTaskId","cycle","frequency","testId","testMethodId","methodId","methodVersionId","itemId","itemOrderNo","itemName","itemAliasName","qcCategoryId","qcCategoryCode","qcName","scriptFlag","resultCategory","revisionRule","unitName","repeatNo","repeatQty","originalResult","reportResult","reportDisplay","resultStatus","analysisType","lowLimit","highLimit","indexLowLimit","indexHighLimit","indexLimitUnit","reason","runId","avgResult","standLowLimit","standHighLimit","standQty","submitFlag","rn1","rn2","rn3","rn4","rn5","rn6","rn7","rn8","rn9","rn10","rn11","rn12","rn13","rn14","rn15","rn16","rn17","rn18","rn19","rn20","rn21","source","mobileId","correlationValidateFlag","resultRule","convertFactor","createdById","createdByName","createdTime","createdByOrgId","createdByOrgName","sceneEquiptNo","sceneFileNo","qcDecisionCategory"};

    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);} private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");} private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.RESULT_FIELDS.UNAVAILABLE\"}");}
}
