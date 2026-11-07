import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcInvoiceDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.impl.EmcInvoiceServiceImpl;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Public original invoice Service regression, with real Spring TX and disposable H2.
 * Existing preparation hooks supply runtime sources without production metadata/version checks.
 * BPMN runtime and Redis are explicit boundary fixtures, NOT the engine or live Redis.
 * No HTTP/Oracle/DB-link/audit-aspect/strict claim is made by this test. */
public final class InvoiceContractAllocationTest {
    @Configuration @EnableTransactionManagement(proxyTargetClass=true)
    public static class Transactions {}

    public static class InvoiceService extends EmcInvoiceServiceImpl {
        @Override public void checkAndUpdateVersion(List<EmcInvoiceBean> rows) {}
        @Override public Map<Long, Map<String,Object>> getBpmnVars(RestJsonWrapperBean wrapper, List<EmcInvoiceBean> rows) {
            return Collections.emptyMap();
        }
        private List<CoreBpmnRuntimeSource<EmcInvoiceBean,Long>> sources(List<Long> ids) {
            List<CoreBpmnRuntimeSource<EmcInvoiceBean,Long>> result = new ArrayList<>();
            for (EmcInvoiceBean row : getBpmnVarsItemList(ids))
                result.add(CoreBpmnRuntimeSource.of("T_EMC_INVOICE", row, "fixture", Collections.emptyMap()));
            return result;
        }
        @Override public List<CoreBpmnRuntimeSource<EmcInvoiceBean,Long>> parseSubmittableRuntimeSource(
                List<Long> ids, CoreBpmnParameterDTO parameter, Map<Long,Map<String,Object>> vars) { return sources(ids); }
        @Override public List<CoreBpmnRuntimeSource<EmcInvoiceBean,Long>> parseAuditableRuntimeSource(
                List<Long> ids, CoreBpmnParameterDTO parameter, Map<Long,Map<String,Object>> vars) { return sources(ids); }
    }
    interface Call { Object invoke(Method m, Object[] a) throws Throwable; }
    static <T> T adapter(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (p,m,a) -> {
            if (m.getDeclaringClass() == Object.class) {
                if (m.getName().equals("toString")) return "fixture " + type.getSimpleName();
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
            }
            return call.invoke(m,a);
        }));
    }
    static Object unexpected(Method m, Object[] a) { throw new AssertionError("Unexpected fixture call " + m); }
    static void equal(Object expected, Object actual, String reason) {
        if (!Objects.equals(expected, actual)) throw new AssertionError(reason + ": expected " + expected + ", got " + actual);
    }
    /** Executes the real locker callback against a bounded Redis boundary fixture; no network. */
    static final class LocalRedis extends RedisTemplate<String,Object> {
        final Map<Object,Object> locks = new HashMap<>();
        final ValueOperations<String,Object> values = adapter(ValueOperations.class, (m,a) -> {
            if (m.getName().equals("get")) return locks.get(a[0]);
            if (m.getName().equals("setIfAbsent")) return locks.putIfAbsent(a[0], a[1]) == null;
            if (m.getName().equals("getOperations")) return this;
            return unexpected(m,a);
        });
        @Override public ValueOperations<String,Object> opsForValue() { return values; }
        @Override public Boolean delete(String key) { return locks.remove(key) != null; }
        @Override public <T> T execute(SessionCallback<T> callback) { return callback.execute(this); }
    }
    static final class Store {
        final JdbcTemplate db;
        boolean workflowFailure;
        final List<Long> updatedContracts = new ArrayList<>();
        List<CoreBpmnInstanceStatusDTO<Long>> lastStatuses;
        Store(DataSource ds) { db = new JdbcTemplate(ds); }
        void schema() {
            db.execute("CREATE TABLE CONTRACT(ID BIGINT PRIMARY KEY, AMOUNT VARCHAR(40), LOCKED BOOLEAN DEFAULT FALSE, CHECK (NOT LOCKED OR AMOUNT='500'))");
            db.execute("CREATE TABLE INVOICE(ID BIGINT PRIMARY KEY, CONTRACT_ID BIGINT, AMOUNT VARCHAR(40), STATUS VARCHAR(20))");
            db.execute("CREATE TABLE WORKFLOW_EVENT(INVOICE_ID BIGINT, ACTION VARCHAR(20))");
            db.update("INSERT INTO CONTRACT(ID, AMOUNT) VALUES(1,'1000'),(2,'500'),(3,'900')");
            db.update("INSERT INTO INVOICE VALUES(10,1,'100','DRAFT'),(11,1,'50','DRAFT'),(12,2,'25','DRAFT'),(13,3,'300','DRAFT')");
        }
        List<EmcInvoiceBean> invoices(List<?> ids) {
            List<EmcInvoiceBean> rows = new ArrayList<>();
            for (Object id : new LinkedHashSet<>(ids)) rows.addAll(db.query("SELECT * FROM INVOICE WHERE ID=?", (rs,n) -> {
                EmcInvoiceBean row = new EmcInvoiceBean(); row.setId(rs.getLong("ID"));
                row.setContractId(rs.getString("CONTRACT_ID")); row.setInvoiceAmount(rs.getString("AMOUNT"));
                row.setProcessStatus(rs.getString("STATUS")); return row;
            }, id));
            return rows;
        }
        EmcContractBean contract(long id) {
            return db.queryForObject("SELECT * FROM CONTRACT WHERE ID=?", (rs,n) -> {
                EmcContractBean row = new EmcContractBean(); row.setId(rs.getLong("ID"));
                row.setExt$Item("uninvoicedAmount", rs.getString("AMOUNT")); return row;
            }, id);
        }
        Object invoiceDao(Method m, Object[] a) {
            if (m.getName().equals("getType")) return EmcInvoiceBean.class;
            if (m.getName().equals("getTable")) return "T_EMC_INVOICE";
            if (m.getName().equals("selectListByIds")) return invoices((List<?>)a[0]);
            if (m.getName().equals("selectById")) return invoices(Collections.singletonList(a[0])).get(0);
            return unexpected(m,a);
        }
        Object contractDao(Method m, Object[] a) {
            if (m.getName().equals("selectById")) return contract((Long)a[0]);
            if (m.getName().equals("update")) {
                equal(Collections.singletonList("uninvoicedAmount"), Arrays.asList((String[])a[1]), "only original monetary column updated");
                for (EmcContractBean row : (List<EmcContractBean>)a[0]) {
                    db.update("UPDATE CONTRACT SET AMOUNT=? WHERE ID=?", row.getExt$Item("uninvoicedAmount"), row.getId());
                    updatedContracts.add(row.getId());
                }
                return null;
            }
            return unexpected(m,a);
        }
        Object runtime(Method m, Object[] a) {
            if (!m.getName().equals("startProcess") && !m.getName().equals("rejectTask")) return unexpected(m,a);
            equal(true, TransactionSynchronizationManager.isActualTransactionActive(), "public workflow has a real transaction");
            List<CoreBpmnInstanceStatusDTO<Long>> result = new ArrayList<>();
            for (CoreBpmnRuntimeSource<EmcInvoiceBean,Long> source : (List<CoreBpmnRuntimeSource<EmcInvoiceBean,Long>>)a[0]) {
                String status = m.getName().equals("startProcess") ? "APPROVE" : "DRAFT_REJECTED";
                db.update("UPDATE INVOICE SET STATUS=? WHERE ID=?", status, source.getId());
                db.update("INSERT INTO WORKFLOW_EVENT VALUES(?,?)", source.getId(), m.getName());
                CoreBpmnInstanceStatusDTO<Long> dto = new CoreBpmnInstanceStatusDTO<>(); dto.setId(source.getId());
                dto.setStatusCode("fixture-status"); dto.setNextStatusCode("fixture-next"); dto.setProcessStatus(ProcessStatus.valueOf(status));
                result.add(dto);
            }
            if (workflowFailure) throw new IllegalStateException("fixture engine failure");
            lastStatuses = result; return result;
        }
    }
    static RestJsonWrapperBean body(long... ids) {
        List<String> rows = new ArrayList<>(); for (long id : ids) rows.add("{\"id\":"+id+"}");
        return new RestJsonWrapperBean(Collections.emptyMap(), Collections.singletonList(rows), Collections.singletonList(null));
    }
    static void amount(EmcContractService contracts, long id, String expected) {
        equal(new BigDecimal(expected), new BigDecimal(contracts.selectById(id).getExt$Item("uninvoicedAmount")), "contract " + id + " amount");
    }
    static void run(String scenario) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            DataSource ds = new DriverManagerDataSource("jdbc:h2:mem:allocation;DB_CLOSE_DELAY=-1", "sa", "");
            Store store = new Store(ds); store.schema(); LocalRedis redis = new LocalRedis(); new RedisHelper().setRedisTemplate(redis);
            GenericDao contractDao = adapter(GenericDao.class, store::contractDao);
            ctx.registerBean(DataSource.class, () -> ds);
            ctx.registerBean(DataSourceTransactionManager.class, () -> new DataSourceTransactionManager(ds));
            ctx.registerBean(EmcInvoiceDao.class, () -> adapter(EmcInvoiceDao.class, store::invoiceDao));
            ctx.registerBean(EmcContractService.class, () -> adapter(EmcContractService.class, (m,a) -> {
                if (m.getName().equals("getDao")) return contractDao;
                if (m.getName().equals("selectById")) return store.contract((Long)a[0]);
                if (m.getName().equals("selectListByFilter")) {
                    List<EmcContractBean> rows = new ArrayList<>();
                    for (Object id : new LinkedHashSet<>(((SearchFilter)a[0]).getStdFilterList().get(0).getMap().values())) rows.add(store.contract(((Number)id).longValue()));
                    rows.sort(Comparator.comparing(EmcContractBean::getId));
                    return rows;
                }
                return unexpected(m,a);
            }));
            ctx.registerBean(CoreBpmnRuntimeService.class, () -> adapter(CoreBpmnRuntimeService.class, store::runtime));
            ctx.register(Transactions.class, InvoiceService.class); ApplicationContextHelper.setApplicationContext(ctx); ctx.refresh();
            EmcInvoiceService invoices = ctx.getBean(EmcInvoiceService.class); EmcContractService contracts = ctx.getBean(EmcContractService.class);
            equal(true, AopUtils.isAopProxy(invoices), "original public Service is Spring proxied");
            if (scenario.equals("mixed")) {
                List<CoreBpmnInstanceStatusDTO<Long>> statuses = invoices.startProcess(body(10,11,12));
                equal(true, store.lastStatuses == statuses, "original runtime status list identity preserved");
                equal("fixture-next", statuses.get(0).getNextStatusCode(), "original DTO metadata preserved");
                amount(contracts,1,"850"); amount(contracts,2,"475"); amount(contracts,3,"900");
                equal("APPROVE", invoices.selectById(10L).getProcessStatus(), "workflow persisted");
                List<CoreBpmnInstanceStatusDTO<Long>> rejected = invoices.rejectTask(body(10,11,12));
                equal(true, store.lastStatuses == rejected, "original rejection status list identity preserved");
                amount(contracts,1,"1000"); amount(contracts,2,"500"); amount(contracts,3,"900");
                equal("DRAFT_REJECTED", invoices.selectById(12L).getProcessStatus(), "rejection persisted");
                equal(6,store.db.queryForObject("SELECT COUNT(*) FROM WORKFLOW_EVENT",Integer.class),"workflow events");
            } else {
                boolean reject = scenario.startsWith("reject-");
                String before = reject ? "APPROVE" : "DRAFT";
                if (reject) store.db.update("UPDATE INVOICE SET STATUS='APPROVE'");
                if (scenario.endsWith("rollback")) store.db.update("UPDATE CONTRACT SET LOCKED=TRUE WHERE ID=2");
                else if (scenario.endsWith("invoice-null")) store.db.update("UPDATE INVOICE SET AMOUNT=NULL WHERE ID=10");
                else if (scenario.endsWith("contract-null")) store.db.update("UPDATE CONTRACT SET AMOUNT=NULL WHERE ID=1");
                else if (scenario.endsWith("overamount")) store.db.update("UPDATE INVOICE SET AMOUNT='1001' WHERE ID=10");
                else if (scenario.equals("workflow-failure")) store.workflowFailure = true;
                else throw new AssertionError("Unknown scenario " + scenario);
                try {
                    if (reject) invoices.rejectTask(body(10,11,12)); else invoices.startProcess(body(10,11,12));
                    throw new AssertionError("Expected public Service rejection");
                } catch (DataIntegrityViolationException expected) {
                    if (!scenario.endsWith("rollback")) throw expected;
                } catch (AuthenticationServiceException expected) {
                    equal(scenario.endsWith("overamount") ? "开票金额大于当前未开票金额！" : "有未开票金额或开票收入金额为空！",
                        expected.getMessage(), "controlled business error");
                } catch (IllegalStateException expected) {
                    if (!scenario.equals("workflow-failure")) throw expected;
                    equal("fixture engine failure", expected.getMessage(), "original workflow failure propagated");
                }
                if (scenario.endsWith("rollback")) equal(Collections.singletonList(1L), store.updatedContracts,
                    "first contract was written before second contract constraint failure");
                for (long id : new long[]{10,11,12}) equal(before, invoices.selectById(id).getProcessStatus(), "workflow rolled back " + id);
                equal(0,store.db.queryForObject("SELECT COUNT(*) FROM WORKFLOW_EVENT",Integer.class),"workflow events rolled back");
                if (!scenario.endsWith("contract-null")) amount(contracts,1,"1000");
                else equal(null, contracts.selectById(1L).getExt$Item("uninvoicedAmount"), "missing contract amount stays absent");
                amount(contracts,2,"500"); amount(contracts,3,"900");
            }
            equal(0,redis.locks.size(),"original locker released at transaction completion");
        }
        System.out.println("PASS invoice-contract-allocation " + scenario);
    }
    public static void main(String[] args) { run(args[0]); }
}
