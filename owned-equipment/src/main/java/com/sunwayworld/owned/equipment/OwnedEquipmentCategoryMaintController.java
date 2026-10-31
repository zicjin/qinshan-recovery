package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned equipment-category maintenance-event API family. */
@RestController
@RequestMapping("/secure/emc/module/mdm/basemdm/equipt-categories")
public final class OwnedEquipmentCategoryMaintController {
    private final DataSource dataSource; private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary; private final AuditEventSink audit;
    private final org.springframework.core.env.Environment environment;
    public OwnedEquipmentCategoryMaintController(DataSource source, JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit, org.springframework.core.env.Environment environment) { this.dataSource=source;this.provider=provider;this.boundary=boundary;this.audit=audit;this.environment=environment; }
    @RequestMapping(value="/{id}/maints/queries",method=RequestMethod.POST)
    public Page<EmcEquiptMaintBean> read(@PathVariable Long id, RestJsonWrapperBean wrapper){
        if(id==null||id<0L)throw new IllegalArgumentException("category id is invalid"); try{OwnedPrincipal p=readPrincipal();Set<String> orgs=provider.permittedOrganizationIds(p);QueryRequest req=QueryRequest.parse(wrapper==null?null:wrapper.getRequestJson(),wrapper==null?null:wrapper.getParamMap());List<Object> scopeValues=new ArrayList<Object>();String scope=categoryScope(id,orgs,scopeValues);String base="SELECT M.* FROM T_EMC_EQUIPT_MAINT M WHERE M.EQUIPTCATEGORYID=? AND "+scope;List<Object> values=new ArrayList<Object>();values.add(id);values.addAll(scopeValues);int from=(req.getPageNumber()-1)*req.getPageSize()+1,to=req.getPageNumber()*req.getPageSize(),total;List<EmcEquiptMaintBean> rows=new ArrayList<EmcEquiptMaintBean>();try(Connection c=dataSource.getConnection()){try(PreparedStatement q=c.prepareStatement("SELECT COUNT(1) FROM ("+base+") X")){bind(q,values);try(ResultSet r=q.executeQuery()){r.next();total=r.getInt(1);}}List<Object> pv=new ArrayList<Object>(values);pv.add(from);pv.add(to);try(PreparedStatement q=c.prepareStatement("SELECT * FROM (SELECT X.*,ROW_NUMBER() OVER (ORDER BY X.ID DESC) RN FROM ("+base+") X) WHERE RN BETWEEN ? AND ? ORDER BY ID DESC")){bind(q,pv);try(ResultSet r=q.executeQuery()){while(r.next())rows.add(convert(r));}}}return new Pagination<EmcEquiptMaintBean>(total,req.getPageSize(),req.getPageNumber(),rows);}catch(SQLException e){throw new IllegalStateException("equipment maintenance event query failed",e);}}
    @RequestMapping(value="/{id}/maints",method=RequestMethod.POST)
    public Long insert(@PathVariable Long id, RestJsonWrapperBean wrapper){if(id==null||id<0L)throw new IllegalArgumentException("category id is invalid");requireIsolated();EmcEquiptMaintBean v=wrapper.parseUnique(EmcEquiptMaintBean.class);validate(v);try{OwnedPrincipal p=managePrincipal();Set<String> orgs=provider.permittedOrganizationIds(p);Long result=OwnedWriteSupport.transaction(dataSource,c->{requireCategory(c,id,orgs);long n=OwnedWriteSupport.nextIdentity(c);try(PreparedStatement q=c.prepareStatement("INSERT INTO T_EMC_EQUIPT_MAINT (ID,EQUIPTCATEGORYID,MAINTEVENT,EVENTDESC,WARNINGDAYS,SAME_DAY) VALUES (?,?,?,?,?,?)")){q.setLong(1,n);q.setLong(2,id);q.setString(3,v.getMaintEvent());q.setString(4,v.getEventDesc());if(v.getWarningDays()==null)q.setNull(5,java.sql.Types.NUMERIC);else q.setLong(5,v.getWarningDays());q.setString(6,v.getSameDay());q.executeUpdate();}return n;});audit.record(new AuditEvent(java.time.Instant.now(),"equipment-maint-event-insert",p.getId(),String.valueOf(result),AuditEvent.Outcome.ALLOWED,"isolated-write",null));return result;}catch(SQLException e){throw new IllegalStateException("equipment maintenance event write failed",e);}}
    @RequestMapping(value="/{id}/maints",method=RequestMethod.DELETE)
    public void delete(@PathVariable Long id, RestJsonWrapperBean wrapper){if(id==null||id<0L)throw new IllegalArgumentException("category id is invalid");requireIsolated();List<Long> ids=wrapper.parseId(Long.class);if(ids.isEmpty()||ids.size()>100)throw new IllegalArgumentException("maintenance event ids are required");try{OwnedPrincipal p=managePrincipal();Set<String> orgs=provider.permittedOrganizationIds(p);OwnedWriteSupport.transaction(dataSource,c->{requireCategory(c,id,orgs);try(PreparedStatement q=c.prepareStatement("DELETE FROM T_EMC_EQUIPT_MAINT WHERE ID=? AND EQUIPTCATEGORYID=?")){for(Long x:ids){q.setLong(1,x);q.setLong(2,id);if(q.executeUpdate()!=1)throw denied();}}return null;});audit.record(new AuditEvent(java.time.Instant.now(),"equipment-maint-event-delete",p.getId(),ids.toString(),AuditEvent.Outcome.ALLOWED,"isolated-write",null));}catch(SQLException e){throw new IllegalStateException("equipment maintenance event delete failed",e);}}
    private OwnedPrincipal readPrincipal()throws SQLException{HttpSession s=current().getSession(false);boundary.requirePermission(s,OwnedIdentityProvider.EQUIPMENT_READ);return boundary.currentPrincipal(s);} private OwnedPrincipal managePrincipal()throws SQLException{HttpSession s=current().getSession(false);boundary.requirePermission(s,OwnedIdentityProvider.EQUIPMENT_MANAGE);return boundary.currentPrincipal(s);} private void requireIsolated(){if(environment==null||!"isolated".equals(environment.getProperty("owned.equipment.write.mode")))throw denied();OwnedWriteSupport.requireLoopback(current());} private static javax.servlet.http.HttpServletRequest current(){org.springframework.web.context.request.RequestAttributes a=org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();if(!(a instanceof org.springframework.web.context.request.ServletRequestAttributes))throw denied();return ((org.springframework.web.context.request.ServletRequestAttributes)a).getRequest();} private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}
    private static String categoryScope(long id,Set<String> orgs,List<Object> vals){if(orgs==null||orgs.isEmpty())throw denied();StringBuilder s=new StringBuilder("EXISTS (SELECT 1 FROM T_EMC_ORG_PERMISSION P WHERE P.PERMISSIONTYPEID='T_EMC_EQUIPT_CATEGORY' AND P.TARGETID=? AND P.ORGID IN (");vals.add(String.valueOf(id));int i=0;for(String o:orgs){if(i++>0)s.append(',');s.append('?');vals.add(o);}return s.append("))").toString();} private static void requireCategory(Connection c,long id,Set<String> orgs)throws SQLException{List<Object> v=new ArrayList<Object>();String scope=categoryScope(id,orgs,v);try(PreparedStatement q=c.prepareStatement("SELECT 1 FROM T_EMC_EQUIPT_CATEGORY C WHERE C.ID=? AND "+scope)){int i=1;q.setLong(i++,id);for(Object x:v)q.setString(i++,String.valueOf(x));try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();}}} private static void bind(PreparedStatement q,List<Object> v)throws SQLException{int i=1;for(Object x:v)q.setObject(i++,x);} private static void validate(EmcEquiptMaintBean v){if(v==null||v.getMaintEvent()==null||v.getMaintEvent().trim().isEmpty()||v.getMaintEvent().length()>64)throw new IllegalArgumentException("maintenance event is required");if(v.getEventDesc()!=null&&v.getEventDesc().length()>256)throw new IllegalArgumentException("event description is too long");if(v.getWarningDays()!=null&&(v.getWarningDays()<0||v.getWarningDays()>36500))throw new IllegalArgumentException("warning days is invalid");} private static EmcEquiptMaintBean convert(ResultSet r)throws SQLException{EmcEquiptMaintBean v=new EmcEquiptMaintBean();v.setId(r.getLong("ID"));v.setEquiptCategoryId(r.getLong("EQUIPTCATEGORYID"));v.setMaintEvent(r.getString("MAINTEVENT"));v.setEventDesc(r.getString("EVENTDESC"));long w=r.getLong("WARNINGDAYS");if(!r.wasNull())v.setWarningDays(w);v.setSameDay(r.getString("SAME_DAY"));return v;}
}
