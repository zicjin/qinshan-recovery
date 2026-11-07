import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.*;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.*;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.*;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.impl.*;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.resource.impl.EmcSealRuleResourceImpl;
import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.context.*;
import com.sunwayworld.framework.database.context.*;
import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.context.instance.*;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.DaoMapper;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.database.sql.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.at.bean.*;
import com.sunwayworld.module.sys.at.service.*;
import com.sunwayworld.module.sys.bpmn.service.*;
import com.sunwayworld.module.sys.metadata.dao.*;
import com.sunwayworld.module.sys.metadata.mapper.CoreMetadataMapper;
import com.sunwayworld.module.sys.metadata.service.impl.CoreTableServiceImpl;
import java.lang.reflect.*;
import java.util.*;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.*;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import com.sunwayworld.framework.exception.checked.CheckedException;
import org.springframework.transaction.annotation.*;
import org.springframework.web.context.request.*;

/** Public service CRUD/readback and audit boundary. JDBC adapters persist to disposable H2;
 * actual original services, CoreTableService cascade and MybatisDaoSupport deletion, audit aspect and Spring TX execute unchanged.
 * This does not claim Oracle/HTTP or strict evidence. SQL fixtures never touch a live database. */
public final class SealChildDeleteTest {
    @Configuration @EnableTransactionManagement(proxyTargetClass=true)
    @EnableAspectJAutoProxy(proxyTargetClass=true)
    public static class Transactions {}

