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
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.transaction.annotation.*;
import org.springframework.web.context.request.*;

/** Public service CRUD/readback and audit boundary. JDBC adapters persist to disposable H2;
 * actual original services, MybatisDaoSupport update, audit aspect and Spring TX execute unchanged.
 * This does not claim Oracle/HTTP or strict evidence. SQL fixtures never touch a live database. */
public final class InvoiceReturnedUpdateTest {
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
            col.setEntityClass(beanClass); col.setType(name.equals("id") || name.equals("invoiceId") ? Long.class : String.class);
            columns.add(col); if (name.equals("id")) entity.setIdContext(col);
        }
        entity.setColumnContextList(columns); return entity;
    }
    static final EntityContext RETURNS = entity(EmcReturnedBean.class,"T_EMC_RETURNED",
            "id","invoiceId","returnedAmount","invoiceAmount","uncollectedAmount","remark");
    static final EntityContext INVOICES = entity(EmcInvoiceBean.class,"T_EMC_INVOICE","id","invoiceAmount","doneFlag");
    /** Only the database/metadata-cache boundary is adapted. Real framework patch merging,
     * changed-column selection, old/new audit generation and Spring transactions execute. */
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
    public static class ReturnDao extends MybatisDaoSupport<EmcReturnedBean,Long> implements EmcReturnedDao {
        final Store store;
        ReturnDao(Store store) { this.store=store; }
        @Override public EntityContext getEntityContext() { return RETURNS; }
        @Override public String getTable() { return "T_EMC_RETURNED"; }
        @Override public EmcReturnedBean selectById(Long id) { return Objects.requireNonNull(selectByIdIfPresent(id)); }
        @Override public EmcReturnedBean selectByIdIfPresent(Long id) {
            List<EmcReturnedBean> rows=store.returns("WHERE ID=?", id); return rows.isEmpty()?null:rows.get(0);
        }
        @Override public List<EmcReturnedBean> selectListByIds(List<Long> ids, List<String> cols, Order... order) {
            List<EmcReturnedBean> rows=new ArrayList<>(); for (Long id:ids) rows.addAll(store.returns("WHERE ID=?",id)); return rows;
        }
        @Override public <V> List<EmcReturnedBean> selectListByOneColumnValue(V value,String col,Order... order) {
            equal("invoiceId",col,"stored parent scopes returns"); return store.returns("WHERE INVOICEID=?",value);
        }
    }
    public static class InvoiceDao extends MybatisDaoSupport<EmcInvoiceBean,Long> implements EmcInvoiceDao {
        final Store store;
        InvoiceDao(Store store) { this.store=store; }
        @Override public EntityContext getEntityContext() { return INVOICES; }
        @Override public String getTable() { return "T_EMC_INVOICE"; }
        @Override public EmcInvoiceBean selectById(Long id) { return Objects.requireNonNull(selectByIdIfPresent(id)); }
        @Override public EmcInvoiceBean selectByIdIfPresent(Long id) { return store.invoice(id); }
        @Override public List<EmcInvoiceBean> selectListByIds(List<Long> ids,List<String> cols,Order... order) {
            List<EmcInvoiceBean> rows=new ArrayList<>(); for(Long id:ids) { EmcInvoiceBean row=store.invoice(id); if(row!=null) rows.add(row); } return rows;
        }
    }
    static void mapper(Object dao, Store store) throws Exception {
        Field field=MybatisDaoSupport.class.getDeclaredField("daoMapper"); field.setAccessible(true);
        field.set(dao,adapter(DaoMapper.class,(m,a) -> {
            if(!m.getName().equals("updateById")) return unused(m,a);
            EntityContextInstance item=(EntityContextInstance)a[0];
            List<String> assignments=new ArrayList<>(); List<Object> values=new ArrayList<>();
            for(EntityColumnContextInstance col:item.getColumnContextInstanceList()) if(col.isActive()) {
                assignments.add(col.getColumnContext().getColumnName()+"=?"); values.add(col.getValue());
            }
            values.add(item.getIdInstance().getValue());
            int count=store.db.update("UPDATE "+item.getContext().getTableName()+" SET "+String.join(",",assignments)+" WHERE ID=?",values.toArray());
            if(item.getContext().getTableName().equals("T_EMC_RETURNED")) store.childUpdates++;
            return count;
        }));
    }
    static final class Store {
        final JdbcTemplate db;
        Store(DataSource source) { db = new JdbcTemplate(source); }
        int childUpdates;
        List<EmcReturnedBean> returns(String where,Object... params) {
            return db.query("SELECT ID,INVOICEID,RETURNEDAMOUNT,INVOICEAMOUNT,UNCOLLECTEDAMOUNT,REMARK FROM T_EMC_RETURNED "+where,(rs,n)-> {
                EmcReturnedBean row=new EmcReturnedBean(); row.setId(rs.getLong(1)); row.setInvoiceId(rs.getLong(2));
                row.setReturnedAmount(rs.getString(3)); row.setInvoiceAmount(rs.getString(4)); row.setUncollectedAmount(rs.getString(5)); row.setRemark(rs.getString(6)); return row;
            },params);
        }
        EmcInvoiceBean invoice(Long id) {
            List<EmcInvoiceBean> rows=db.query("SELECT ID,INVOICEAMOUNT,DONEFLAG FROM T_EMC_INVOICE WHERE ID=?",(rs,n)-> {
                EmcInvoiceBean row=new EmcInvoiceBean(); row.setId(rs.getLong(1)); row.setInvoiceAmount(rs.getString(2)); row.setDoneFlag(rs.getString(3)); return row;
            },id); return rows.isEmpty()?null:rows.get(0);
        }
        void schema() {
            db.execute("CREATE TABLE T_EMC_INVOICE(ID BIGINT PRIMARY KEY, INVOICEAMOUNT VARCHAR(40), DONEFLAG VARCHAR(1), LOCK_DONE BOOLEAN DEFAULT FALSE, CHECK (NOT LOCK_DONE OR DONEFLAG='1'))");
            db.execute("CREATE TABLE T_EMC_RETURNED(ID BIGINT PRIMARY KEY, INVOICEID BIGINT REFERENCES T_EMC_INVOICE(ID), RETURNEDAMOUNT VARCHAR(40),INVOICEAMOUNT VARCHAR(40),UNCOLLECTEDAMOUNT VARCHAR(40),REMARK VARCHAR(100))");
            db.execute("CREATE TABLE AUDIT_RECORD(ID BIGINT PRIMARY KEY, EVENT VARCHAR(40), TARGET VARCHAR(100))");
            db.execute("CREATE TABLE AUDIT_LINE(RECORD_ID BIGINT, EVENT VARCHAR(40), OLD_VALUE VARCHAR(100),NEW_VALUE VARCHAR(100),COL VARCHAR(60))");
        }
        void invoice(long id, String amount, String flag) { db.update("INSERT INTO T_EMC_INVOICE(ID, INVOICEAMOUNT, DONEFLAG) VALUES(?,?,?)", id, amount, flag); }
        void returned(long id, long parent, String amount) { db.update("INSERT INTO T_EMC_RETURNED VALUES(?,?,?,?,?,?)", id,parent,amount,"100",id==301?"70":"0","original note"); }
        int audit(String table) { return db.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class); }
    }
    static RestJsonWrapperBean body(EmcReturnedService service,String... rows) {
        RestJsonWrapperBean wrapper=new RestJsonWrapperBean(Collections.emptyMap(),Collections.singletonList(Arrays.asList(rows)),Collections.singletonList(service));
        for(EmcReturnedBean bean:wrapper.parse(EmcReturnedBean.class,0))
            equal(true,bean instanceof BeanPropertyListener,"actual JSON partial-property proxy");
        return wrapper;
    }
    static void save(AnnotationConfigApplicationContext ctx,String... rows) {
        ctx.getBean(EmcInvoiceResourceImpl.class).save(41L,body(ctx.getBean(EmcReturnedService.class),rows));
    }
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
            ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("fixture", new HashMap<String,Object>() {{ put(AuditDatabaseOperation.UPDATE.getPropertyKey(),true); put("spring.datasource.driver-class-name","org.h2.Driver"); }}));
            DataSource ds = new DriverManagerDataSource("jdbc:h2:mem:return_update;DB_CLOSE_DELAY=-1", "sa", "");
            Store store = new Store(ds); store.schema();
            ctx.registerBean(DataSource.class, () -> ds);
            ctx.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(ds));
            ReturnDao returnDao=new ReturnDao(store); InvoiceDao invoiceDao=new InvoiceDao(store);
            mapper(returnDao,store); mapper(invoiceDao,store);
            ctx.getBeanFactory().registerSingleton("emcReturnedDao",returnDao);
            ctx.getBeanFactory().registerSingleton("emcInvoiceDao",invoiceDao);
            LocalMetadataCache metadata=new LocalMetadataCache();
            metadata.values.put("EC::"+EmcReturnedBean.class.toString(),RETURNS);
            metadata.values.put("EC::"+EmcInvoiceBean.class.toString(),INVOICES);
            new RedisHelper().setRedisTemplate(metadata);
            ctx.registerBean(EmcContractService.class, () -> adapter(EmcContractService.class, InvoiceReturnedUpdateTest::unused));
            ctx.registerBean(CoreTableDao.class, () -> adapter(CoreTableDao.class, (m,a) -> {
                if (m.getName().equals("getTable")) return "T_CORE_TABLE";
                if (m.getName().equals("selectByIdIfPresent")) return null; return unused(m,a);
            }));
            ctx.registerBean(CoreTableHierarchyDao.class, () -> adapter(CoreTableHierarchyDao.class, (m,a) -> {
                if (m.getName().equals("selectList")) return Collections.emptyList(); return unused(m,a);
            }));
            ctx.registerBean(CoreTablePermissionDao.class, () -> adapter(CoreTablePermissionDao.class, InvoiceReturnedUpdateTest::unused));
            ctx.registerBean(CoreMetadataMapper.class, () -> adapter(CoreMetadataMapper.class, InvoiceReturnedUpdateTest::unused));
            ctx.registerBean(CoreFileService.class, () -> adapter(CoreFileService.class, InvoiceReturnedUpdateTest::unused));
            ctx.registerBean(CoreBpmnCommentService.class, () -> adapter(CoreBpmnCommentService.class, InvoiceReturnedUpdateTest::unused));
            ctx.registerBean(CoreBpmnTargetService.class, () -> adapter(CoreBpmnTargetService.class, InvoiceReturnedUpdateTest::unused));
            ctx.registerBean(Dialect.class, () -> adapter(Dialect.class, InvoiceReturnedUpdateTest::unused));
            CoreAuditTrailConfigService config = adapter(CoreAuditTrailConfigService.class, (m,a) -> {
                if (m.getName().equals("auditTrailable")) return true;
                if (m.getName().equals("selectAuditTrailableLineList")) {
                    List<CoreAuditTrailConfigLineBean> lines = new ArrayList<>();
                    for (String name : Arrays.asList("id","invoiceId","returnedAmount","invoiceAmount","uncollectedAmount","remark","doneFlag")) {
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
            SunwayAopContext.setCurrentProxy(returnDao);
            try {
                store.invoice(41, "100", "1"); store.returned(301,41,"30"); store.returned(302,41,"70");
                if (scenario.equals("partial")) {
                    save(ctx,"{\"id\":302,\"returnedAmount\":\"60\"}");
                    equal("60",service.selectById(302L).getReturnedAmount(),"saved return amount");
                    flag(invoices,41,"0");
                    equal("10",service.selectById(302L).getUncollectedAmount(),"server computes remaining amount");
                    equal("70",service.selectById(301L).getUncollectedAmount(),"historic sibling snapshot unchanged");
                    equal("original note",service.selectById(302L).getRemark(),"omitted note preserved");
                    equal(2,store.audit("AUDIT_RECORD"),"child and parent UPDATE audit committed");
                    equal(2,store.db.queryForObject("SELECT COUNT(*) FROM AUDIT_RECORD WHERE EVENT='UPDATE'",Integer.class),"original UPDATE audit event type");
                    equal("1",store.db.queryForObject("SELECT OLD_VALUE FROM AUDIT_LINE WHERE COL='DONEFLAG'",String.class),"audit previous parent completion");
                    equal("0",store.db.queryForObject("SELECT NEW_VALUE FROM AUDIT_LINE WHERE COL='DONEFLAG'",String.class),"audit new parent completion");
                    equal("70",store.db.queryForObject("SELECT OLD_VALUE FROM AUDIT_LINE WHERE COL='RETURNEDAMOUNT'",String.class),"audit previous amount");
                    equal("60",store.db.queryForObject("SELECT NEW_VALUE FROM AUDIT_LINE WHERE COL='RETURNEDAMOUNT'",String.class),"audit new amount");
                    save(ctx,"{\"id\":302,\"returnedAmount\":\"70\"}");
                    flag(invoices,41,"1"); equal("0",service.selectById(302L).getUncollectedAmount(),"returning to full payment recomputes completion");
                } else if(scenario.equals("overpay")) {
                    boolean rejected=false;
                    try { save(ctx,"{\"id\":302,\"returnedAmount\":\"200\"}"); }
                    catch(AuthenticationServiceException expected) { rejected=true; equal("输入的回款金额大于未回款金额！",expected.getMessage(),"existing overpay message"); }
                    equal(true,rejected,"overpayment rejected before persistence");
                    equal("70",service.selectById(302L).getReturnedAmount(),"rejected update leaves amount unchanged"); flag(invoices,41,"1");
                    equal(0,store.childUpdates,"no child SQL before validation"); equal(0,store.audit("AUDIT_RECORD"),"rejection has no audit");
                } else if(scenario.equals("batch-net")) {
                    save(ctx,"{\"id\":302,\"returnedAmount\":\"90\"}","{\"id\":301,\"returnedAmount\":\"10\"}");
                    equal("90",service.selectById(302L).getReturnedAmount(),"net offset accepts final amount despite first row increase");
                    equal("10",service.selectById(301L).getReturnedAmount(),"offset reduction saved"); flag(invoices,41,"1");
                    equal("0",service.selectById(301L).getUncollectedAmount(),"batch uses final balance");
                    equal("0",service.selectById(302L).getUncollectedAmount(),"both changed rows use final balance");
                } else if(scenario.equals("batch-partial")) {
                    save(ctx,"{\"id\":301,\"returnedAmount\":\"20\"}","{\"id\":302,\"returnedAmount\":\"60\"}");
                    flag(invoices,41,"0"); equal("20",service.selectById(301L).getUncollectedAmount(),"batch balance from both changes");
                    equal("20",service.selectById(302L).getUncollectedAmount(),"same parent final balance");
                } else if(scenario.equals("multiple-parent-valid")) {
                    store.invoice(42,"200","1"); store.returned(401,42,"200");
                    save(ctx,"{\"id\":302,\"returnedAmount\":\"60\"}","{\"id\":401,\"returnedAmount\":\"190\"}","{\"id\":301,\"returnedAmount\":\"30\"}");
                    equal("60",service.selectById(302L).getReturnedAmount(),"first parent update persisted");
                    equal("190",service.selectById(401L).getReturnedAmount(),"second parent update persisted");
                    equal("100",service.selectById(302L).getInvoiceAmount(),"first parent amount isolated");
                    equal("200",service.selectById(401L).getInvoiceAmount(),"second parent amount isolated");
                    equal("10",service.selectById(302L).getUncollectedAmount(),"first parent balance");
                    equal("10",service.selectById(401L).getUncollectedAmount(),"second parent balance");
                    equal("70",service.selectById(301L).getUncollectedAmount(),"explicit unchanged sibling retains historical snapshot");
                    flag(invoices,41,"0"); flag(invoices,42,"0");
                } else if(scenario.equals("batch-overpay") || scenario.equals("multiple-parent")) {
                    store.invoice(42,"200","1"); store.returned(401,42,"200");
                    boolean rejected=false;
                    try {
                        if(scenario.equals("batch-overpay")) save(ctx,"{\"id\":301,\"returnedAmount\":\"20\"}","{\"id\":302,\"returnedAmount\":\"90\"}");
                        else save(ctx,"{\"id\":302,\"returnedAmount\":\"60\"}","{\"id\":401,\"returnedAmount\":\"201\"}");
                    } catch(AuthenticationServiceException expected) { rejected=true; }
                    equal(true,rejected,"entire batch validates before writing any row");
                    equal("30",service.selectById(301L).getReturnedAmount(),"first return unchanged");
                    equal("70",service.selectById(302L).getReturnedAmount(),"second return unchanged");
                    equal("200",service.selectById(401L).getReturnedAmount(),"other parent return unchanged");
                    flag(invoices,41,"1"); flag(invoices,42,"1"); equal(0,store.childUpdates,"validation precedes first update");
                    equal(0,store.audit("AUDIT_LINE"),"failed validation leaves no audit lines");
                } else if(scenario.equals("forged") || scenario.equals("notes")) {
                    store.invoice(42,"999","0");
                    String amount=scenario.equals("forged")?",\"returnedAmount\":\"60\"":"";
                    save(ctx,"{\"id\":302,\"invoiceId\":42,\"invoiceAmount\":\"9999\",\"uncollectedAmount\":\"-999\",\"remark\":\"updated note\""+amount+"}");
                    EmcReturnedBean saved=service.selectById(302L);
                    equal(41L,saved.getInvoiceId(),"stored relationship cannot be redirected");
                    equal("100",saved.getInvoiceAmount(),"invoice amount cannot be forged");
                    equal(scenario.equals("forged")?"10":"0",saved.getUncollectedAmount(),"balance comes from server-owned amount");
                    equal(scenario.equals("forged")?"60":"70",saved.getReturnedAmount(),"omitted amount remains unchanged");
                    equal("updated note",saved.getRemark(),"legitimate note update persists");
                    equal("70",service.selectById(301L).getUncollectedAmount(),"historic sibling snapshot preserved");
                    flag(invoices,41,scenario.equals("forged")?"0":"1"); flag(invoices,42,"0");
                    equal("999",invoices.selectById(42L).getInvoiceAmount(),"unrelated invoice unchanged");
                } else if(scenario.equals("clear-note")) {
                    save(ctx,"{\"id\":302,\"remark\":null}");
                    equal("original note",service.selectById(302L).getRemark(),"original JSON parser treats null as omitted");
                    EmcReturnedBean patch=com.sunwayworld.framework.utils.BeanUtils.getPropertyListenerProxy(EmcReturnedBean.class);
                    patch.setId(302L); patch.setRemark(null); service.updateIfChanged(patch);
                    equal(null,service.selectById(302L).getRemark(),"explicit proxy null retains original clearing semantics");
                    equal("70",service.selectById(302L).getReturnedAmount(),"omitted amount survives null note"); flag(invoices,41,"1");
                } else if(scenario.equals("raw-notes")) {
                    EmcReturnedBean patch=new EmcReturnedBean(); patch.setId(302L); patch.setRemark("raw note");
                    patch.setInvoiceId(42L); patch.setInvoiceAmount("9999"); patch.setUncollectedAmount("-999");
                    service.updateIfChanged(patch);
                    EmcReturnedBean saved=service.selectById(302L);
                    equal("raw note",saved.getRemark(),"raw note change persists"); equal("70",saved.getReturnedAmount(),"raw omitted amount preserved");
                    equal(41L,saved.getInvoiceId(),"raw forged parent ignored"); equal("100",saved.getInvoiceAmount(),"raw invoice snapshot protected");
                    equal("0",saved.getUncollectedAmount(),"raw balance protected"); equal("70",service.selectById(301L).getUncollectedAmount(),"historic sibling remains untouched"); flag(invoices,41,"1");
                } else if(scenario.equals("direct")) {
                    EmcReturnedBean patch=new EmcReturnedBean(); patch.setId(302L); patch.setReturnedAmount("60");
                    service.updateIfChanged(patch); flag(invoices,41,"0");
                    equal("10",service.selectById(302L).getUncollectedAmount(),"single raw-bean update computes balance");
                    equal("original note",service.selectById(302L).getRemark(),"raw null property remains omitted");
                } else if(scenario.equals("rollback") || scenario.equals("direct-rollback")) {
                    store.db.update("UPDATE T_EMC_INVOICE SET LOCK_DONE=TRUE WHERE ID=41");
                    boolean rejected=false;
                    try {
                        if(scenario.equals("rollback")) save(ctx,"{\"id\":302,\"returnedAmount\":\"60\",\"remark\":\"attempted note\"}");
                        else { EmcReturnedBean patch=new EmcReturnedBean(); patch.setId(302L); patch.setReturnedAmount("60"); patch.setRemark("attempted note"); service.updateIfChanged(patch); }
                    } catch(org.springframework.dao.DataIntegrityViolationException expected) { rejected=true; }
                    equal(true,rejected,"real database constraint fails parent after child update"); equal(1,store.childUpdates,"child UPDATE actually executed before parent failure");
                    EmcReturnedBean restored=service.selectById(302L);
                    equal("70",restored.getReturnedAmount(),"transaction restores previous amount");
                    equal("0",restored.getUncollectedAmount(),"transaction restores previous balance");
                    equal("100",restored.getInvoiceAmount(),"transaction restores previous invoice snapshot");
                    equal("original note",restored.getRemark(),"transaction restores previous note"); flag(invoices,41,"1");
                    equal(0,store.audit("AUDIT_RECORD"),"rollback retains no audit record"); equal(0,store.audit("AUDIT_LINE"),"rollback retains no audit lines");
                } else if(scenario.equals("missing") || scenario.equals("null-id") || scenario.equals("null-amount")) {
                    boolean rejected=false;
                    try {
                        if(scenario.equals("missing")) save(ctx,"{\"id\":999,\"returnedAmount\":\"60\"}");
                        else if(scenario.equals("null-id")) { EmcReturnedBean patch=new EmcReturnedBean(); patch.setReturnedAmount("60"); service.updateIfChanged(patch); }
                        else { EmcReturnedBean patch=com.sunwayworld.framework.utils.BeanUtils.getPropertyListenerProxy(EmcReturnedBean.class); patch.setId(302L); patch.setReturnedAmount(null); service.updateIfChanged(patch); }
                    } catch(NullPointerException expected) { rejected=true; }
                    equal(true,rejected,"invalid update never silently inserts or succeeds"); equal("70",service.selectById(302L).getReturnedAmount(),"invalid update retains existing amount");
                    flag(invoices,41,"1"); equal(0,store.childUpdates,"invalid input writes no child"); equal(0,store.audit("AUDIT_RECORD"),"invalid input creates no audit");
                } else if(scenario.equals("missing-amount")) {
                    for(String amount:Arrays.asList(null,""," \t ")) {
                        store.db.update("UPDATE T_EMC_INVOICE SET INVOICEAMOUNT=? WHERE ID=41",amount);
                        boolean rejected=false;
                        try { save(ctx,"{\"id\":302,\"returnedAmount\":\"60\"}"); }
                        catch(AuthenticationServiceException expected) { rejected=true; equal("请输入开票金额！",expected.getMessage(),"existing amount guard"); }
                        equal(true,rejected,"amount change cannot infer absent invoice amount");
                        equal("70",service.selectById(302L).getReturnedAmount(),"amount guard preserves return"); flag(invoices,41,"1");
                    }
                } else if(scenario.equals("duplicate")) {
                    boolean rejected=false;
                    try { save(ctx,"{\"id\":302,\"returnedAmount\":\"60\"}","{\"id\":302,\"returnedAmount\":\"70\"}"); }
                    catch(IllegalArgumentException expected) { rejected=true; equal("Duplicate return id in update batch",expected.getMessage(),"unambiguous duplicate identity error"); }
                    equal(true,rejected,"ambiguous duplicate patches rejected"); equal("70",service.selectById(302L).getReturnedAmount(),"duplicate rejection leaves return unchanged");
                    equal(0,store.childUpdates,"duplicate rejected before first SQL update"); equal(0,store.audit("AUDIT_RECORD"),"duplicate creates no audit"); flag(invoices,41,"1");
                } else if(scenario.equals("empty")) {
                    service.updateIfChanged(Collections.emptyList()); service.updateIfChanged((List<EmcReturnedBean>)null);
                    equal(0,store.childUpdates,"empty batch no-op"); equal(0,store.audit("AUDIT_RECORD"),"empty batch no audit"); flag(invoices,41,"1");
                } else throw new AssertionError("Unknown case "+scenario);

                equal(null,AuditTrailAspect.getAuditTrailEntryData(),"audit entry thread state cleaned");
            } finally { RequestContextHolder.resetRequestAttributes(); store.db.execute("SHUTDOWN"); SunwayAopContext.setCurrentProxy(null); }
        }
        System.out.println(scenario + ": PASS (original Resource.save + wrapper dispatch + Service + MybatisDaoSupport + audit/TX + JDBC/H2)");
    }
    public static void main(String[] args) throws Exception { run(args[0]); }
}
