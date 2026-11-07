import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.invoices.bean.*;
import com.sunwayworld.emc.module.mdm.invoices.dao.*;
import com.sunwayworld.emc.module.mdm.invoices.service.*;
import com.sunwayworld.emc.module.mdm.invoices.service.impl.*;
import com.sunwayworld.emc.module.mdm.invoices.resource.impl.EmcInvoiceResourceImpl;
import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.aspect.AuditTrailAspect;
import com.sunwayworld.framework.audit.AuditDatabaseOperation;
import com.sunwayworld.framework.context.*;
import com.sunwayworld.framework.database.context.*;
import com.sunwayworld.framework.database.dialect.Dialect;
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
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.transaction.annotation.*;
import org.springframework.web.context.request.*;

/** Public service CRUD/readback and audit boundary. JDBC adapters persist to disposable H2;
 * actual original services, CoreTableService cascade, audit aspect and Spring TX execute unchanged.
 * This does not claim Oracle/HTTP or strict evidence. SQL fixtures never touch a live database. */
public final class InvoiceReturnedDeleteTest {
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
    static EntityContext entity() {
        EntityContext entity = new EntityContext(); entity.setTableName("T_EMC_RETURNED");
        List<EntityColumnContext> columns = new ArrayList<>();
        for (String name : Arrays.asList("id", "invoiceId", "returnedAmount")) {
            EntityColumnContext col = new EntityColumnContext(); col.setColumnName(name);
            col.setEntityClass(EmcReturnedBean.class); col.setType(name.equals("returnedAmount") ? String.class : Long.class);
            columns.add(col); if (name.equals("id")) entity.setIdContext(col);
        }
        entity.setColumnContextList(columns); return entity;
    }
    static final class Store {
        final JdbcTemplate db;
        Store(DataSource source) { db = new JdbcTemplate(source); }
        List<EmcReturnedBean> returns(String where, Object... params) {
            return db.query("SELECT ID, INVOICEID, RETURNEDAMOUNT FROM T_EMC_RETURNED " + where, (rs,n) -> {
                EmcReturnedBean row = new EmcReturnedBean(); row.setId(rs.getLong(1));
                row.setInvoiceId(rs.getLong(2)); row.setReturnedAmount(rs.getString(3)); return row;
            }, params);
        }
        Object returnDao(Method m, Object[] a) {
            switch (m.getName()) {
                case "getType": return EmcReturnedBean.class;
                case "getTable": return "T_EMC_RETURNED";
                case "getEntityContext": return entity();
                case "selectById":
                case "selectByIdIfPresent": {
                    List<EmcReturnedBean> rows = returns("WHERE ID=?", a[0]);
                    EmcReturnedBean selected = rows.isEmpty() ? null : rows.get(0);
                    return m.getName().equals("selectById") ? Objects.requireNonNull(selected) : selected;
                }
                case "selectListByIds": {
                    List<EmcReturnedBean> rows = new ArrayList<>();
                    for (Object id : (List<?>) a[0]) rows.addAll(returns("WHERE ID=?", id)); return rows;
                }
                case "selectListByOneColumnValue":
                    equal("invoiceId", a[1], "remaining rows belong to stored parent"); return returns("WHERE INVOICEID=?", a[0]);
                case "delete": db.update("DELETE FROM T_EMC_RETURNED WHERE ID=?", a[0]); return null;
                case "deleteByIdList":
                    for (Object id : (List<?>) a[0]) db.update("DELETE FROM T_EMC_RETURNED WHERE ID=?", id); return null;
                default: return unused(m,a);
            }
        }
        Object invoiceDao(Method m, Object[] a) {
            if (m.getName().equals("getTable")) return "T_EMC_INVOICE";
            if (m.getName().equals("selectById")) {
                List<EmcInvoiceBean> rows = db.query("SELECT ID, INVOICEAMOUNT, DONEFLAG FROM T_EMC_INVOICE WHERE ID=?", (rs,n) -> {
                    EmcInvoiceBean row = new EmcInvoiceBean(); row.setId(rs.getLong(1));
                    row.setInvoiceAmount(rs.getString(2)); row.setDoneFlag(rs.getString(3)); return row;
                }, a[0]); return Objects.requireNonNull(rows.isEmpty() ? null : rows.get(0));
            }
            if (m.getName().equals("updateIfChanged")) {
                EmcInvoiceBean row = a[0] instanceof List ? (EmcInvoiceBean) ((List<?>) a[0]).get(0) : (EmcInvoiceBean) a[0];
                db.update("UPDATE T_EMC_INVOICE SET DONEFLAG=? WHERE ID=?", row.getDoneFlag(), row.getId()); return null;
            }
            return unused(m,a);
        }
        void schema() {
            db.execute("CREATE TABLE T_EMC_INVOICE(ID BIGINT PRIMARY KEY, INVOICEAMOUNT VARCHAR(40), DONEFLAG VARCHAR(1), LOCK_DONE BOOLEAN DEFAULT FALSE, CHECK (NOT LOCK_DONE OR DONEFLAG='1'))");
            db.execute("CREATE TABLE T_EMC_RETURNED(ID BIGINT PRIMARY KEY, INVOICEID BIGINT REFERENCES T_EMC_INVOICE(ID), RETURNEDAMOUNT VARCHAR(40))");
            db.execute("CREATE TABLE AUDIT_RECORD(ID BIGINT PRIMARY KEY, EVENT VARCHAR(40), TARGET VARCHAR(100))");
            db.execute("CREATE TABLE AUDIT_LINE(RECORD_ID BIGINT, EVENT VARCHAR(40), OLD_VALUE VARCHAR(100))");
        }
        void invoice(long id, String amount, String flag) { db.update("INSERT INTO T_EMC_INVOICE(ID, INVOICEAMOUNT, DONEFLAG) VALUES(?,?,?)", id, amount, flag); }
        void returned(long id, long parent, String amount) { db.update("INSERT INTO T_EMC_RETURNED VALUES(?,?,?)", id,parent,amount); }
        int audit(String table) { return db.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class); }
    }
    static RestJsonWrapperBean body(String... rows) {
        return new RestJsonWrapperBean(Collections.emptyMap(), Collections.singletonList(Arrays.asList(rows)), Collections.singletonList(null));
    }
    static String row(long id) { return "{\"id\":" + id + "}"; }
    static void flag(EmcInvoiceService invoices, long id, String expected) { equal(expected, invoices.selectById(id).getDoneFlag(), "invoice " + id + " doneFlag"); }
    static void gone(EmcReturnedService service, long id) { equal(null, service.selectByIdIfPresent(id), "deleted return readback"); }
    static void localIdentity() throws Exception {
        Constructor<?> ctor = Class.forName("com.sunwayworld.framework.id.SnowflakeIdWorker").getDeclaredConstructor(long.class);
        ctor.setAccessible(true); Field worker = GikamIdHelper.class.getDeclaredField("worker"); worker.setAccessible(true); worker.set(null, ctor.newInstance(1L));
    }
    static void run(String scenario) throws Exception {
        localIdentity();
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.getEnvironment().setActiveProfiles("audit-trail");
            ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture", Collections.singletonMap(AuditDatabaseOperation.DELETE.getPropertyKey(), true)));
            DataSource ds = new DriverManagerDataSource("jdbc:h2:mem:return_delete;DB_CLOSE_DELAY=-1", "sa", "");
            Store store = new Store(ds); store.schema();
            ctx.registerBean(DataSource.class, () -> ds);
            ctx.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(ds));
            ctx.registerBean(EmcReturnedDao.class, () -> adapter(EmcReturnedDao.class, store::returnDao));
            ctx.registerBean(EmcInvoiceDao.class, () -> adapter(EmcInvoiceDao.class, store::invoiceDao));
            ctx.registerBean(EmcContractService.class, () -> adapter(EmcContractService.class, InvoiceReturnedDeleteTest::unused));
            ctx.registerBean(CoreTableDao.class, () -> adapter(CoreTableDao.class, (m,a) -> {
                if (m.getName().equals("getTable")) return "T_CORE_TABLE";
                if (m.getName().equals("selectByIdIfPresent")) return null; return unused(m,a);
            }));
            ctx.registerBean(CoreTableHierarchyDao.class, () -> adapter(CoreTableHierarchyDao.class, (m,a) -> {
                if (m.getName().equals("selectList")) return Collections.emptyList(); return unused(m,a);
            }));
            ctx.registerBean(CoreTablePermissionDao.class, () -> adapter(CoreTablePermissionDao.class, InvoiceReturnedDeleteTest::unused));
            ctx.registerBean(CoreMetadataMapper.class, () -> adapter(CoreMetadataMapper.class, InvoiceReturnedDeleteTest::unused));
            ctx.registerBean(CoreFileService.class, () -> adapter(CoreFileService.class, InvoiceReturnedDeleteTest::unused));
            ctx.registerBean(CoreBpmnCommentService.class, () -> adapter(CoreBpmnCommentService.class, InvoiceReturnedDeleteTest::unused));
            ctx.registerBean(CoreBpmnTargetService.class, () -> adapter(CoreBpmnTargetService.class, InvoiceReturnedDeleteTest::unused));
            ctx.registerBean(Dialect.class, () -> adapter(Dialect.class, InvoiceReturnedDeleteTest::unused));
            CoreAuditTrailConfigService config = adapter(CoreAuditTrailConfigService.class, (m,a) -> {
                if (m.getName().equals("auditTrailable")) return true;
                if (m.getName().equals("selectAuditTrailableLineList")) {
                    List<CoreAuditTrailConfigLineBean> lines = new ArrayList<>();
                    for (String name : Arrays.asList("id", "invoiceId", "returnedAmount")) {
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
                        store.db.update("INSERT INTO AUDIT_LINE VALUES(?,?,?)", line.getRecordId(), line.getRecordType(), line.getOldValue());
                    return null;
                } return unused(m,a);
            }));
            ctx.register(Transactions.class, EmcReturnedServiceImpl.class, EmcInvoiceServiceImpl.class, EmcInvoiceResourceImpl.class, CoreTableServiceImpl.class, AuditTrailAspect.class);
            ApplicationContextHelper.setApplicationContext(ctx); ctx.refresh(); GikamBeanContext.init();
            EmcReturnedService service = ctx.getBean(EmcReturnedService.class);
            EmcInvoiceService invoices = ctx.getBean(EmcInvoiceService.class);
            equal(true, AopUtils.isAopProxy(service), "service runs through Spring transaction and audit proxy");
            HttpServletRequest request = adapter(HttpServletRequest.class, (m,a) -> {
                if (m.getName().equals("getHeader")) {
                    if (a[0].equals("primaryId")) return "41"; return null;
                }
                if (m.getName().equals("getAttribute")) return null;
                if (m.getName().equals("setAttribute") || m.getName().equals("removeAttribute")) return null;
                return unused(m,a);
            });
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            try {
                store.invoice(41, "100", "1"); store.returned(301,41,"30"); store.returned(302,41,"70");
                if (scenario.equals("partial")) {
                    ctx.getBean(EmcInvoiceResourceImpl.class).deleteReturns(body(row(301))); gone(service,301); flag(invoices,41,"0");
                    equal("70", service.selectById(302L).getReturnedAmount(), "remaining return unchanged");
                    equal(1,store.audit("AUDIT_RECORD"),"DELETE audit record committed");
                    equal(3,store.audit("AUDIT_LINE"),"original cascade audits stored child columns");
                    equal("DELETE",store.db.queryForObject("SELECT EVENT FROM AUDIT_RECORD",String.class),"DELETE audit semantics");
                    equal("30",store.db.queryForObject("SELECT OLD_VALUE FROM AUDIT_LINE WHERE OLD_VALUE='30'",String.class),"audit old return amount");
                } else if (scenario.equals("full")) {
                    service.delete(body(row(301),row(302))); gone(service,301); gone(service,302); flag(invoices,41,"0");
                } else if (scenario.equals("multiple")) {
                    store.invoice(42,"200","1"); store.returned(401,42,"200");
                    store.invoice(43,"100","1"); store.returned(501,43,"100"); store.returned(502,43,"0");
                    service.delete(body(row(301),row(401),row(502))); flag(invoices,41,"0"); flag(invoices,42,"0"); flag(invoices,43,"1");
                    gone(service,301); gone(service,401); gone(service,502);
                } else if (scenario.equals("forged")) {
                    store.invoice(42,"200","1");
                    service.delete(body("{\"id\":301,\"invoiceId\":42}")); gone(service,301); flag(invoices,41,"0"); flag(invoices,42,"1");
                } else if (scenario.equals("empty") || scenario.equals("missing")) {
                    store.db.update("UPDATE T_EMC_INVOICE SET LOCK_DONE=TRUE WHERE ID=41");
                    service.delete(scenario.equals("empty") ? body() : body(row(999)));
                    flag(invoices,41,"1"); equal(2,service.selectListByIds(Arrays.asList(301L,302L)).size(),"no-op keeps child rows");
                    equal(0,store.audit("AUDIT_RECORD"),"no-op produces no deletion audit");
                    if (scenario.equals("empty")) {
                        boolean rejected = false;
                        try { service.delete(body("{}")); } catch (NullPointerException expected) { rejected = true; }
                        equal(true,rejected,"missing return identity cannot select or delete arbitrary rows");
                        equal(2,service.selectListByIds(Arrays.asList(301L,302L)).size(),"invalid request keeps child rows");
                    }
                } else if (scenario.equals("direct")) {
                    service.delete(301L); gone(service,301); flag(invoices,41,"0");
                } else if (scenario.equals("rollback") || scenario.equals("direct-rollback")) {
                    store.db.update("UPDATE T_EMC_INVOICE SET LOCK_DONE=TRUE WHERE ID=41");
                    boolean rejected = false;
                    try { if (scenario.equals("rollback")) service.delete(body(row(301))); else service.delete(301L); }
                    catch (org.springframework.dao.DataIntegrityViolationException expected) { rejected = true; }
                    equal(true,rejected,"real database rejects parent flag update after child deletion");
                    equal("30",service.selectById(301L).getReturnedAmount(),"Spring JDBC rollback restores deleted child");
                    flag(invoices,41,"1"); equal(0,store.audit("AUDIT_RECORD"),"failed transaction retains no audit record"); equal(0,store.audit("AUDIT_LINE"),"failed transaction retains no audit lines");
                } else if (scenario.equals("missing-amount")) {
                    for (String amount : Arrays.asList(null, "", " \t ")) {
                        store.db.update("UPDATE T_EMC_INVOICE SET INVOICEAMOUNT=? WHERE ID=41", amount);
                        boolean rejected = false;
                        try { service.delete(body(row(301))); }
                        catch (AuthenticationServiceException expected) { rejected = true; equal("请输入开票金额！",expected.getMessage(),"existing amount guard"); }
                        equal(true,rejected,"unknown amount cannot infer completion");
                        equal("30",service.selectById(301L).getReturnedAmount(),"amount guard rolls deletion back"); flag(invoices,41,"1");
                        equal(0,store.audit("AUDIT_RECORD"),"amount guard retains no deletion audit");
                    }
                } else throw new AssertionError("Unknown case " + scenario);
                equal(null,AuditTrailAspect.getAuditTrailEntryData(),"audit entry thread state cleaned");
            } finally { RequestContextHolder.resetRequestAttributes(); store.db.execute("SHUTDOWN"); }
        }
        System.out.println(scenario + ": PASS (original Service + CoreTable cascade + Spring audit/TX + JDBC/H2)");
    }
    public static void main(String[] args) throws Exception { run(args[0]); }
}