    interface Call { Object invoke(Method method, Object[] args) throws Throwable; }
    static <T> T adapter(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (p,m,a) -> {
            if (m.getDeclaringClass() == Object.class) {
                if (m.getName().equals("toString")) return "JDBC fixture " + type.getSimpleName();
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
            }
            return call.invoke(m, a);
        }));
    }
    static Object unused(Method m, Object[] a) { throw new AssertionError("Unexpected fixture call: " + m); }
    static void equal(Object expected, Object actual, String reason) {
        if (!Objects.equals(expected, actual)) throw new AssertionError(reason + ": expected " + expected + ", got " + actual);
    }
    static EntityContext entity(Class<?> beanClass, String table, String... names) {
        EntityContext entity = new EntityContext(); entity.setTableName(table);
        List<EntityColumnContext> columns = new ArrayList<>();
        for (String name : names) {
            EntityColumnContext col = new EntityColumnContext(); col.setColumnName(name);
            col.setEntityClass(beanClass); col.setType(name.equals("id") || name.equals("sealRuleId") ? Long.class : String.class);
            columns.add(col); if (name.equals("id")) entity.setIdContext(col);
        }
        entity.setColumnContextList(columns); return entity;
    }
    static final EntityContext DETAILS = entity(EmcSealRuleDetailsBean.class,"T_EMC_SEAL_RULE_DETAILS","id","sealRuleId","sealName");
    static final EntityContext RULES = entity(EmcSealRuleBean.class,"T_EMC_SEAL_RULE","id");
    /** Local metadata-cache boundary only; no real Redis connection is opened. */
    static final class LocalMetadataCache extends RedisTemplate<String,Object> {
        final Map<Object,Object> values = new HashMap<>();
        @Override public ValueOperations<String,Object> opsForValue() {
            return adapter(ValueOperations.class, (m,a) -> {
                if (m.getName().equals("get")) return values.get(a[0]);
                if (m.getName().equals("set")) { values.put(a[0],a[1]); return null; }
                return unused(m,a);
            });
        }
    }
    public static class DetailsDao extends MybatisDaoSupport<EmcSealRuleDetailsBean,Long> implements EmcSealRuleDetailsDao {
        final Store store;
        DetailsDao(Store store) { this.store=store; }
        @Override public EntityContext getEntityContext() { return DETAILS; }
        @Override public String getTable() { return "T_EMC_SEAL_RULE_DETAILS"; }
        @Override public EmcSealRuleDetailsBean selectById(Long id) { return Objects.requireNonNull(selectByIdIfPresent(id)); }
        @Override public EmcSealRuleDetailsBean selectByIdIfPresent(Long id) {
            List<EmcSealRuleDetailsBean> rows=store.details("WHERE ID=?", id); return rows.isEmpty()?null:rows.get(0);
        }
        @Override public List<EmcSealRuleDetailsBean> selectListByIds(List<Long> ids, List<String> cols, Order... order) {
            List<EmcSealRuleDetailsBean> rows=new ArrayList<>(); for (Long id:ids) rows.addAll(store.details("WHERE ID=?",id)); return rows;
        }
    }
    public static class RuleDao extends MybatisDaoSupport<EmcSealRuleBean,Long> implements EmcSealRuleDao {
        final Store store;
        RuleDao(Store store) { this.store=store; }
        @Override public EntityContext getEntityContext() { return RULES; }
        @Override public String getTable() { return "T_EMC_SEAL_RULE"; }
        @Override public EmcSealRuleBean selectByIdIfPresent(Long id) {
            List<Long> ids=store.db.queryForList("SELECT ID FROM T_EMC_SEAL_RULE WHERE ID=?",Long.class,id);
            if(ids.isEmpty()) return null; EmcSealRuleBean row=new EmcSealRuleBean(); row.setId(id); return row;
        }
    }
    static void mapper(Object dao, Store store) throws Exception {
        Field field=MybatisDaoSupport.class.getDeclaredField("daoMapper"); field.setAccessible(true);
        field.set(dao,adapter(DaoMapper.class,(m,a) -> {
            if(m.getName().equals("batchDeleteById")) {
                EntityContext context=(EntityContext)a[0];
                for(Object id:(List<?>)a[1]) {
                    store.db.update("DELETE FROM "+context.getTableName()+" WHERE ID=?",id);
                    store.deletes++;
                }
                return null;
            }
            return unused(m,a);
        }));
    }
    static final class Store {
        final JdbcTemplate db;
        Store(DataSource source) { db = new JdbcTemplate(source); }
        int deletes;
        int attachmentCalls;
        List<EmcSealRuleDetailsBean> details(String where,Object... params) {
            return db.query("SELECT ID,SEALRULEID,SEALNAME FROM T_EMC_SEAL_RULE_DETAILS "+where,(rs,n)-> {
                EmcSealRuleDetailsBean row=new EmcSealRuleDetailsBean(); row.setId(rs.getLong(1)); row.setSealRuleId(rs.getLong(2)); row.setSealName(rs.getString(3)); return row;
            },params);
        }
        void schema() {
            db.execute("CREATE TABLE T_EMC_SEAL_RULE(ID BIGINT PRIMARY KEY)");
            db.execute("CREATE TABLE T_EMC_SEAL_RULE_DETAILS(ID BIGINT PRIMARY KEY,SEALRULEID BIGINT REFERENCES T_EMC_SEAL_RULE(ID),SEALNAME VARCHAR(100))");
            db.execute("CREATE TABLE ATTACHMENT_FIXTURE(CHILD_ID BIGINT PRIMARY KEY,NAME VARCHAR(40))");
            db.execute("CREATE TABLE AUDIT_RECORD(ID BIGINT PRIMARY KEY, EVENT VARCHAR(40), TARGET VARCHAR(100))");
            db.execute("CREATE TABLE AUDIT_LINE(RECORD_ID BIGINT, EVENT VARCHAR(40), OLD_VALUE VARCHAR(100),NEW_VALUE VARCHAR(100),COL VARCHAR(60))");
            db.update("INSERT INTO T_EMC_SEAL_RULE VALUES(41),(42)");
            db.update("INSERT INTO T_EMC_SEAL_RULE_DETAILS VALUES(301,41,'seal A'),(302,42,'seal B')");
            db.update("INSERT INTO ATTACHMENT_FIXTURE VALUES(301,'attachment A'),(302,'attachment B')");
        }
        int audit(String table) { return db.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class); }
    }
    static RestJsonWrapperBean body(String... rows) {
        return new RestJsonWrapperBean(Collections.emptyMap(),Collections.singletonList(Arrays.asList(rows)),Collections.singletonList(null));
    }
    static void delete(AnnotationConfigApplicationContext ctx,Long parent,String... rows) {
        ctx.getBean(EmcSealRuleResourceImpl.class).deleteRuleDetails(parent,body(rows));
    }
    static void localIdentity() throws Exception {
        Constructor<?> ctor = Class.forName("com.sunwayworld.framework.id.SnowflakeIdWorker").getDeclaredConstructor(long.class);
        ctor.setAccessible(true); Field worker = GikamIdHelper.class.getDeclaredField("worker"); worker.setAccessible(true); worker.set(null, ctor.newInstance(1L));
    }
    static void run(String scenario) throws Exception {
        localIdentity();
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.getEnvironment().setActiveProfiles("audit-trail");
            ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture", new HashMap<String,Object>() {{ put(AuditDatabaseOperation.UPDATE.getPropertyKey(),true); put(AuditDatabaseOperation.DELETE.getPropertyKey(),true); put("spring.datasource.driver-class-name","org.h2.Driver"); }}));
            DataSource ds = new DriverManagerDataSource("jdbc:h2:mem:seal_child_delete;DB_CLOSE_DELAY=-1", "sa", "");
            Store store = new Store(ds); store.schema();
            if(scenario.equals("rollback")) store.db.execute("ALTER TABLE AUDIT_RECORD ADD CHECK(EVENT<>'DELETE')");
            ctx.registerBean(DataSource.class, () -> ds);
            ctx.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(ds));
            DetailsDao detailsDao=new DetailsDao(store); RuleDao ruleDao=new RuleDao(store);
            mapper(detailsDao,store);
            Dialect dialect=adapter(Dialect.class,(m,a)-> { if(m.getName().equals("getInClauseIdMaxQty")) return 1000; return unused(m,a); });
            for(Object dao:Arrays.asList(detailsDao,ruleDao)) {
                Field field=MybatisDaoSupport.class.getDeclaredField("dialect"); field.setAccessible(true); field.set(dao,dialect);
                field=MybatisDaoSupport.class.getDeclaredField("daoAfterDeleteSupport"); field.setAccessible(true);
                field.set(dao,new com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteSupport());
            }
            ctx.getBeanFactory().registerSingleton("emcSealRuleDetailsDao",detailsDao);
            ctx.getBeanFactory().registerSingleton("emcSealRuleDao",ruleDao);
            LocalMetadataCache metadata=new LocalMetadataCache();
            metadata.values.put("EC::"+EmcSealRuleDetailsBean.class.toString(),DETAILS);
            metadata.values.put("EC::"+EmcSealRuleBean.class.toString(),RULES);
            new RedisHelper().setRedisTemplate(metadata);

            ctx.registerBean(CoreTableDao.class, () -> adapter(CoreTableDao.class, (m,a) -> {
                if (m.getName().equals("getTable")) return "T_CORE_TABLE";
                if (m.getName().equals("selectByIdIfPresent")) {
                    if(!"T_EMC_SEAL_RULE_DETAILS".equals(a[0])) return null;
                    com.sunwayworld.module.sys.metadata.bean.CoreTableBean table=new com.sunwayworld.module.sys.metadata.bean.CoreTableBean();
                    table.setId("T_EMC_SEAL_RULE_DETAILS"); table.setAuditable("0"); table.setAttachment("1"); return table;
                } return unused(m,a);
            }));
            ctx.registerBean(CoreTableHierarchyDao.class, () -> adapter(CoreTableHierarchyDao.class, (m,a) -> {
                if (m.getName().equals("selectList")) return Collections.emptyList(); return unused(m,a);
            }));
            ctx.registerBean(CoreTablePermissionDao.class, () -> adapter(CoreTablePermissionDao.class, SealChildDeleteTest::unused));
            ctx.registerBean(CoreMetadataMapper.class, () -> adapter(CoreMetadataMapper.class, SealChildDeleteTest::unused));
            ctx.registerBean(CoreFileService.class, () -> adapter(CoreFileService.class, (m,a) -> {
                if(!m.getName().equals("batchDelete")) return unused(m,a);
                equal("T_EMC_SEAL_RULE_DETAILS",a[0],"original cascade attachment table");
                store.attachmentCalls++;
                for(Object id:(List<?>)a[1]) store.db.update("DELETE FROM ATTACHMENT_FIXTURE WHERE CHILD_ID=?",id);
                return null;
            }));
            ctx.registerBean(CoreBpmnCommentService.class, () -> adapter(CoreBpmnCommentService.class, SealChildDeleteTest::unused));
            ctx.registerBean(CoreBpmnTargetService.class, () -> adapter(CoreBpmnTargetService.class, SealChildDeleteTest::unused));
            ctx.registerBean(Dialect.class, () -> adapter(Dialect.class, SealChildDeleteTest::unused));
            CoreAuditTrailConfigService config = adapter(CoreAuditTrailConfigService.class, (m,a) -> {
                if (m.getName().equals("auditTrailable")) return true;
                if (m.getName().equals("selectAuditTrailableLineList")) {
                    List<CoreAuditTrailConfigLineBean> lines = new ArrayList<>();
                    for (String name : Arrays.asList("id","sealRuleId","sealName")) {
                        CoreAuditTrailConfigLineBean line = new CoreAuditTrailConfigLineBean(); line.setColumnId(name); lines.add(line);
                    } return lines;
                } return unused(m,a);
            });
            new AuditTrailHelper().setCoreAuditTrailConfigService(config);
            GenericDao records = adapter(GenericDao.class, (m,a) -> {
                if (m.getName().equals("getTable")) return "T_CORE_AT_RECORD";
                if (m.getName().equals("insert")) {
                    for (CoreAuditTrailRecordBean record : (List<CoreAuditTrailRecordBean>) a[0])
                        store.db.update("INSERT INTO AUDIT_RECORD VALUES(?,?,?)", record.getId(), record.getAuditTrailType(), record.getTargetId());
                    return null;
                } return unused(m,a);
            });
            ctx.registerBean(CoreAuditTrailRecordService.class, () -> adapter(CoreAuditTrailRecordService.class, (m,a) -> {
                if (m.getName().equals("getDao")) return records; return unused(m,a);
            }));
            ctx.registerBean(CoreAuditTrailRecordLineService.class, () -> adapter(CoreAuditTrailRecordLineService.class, (m,a) -> {
                if (m.getName().equals("insert")) {
                    for (CoreAuditTrailRecordLineBean line : (List<CoreAuditTrailRecordLineBean>) a[0])
                        store.db.update("INSERT INTO AUDIT_LINE VALUES(?,?,?,?,?)", line.getRecordId(), line.getRecordType(), line.getOldValue(),line.getNewValue(),line.getColumn());
                    return null;
                } return unused(m,a);
            }));
            ctx.register(Transactions.class, EmcSealRuleDetailsServiceImpl.class, EmcSealRuleServiceImpl.class, EmcSealRuleResourceImpl.class, CoreTableServiceImpl.class, AuditTrailAspect.class);
            ApplicationContextHelper.setApplicationContext(ctx); ctx.refresh(); GikamBeanContext.init();
            EmcSealRuleDetailsService service = ctx.getBean(EmcSealRuleDetailsService.class);
            equal(true, AopUtils.isAopProxy(service), "service runs through Spring transaction and audit proxy");
            HttpServletRequest request = adapter(HttpServletRequest.class, (m,a) -> {
                if (m.getName().equals("getHeader")) {
                    if (a[0].equals("primaryId")) return "41"; return null;
                }
                if (m.getName().equals("getAttribute") || m.getName().equals("getCookies")) return null;
                if (m.getName().equals("setAttribute") || m.getName().equals("removeAttribute")) return null;
                return unused(m,a);
            });
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            LocalContextHelper.setServletRequest(request);
            SunwayAopContext.setCurrentProxy(detailsDao);
            try {
                if(Arrays.asList("foreign","mixed","forged","null-id","null-parent","stored-null").contains(scenario)) {
                    if(scenario.equals("stored-null")) store.db.update("UPDATE T_EMC_SEAL_RULE_DETAILS SET SEALRULEID=NULL WHERE ID=301");
                    boolean rejected=false;
                    try {
                        if(scenario.equals("mixed")) delete(ctx,42L,"{\"id\":302}","{\"id\":301}");
                        else if(scenario.equals("forged")) delete(ctx,42L,"{\"id\":301,\"sealRuleId\":42}");
                        else if(scenario.equals("null-id")) delete(ctx,41L,"{}");
                        else if(scenario.equals("null-parent")) delete(ctx,null,"{\"id\":301}");
                        else delete(ctx,42L,"{\"id\":301}");
                    } catch(CheckedException expected) { rejected=true; equal("GIKAM.EXCEPTION.PARAM_ERROR",expected.getMessage(),"existing controlled parameter error"); }
                    equal(true,rejected,"invalid or foreign child batch rejected");
                    equal("seal A",service.selectById(301L).getSealName(),"foreign child remains");
                    equal("seal B",service.selectById(302L).getSealName(),"earlier valid batch row remains");
                    equal(0,store.deletes,"scope rejects before any DELETE"); equal(0,store.attachmentCalls,"scope rejects before attachment delegation"); equal(2,store.audit("ATTACHMENT_FIXTURE"),"rejection retains attachments"); equal(0,store.audit("AUDIT_RECORD"),"rejection has no deletion audit"); equal(0,store.audit("AUDIT_LINE"),"rejection has no audit lines");
                } else if(scenario.equals("valid") || scenario.equals("valid-forged")) {
                    delete(ctx,41L,scenario.equals("valid")?"{\"id\":301}":"{\"id\":301,\"sealRuleId\":42}");
                    equal(null,service.selectByIdIfPresent(301L),"matching stored child deleted");
                    equal("seal B",service.selectById(302L).getSealName(),"other parent child remains");
                    equal(1,store.deletes,"original DAO deletion executed"); equal(1,store.attachmentCalls,"original cascade delegates attachments"); equal(1,store.audit("ATTACHMENT_FIXTURE"),"only matching attachment removed"); equal(1,store.audit("AUDIT_RECORD"),"original deletion audit header");
                    equal("DELETE",store.db.queryForObject("SELECT EVENT FROM AUDIT_RECORD",String.class),"original DELETE event");
                    equal("seal A",store.db.queryForObject("SELECT OLD_VALUE FROM AUDIT_LINE WHERE COL='SEALNAME'",String.class),"original stored child old value audit");
                    equal(0,store.db.queryForObject("SELECT COUNT(*) FROM AUDIT_LINE WHERE OLD_VALUE='seal B'",Integer.class),"unrelated child not audited");
                } else if(scenario.equals("missing") || scenario.equals("empty")) {
                    if(scenario.equals("missing")) delete(ctx,41L,"{\"id\":999}"); else delete(ctx,41L);
                    equal("seal A",service.selectById(301L).getSealName(),"no-op retains own child"); equal("seal B",service.selectById(302L).getSealName(),"no-op retains other child");
                    equal(0,store.audit("AUDIT_RECORD"),"no-op no header"); equal(0,store.audit("AUDIT_LINE"),"no-op no lines");
                } else if(scenario.equals("rollback")) {
                    boolean rejected=false;
                    try { delete(ctx,41L,"{\"id\":301}"); }
                    catch(org.springframework.dao.DataIntegrityViolationException expected) { rejected=true; }
                    equal(true,rejected,"real JDBC audit CHECK failure after child deletion propagates"); equal(1,store.deletes,"child DELETE executed before constraint failure");
                    equal("seal A",service.selectById(301L).getSealName(),"transaction restores deleted child"); equal("seal B",service.selectById(302L).getSealName(),"other child unchanged");
                    equal(1,store.attachmentCalls,"attachment delegation preceded failed delete completion"); equal(2,store.audit("ATTACHMENT_FIXTURE"),"rollback restores deleted attachment fixture");
                    equal(0,store.audit("AUDIT_RECORD"),"rollback retains no audit header"); equal(0,store.audit("AUDIT_LINE"),"rollback retains no audit line");
                } else throw new AssertionError("Unknown scenario "+scenario);
                equal(null,AuditTrailAspect.getAuditTrailEntryData(),"audit entry thread state cleaned");
            } finally { RequestContextHolder.resetRequestAttributes(); LocalContextHelper.removeServletRequest(); store.db.execute("SHUTDOWN"); SunwayAopContext.setCurrentProxy(null); }
        }
        System.out.println(scenario + ": PASS (original Resource.deleteRuleDetails + Service + CoreTable cascade + MybatisDaoSupport + audit/TX + JDBC/H2)");
    }
    public static void main(String[] args) throws Exception { run(args[0]); }
}
