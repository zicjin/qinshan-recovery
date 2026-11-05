package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Isolated owned write seam for the original reagent and composition routes. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedReagentWriteController {
    // The original project resource owns the /secure/emc namespace. This
    // source-only seam supplies only the missing /secure/core boundary alias;
    // registering both namespaces creates an ambiguous Spring mapping when
    // the original project is present in the same application.
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    private final Environment environment;

    public OwnedReagentWriteController(OwnedIdentityHttpBoundary boundary, DataSource dataSource,
            Environment environment) {
        if (boundary == null || dataSource == null || environment == null)
            throw new IllegalArgumentException("reagent write dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource; this.environment = environment;
    }

    @PostMapping(path = "/secure/core/module/bp/reagents",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insert(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request);
            JSONObject values = payload(body);
            long id = OwnedWriteSupport.transaction(dataSource, c -> insertReagent(c, values, principal));
            boundary.recordAllowed("reagent-insert", principal, String.valueOf(id), "isolated-write");
            return ok("{\"id\":" + id + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PutMapping(path = "/secure/core/module/bp/reagents/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); long valid = positiveId(id);
            int updated = OwnedWriteSupport.transaction(dataSource,
                    c -> updateReagent(c, valid, payload(body), principal));
            boundary.recordAllowed("reagent-update", principal, String.valueOf(valid), "isolated-write");
            return ok("{\"updated\":" + updated + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @DeleteMapping(path = "/secure/core/module/bp/reagents",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); List<Long> ids = ids(body);
            int deleted = OwnedWriteSupport.transaction(dataSource, c -> deleteReagents(c, ids, principal));
            boundary.recordAllowed("reagent-delete", principal, ids.toString(), "isolated-write");
            return ok("{\"deleted\":" + deleted + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/bp/reagents/{id}/compositions",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> insertComposition(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); long reagentId = positiveId(id);
            long compositionId = OwnedWriteSupport.transaction(dataSource,
                    c -> insertComposition(c, reagentId, payload(body), principal));
            boundary.recordAllowed("reagent-composition-insert", principal, String.valueOf(compositionId), "isolated-write");
            return ok("{\"id\":" + compositionId + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @DeleteMapping(path = "/secure/core/module/bp/reagents/{id}/compositions",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteComposition(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); long reagentId = positiveId(id);
            List<Long> ids = ids(body);
            int deleted = OwnedWriteSupport.transaction(dataSource,
                    c -> deleteCompositions(c, reagentId, ids, principal));
            boundary.recordAllowed("reagent-composition-delete", principal, ids.toString(), "isolated-write");
            return ok("{\"deleted\":" + deleted + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    @PostMapping(path = "/secure/core/module/bp/reagents/{id}/copyReagent",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> copy(HttpServletRequest request, @PathVariable("id") String id,
            @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = requireWrite(request); long source = positiveId(id);
            long copy = OwnedWriteSupport.transaction(dataSource,
                    c -> copyReagent(c, source, payload(body), principal));
            boundary.recordAllowed("reagent-copy", principal, String.valueOf(copy), "isolated-write");
            return ok("{\"id\":" + copy + "}");
        } catch (AccessDeniedException denied) { denied(request); return forbidden(); }
        catch (IllegalArgumentException invalid) { denied(request); return badRequest(); }
        catch (SQLException failure) { denied(request); return unavailable(); }
    }

    private OwnedPrincipal requireWrite(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.sss2.write.mode"))) throw denied();
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.CONSUMABLE_MANAGE);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private static long insertReagent(Connection c, JSONObject v, OwnedPrincipal p) throws SQLException {
        long id = OwnedWriteSupport.nextIdentity(c);
        if (text(v.get("reagentName")).length() > 60) throw new IllegalArgumentException("reagentName is too long");
        String sql = "INSERT INTO T_EMC_REAGENT (ID,REAGENTNAME,PREPAREDATE,REAGENTSPECIFICATION,DISHWEIGHT,DISHREAGENTWEIGHT,REAGENTWEIGHT,CONSTANTVOLUME,PREPARECONCENT,APPLICABLEITEM,VALIDDATE,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME,PREPAREBASIS,PREPAREVOLUME,SOLVENT,AUDITORID,AUDITORNAME,EQUIPTID,EQUIPTNAME,TEMPERATURE,HUMIDITY,METHODID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSTIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement q = c.prepareStatement(sql)) {
            int i = 1; q.setLong(i++, id); set(q, i++, v.get("reagentName")); setDate(q, i++, v.get("prepareDate"));
            set(q, i++, v.get("reagentSpecification")); set(q, i++, v.get("dishWeight")); set(q, i++, v.get("dishReagentWeight"));
            set(q, i++, v.get("reagentWeight")); set(q, i++, v.get("constantVolume")); set(q, i++, v.get("prepareConcent"));
            set(q, i++, v.get("applicableItem")); setDate(q, i++, v.get("validDate")); set(q, i++, v.get("remark"));
            set(q, i++, p.getId()); set(q, i++, p.getId()); set(q, i++, p.getOrgId()); set(q, i++, p.getOrgName());
            setExt(q, i++, v, "preparebasis"); setExt(q, i++, v, "preparevolume"); setExt(q, i++, v, "solvent");
            setExt(q, i++, v, "auditorid"); setExt(q, i++, v, "auditorname"); setExt(q, i++, v, "equiptid");
            setExt(q, i++, v, "equiptname"); set(q, i++, v.get("temperature")); set(q, i++, v.get("humidity")); set(q, i++, v.get("methodId"));
            if (q.executeUpdate() != 1) throw new SQLException("reagent insert failed");
        }
        return id;
    }

    private static int updateReagent(Connection c, long id, JSONObject v, OwnedPrincipal p) throws SQLException {
        Map<String, Object> fields = new LinkedHashMap<String, Object>();
        add(fields, v, "REAGENTNAME", "reagentName"); add(fields, v, "PREPAREDATE", "prepareDate");
        add(fields, v, "REAGENTSPECIFICATION", "reagentSpecification"); add(fields, v, "PREPARECONCENT", "prepareConcent");
        add(fields, v, "APPLICABLEITEM", "applicableItem"); add(fields, v, "VALIDDATE", "validDate"); add(fields, v, "REMARK", "remark");
        add(fields, v, "TEMPERATURE", "temperature"); add(fields, v, "HUMIDITY", "humidity"); add(fields, v, "METHODID", "methodId");
        if (fields.isEmpty()) throw new IllegalArgumentException("no reagent fields supplied");
        StringBuilder sql = new StringBuilder("UPDATE T_EMC_REAGENT SET "); List<Object> values = new ArrayList<Object>();
        for (Map.Entry<String,Object> e : fields.entrySet()) { if (values.size() > 0) sql.append(','); sql.append(e.getKey()).append("=?"); values.add(e.getValue()); }
        sql.append(" WHERE ID=? AND CREATEDBYORGID=?"); values.add(id); values.add(p.getOrgId());
        try (PreparedStatement q = c.prepareStatement(sql.toString())) { bind(q, values); int count=q.executeUpdate(); if (count != 1) throw denied(); return count; }
    }

    private static int deleteReagents(Connection c, List<Long> ids, OwnedPrincipal p) throws SQLException {
        int count=0; for (Long id:ids) { try (PreparedStatement d=c.prepareStatement("DELETE FROM T_EMC_REAGENT_COMPOSITION WHERE REAGENTID=? AND EXISTS (SELECT 1 FROM T_EMC_REAGENT R WHERE R.ID=? AND R.CREATEDBYORGID=?)")) { d.setLong(1,id); d.setLong(2,id); d.setString(3,p.getOrgId()); d.executeUpdate(); }
            try (PreparedStatement d=c.prepareStatement("DELETE FROM T_EMC_REAGENT WHERE ID=? AND CREATEDBYORGID=?")) { d.setLong(1,id); d.setString(2,p.getOrgId()); int n=d.executeUpdate(); if(n!=1) throw denied(); count+=n; } }
        return count;
    }

    private static long insertComposition(Connection c, long reagentId, JSONObject v, OwnedPrincipal p) throws SQLException {
        requireReagent(c,reagentId,p); long id=OwnedWriteSupport.nextIdentity(c);
        try(PreparedStatement q=c.prepareStatement("INSERT INTO T_EMC_REAGENT_COMPOSITION (ID,REAGENTID,REAGENTNAME,REAGENTFACTORY,REAGENTBATCHNO,WEIGHTVOLUME,REMARK,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?, ?,?,SYSTIMESTAMP,?,?)")) {
            int i=1; q.setLong(i++,id); q.setLong(i++,reagentId); set(q,i++,v.get("reagentName")); set(q,i++,v.get("reagentFactory")); set(q,i++,v.get("reagentBatchNo")); set(q,i++,v.get("weightVolume")); set(q,i++,v.get("remark")); set(q,i++,p.getId()); set(q,i++,p.getId()); set(q,i++,p.getOrgId()); set(q,i++,p.getOrgName()); if(q.executeUpdate()!=1) throw new SQLException("composition insert failed"); }
        return id;
    }

    private static int deleteCompositions(Connection c,long reagentId,List<Long> ids,OwnedPrincipal p)throws SQLException{requireReagent(c,reagentId,p);int n=0;for(Long id:ids){try(PreparedStatement q=c.prepareStatement("DELETE FROM T_EMC_REAGENT_COMPOSITION WHERE ID=? AND REAGENTID=?")){q.setLong(1,id);q.setLong(2,reagentId);n+=q.executeUpdate();}}if(n!=ids.size())throw denied();return n;}

    private static long copyReagent(Connection c,long source,JSONObject ignored,OwnedPrincipal p)throws SQLException{requireReagent(c,source,p);Map<String,Object> row=new LinkedHashMap<String,Object>();try(PreparedStatement q=c.prepareStatement("SELECT REAGENTNAME,REAGENTSPECIFICATION,DISHWEIGHT,DISHREAGENTWEIGHT,REAGENTWEIGHT,CONSTANTVOLUME,PREPARECONCENT,APPLICABLEITEM,REMARK,PREPAREBASIS,PREPAREVOLUME,SOLVENT,AUDITORID,AUDITORNAME,EQUIPTID,EQUIPTNAME,METHODID FROM T_EMC_REAGENT WHERE ID=? AND CREATEDBYORGID=?")){q.setLong(1,source);q.setString(2,p.getOrgId());try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();String[] names={"reagentName","reagentSpecification","dishWeight","dishReagentWeight","reagentWeight","constantVolume","prepareConcent","applicableItem","remark","preparebasis","preparevolume","solvent","auditorid","auditorname","equiptid","equiptname","methodId"};for(int i=0;i<names.length;i++)row.put(names[i],r.getObject(i+1));}}row.put("prepareDate",null);row.put("validDate",null);row.put("temperature",null);row.put("humidity",null);long copy=insertReagent(c,new JSONObject(row),p);try(PreparedStatement q=c.prepareStatement("SELECT REAGENTNAME,REAGENTFACTORY,REAGENTBATCHNO,WEIGHTVOLUME,REMARK FROM T_EMC_REAGENT_COMPOSITION WHERE REAGENTID=?")){q.setLong(1,source);try(ResultSet r=q.executeQuery()){while(r.next()){JSONObject v=new JSONObject();v.put("reagentName",r.getObject(1));v.put("reagentFactory",r.getObject(2));v.put("reagentBatchNo",r.getObject(3));v.put("weightVolume",r.getObject(4));v.put("remark",r.getObject(5));insertComposition(c,copy,v,p);}}}return copy;}

    private static void requireReagent(Connection c,long id,OwnedPrincipal p)throws SQLException{try(PreparedStatement q=c.prepareStatement("SELECT 1 FROM T_EMC_REAGENT WHERE ID=? AND CREATEDBYORGID=?")){q.setLong(1,id);q.setString(2,p.getOrgId());try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();}}}
    private static void add(Map<String,Object> f,JSONObject v,String col,String key){if(v.containsKey(key))f.put(col,normal(v.get(key),key));}
    private static Object normal(Object v,String key){if(v==null)return null;if(key.endsWith("Date"))return Date.valueOf(LocalDate.parse(String.valueOf(v)));return v;}
    private static JSONObject payload(String body){if(body==null||body.trim().isEmpty())throw new IllegalArgumentException("request is required");Object parsed=JSON.parse(body);if(!(parsed instanceof JSONObject))throw new IllegalArgumentException("request must be object");JSONObject o=(JSONObject)parsed;if(o.containsKey("requestJson"))o=JSON.parseObject(String.valueOf(o.get("requestJson")));Object p=o.get("p");if(p instanceof JSONObject)return(JSONObject)p;if(p instanceof String)return JSON.parseObject((String)p);return o;}
    private static List<Long> ids(String body){JSONObject o=payload(body);Object raw=o.get("ids");if(raw==null)raw=o.get("p");if(!(raw instanceof JSONArray))throw new IllegalArgumentException("ids are required");List<Long> out=new ArrayList<Long>();for(Object v:(JSONArray)raw)out.add(positiveId(String.valueOf(v)));if(out.isEmpty())throw new IllegalArgumentException("ids are required");return out;}
    private static long positiveId(String value){try{long n=Long.parseLong(value);if(n<=0)throw new Exception();return n;}catch(Exception e){throw new IllegalArgumentException("id is invalid");}}
    private static String text(Object v){return v==null?"":String.valueOf(v).trim();}
    private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}
    private void denied(HttpServletRequest request){boundary.recordRequestDenied(request==null?null:request.getSession(false),"reagent.write","permission-denied");}
    private static void set(PreparedStatement q,int i,Object v)throws SQLException{if(v==null)q.setObject(i,null);else q.setObject(i,v);}
    private static void setDate(PreparedStatement q,int i,Object v)throws SQLException{if(v==null)q.setDate(i,null);else q.setDate(i,Date.valueOf(LocalDate.parse(String.valueOf(v))));}
    private static void setExt(PreparedStatement q,int i,JSONObject v,String key)throws SQLException{Object ext=v.get("ext$");if(ext instanceof JSONObject)set(q,i,((JSONObject)ext).get(key));else set(q,i,v.get(key));}
    private static void bind(PreparedStatement q,List<Object> values)throws SQLException{for(int i=0;i<values.size();i++){Object v=values.get(i);if(v instanceof LocalDate)v=Date.valueOf((LocalDate)v);q.setObject(i+1,v);}}
    private static ResponseEntity<String> ok(String b){return ResponseEntity.ok().header("Cache-Control","no-store").body(b);}
    private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");}
    private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.REAGENT.INVALID_REQUEST\"}");}
    private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.REAGENT.UNAVAILABLE\"}");}
}
