import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcInvoiceDao;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcReturnedDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.impl.EmcInvoiceServiceImpl;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Runs the original service through its public interface; only collaborators are fixtures. */
public final class InvoiceInsertIdentityTest {
    private static final String TABLE = "T_EMC_INVOICE";
    private static final String SEQUENCE = "INVOICE-TEST-0001";

    private static void expect(Object expected, Object actual, String reason) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(reason + ": expected " + expected + ", got " + actual);
        }
    }

    private static <T> T adapter(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
            (proxy, method, args) -> {
                if (method.getDeclaringClass() == Object.class) {
                    if (method.getName().equals("toString")) return "fixture:" + type.getSimpleName();
                    if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                    if (method.getName().equals("equals")) return proxy == args[0];
                }
                return handler.invoke(proxy, method, args);
            }));
    }

    /** Captures the DAO write boundary; it does not generate IDs or implement service logic. */
    private static final class InvoiceStore implements InvocationHandler {
        final Map<Long, EmcInvoiceBean> rows = new LinkedHashMap<>();
        final RuntimeException rejection = new IllegalStateException("fixture DAO rejected insert");
        boolean reject;
        int attempts;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if (method.getName().equals("getTable")) return TABLE;
            if (method.getName().equals("getType")) return EmcInvoiceBean.class;
            if (method.getName().equals("insert") && args.length == 1 && args[0] instanceof EmcInvoiceBean) {
                attempts++;
                if (reject) throw rejection;
                EmcInvoiceBean row = (EmcInvoiceBean) args[0];
                if (row.getId() == null || rows.containsKey(row.getId())) {
                    throw new AssertionError("DAO requires a fresh, nonnull primary key");
                }
                rows.put(row.getId(), row);
                return null;
            }
            throw new AssertionError("Unexpected DAO call: " + method);
        }
    }

    private static final class SequenceFixture implements InvocationHandler {
        final List<String> keys = new ArrayList<>();
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if (!method.getName().equals("getNextSequenceList") || args.length != 3) {
                throw new AssertionError("Unexpected sequence call: " + method);
            }
            expect(TABLE, args[0], "sequence table");
            expect(1, args[2], "sequence quantity");
            keys.add((String) args[1]);
            return Collections.singletonList(SEQUENCE);
        }
    }

    private static RestJsonWrapperBean body(String json) {
        return new RestJsonWrapperBean(Collections.emptyMap(),
            Collections.singletonList(Collections.singletonList(json)),
            Collections.singletonList(null));
    }

    private static void useLocalIdentityWorker() throws Exception {
        // Use the actual reconstructed Snowflake generator without Redis worker allocation.
        // This seed changes only this disposable test JVM, never application source/runtime.
        Class<?> type = Class.forName("com.sunwayworld.framework.id.SnowflakeIdWorker");
        Constructor<?> constructor = type.getDeclaredConstructor(long.class);
        constructor.setAccessible(true);
        Field worker = GikamIdHelper.class.getDeclaredField("worker");
        worker.setAccessible(true);
        worker.set(null, constructor.newInstance(1L));
    }

    public static void main(String[] args) throws Exception {
        String scenario = args[0];
        InvoiceStore store = new InvoiceStore();
        SequenceFixture sequence = new SequenceFixture();
        useLocalIdentityWorker();
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getBeanFactory().registerSingleton("invoiceDao", adapter(EmcInvoiceDao.class, store));
            context.getBeanFactory().registerSingleton("sequenceService", adapter(CoreSequenceService.class, sequence));
            context.getBeanFactory().registerSingleton("contractService", adapter(EmcContractService.class,
                (proxy, method, params) -> { throw new AssertionError("Insert must not call contract service"); }));
            context.getBeanFactory().registerSingleton("returnedDao", adapter(EmcReturnedDao.class,
                (proxy, method, params) -> { throw new AssertionError("Insert must not query return records"); }));
            context.registerBean(EmcInvoiceServiceImpl.class);
            context.refresh();
            ApplicationContextHelper.setApplicationContext(context);
            EmcInvoiceService service = context.getBean(EmcInvoiceService.class);
            expect(context.getBean(EmcInvoiceDao.class), service.getDao(), "Spring DAO injection");
            String monthBefore = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            if (scenario.equals("failure")) {
                store.reject = true;
                try {
                    service.insert(body("{\"invoiceName\":\"rejected\",\"invoiceAmount\":\"37.50\"}"));
                    throw new AssertionError("DAO rejection must escape the service, not return a success ID");
                } catch (RuntimeException error) {
                    if (error != store.rejection) throw new AssertionError("DAO error must propagate unchanged", error);
                }
                expect(0, store.rows.size(), "failed insert has no captured row");
            } else {
                boolean copy = scenario.equals("copy");
                String json = "{\"invoiceName\":\"identity-fixture\",\"invoiceAmount\":\"37.50\","
                    + "\"contractId\":\"contract-1\",\"contractName\":\"contract\","
                    + "\"projectId\":\"project-1\",\"projectName\":\"project\",\"doneFlag\":\"1\""
                    + (copy ? ",\"id\":41" : "") + "}";
                Long returned = service.insert(scenario.equals("empty") ? new RestJsonWrapperBean() : body(json));
                expect(1, store.rows.size(), "one DAO write");
                EmcInvoiceBean stored = store.rows.values().iterator().next();
                expect(stored.getId(), returned, "returned identity must match the inserted row");
                if (returned == null || returned.equals(41L)) throw new AssertionError("Must return a fresh identity");
                expect(SEQUENCE, stored.getSysNo(), "generated sequence is retained");
                expect("1", stored.getExt$Item("COPYFLAG"), "copy flag is retained");
                expect(scenario.equals("empty") ? null : "identity-fixture", stored.getInvoiceName(), "name copied");
                boolean clear = copy || scenario.equals("empty");
                expect(clear ? null : "37.50", stored.getInvoiceAmount(), "invoice amount copy semantics");
                expect(clear ? null : "1", stored.getDoneFlag(), "copy resets completion to fresh invoice default; normal insert preserves supplied value");
                expect(clear ? null : "contract-1", stored.getContractId(), "contract ID copy semantics");
                expect(clear ? null : "contract", stored.getContractName(), "contract name copy semantics");
                expect(clear ? null : "project-1", stored.getProjectId(), "project ID copy semantics");
                expect(clear ? null : "project", stored.getProjectName(), "project name copy semantics");
            }
            expect(1, store.attempts, "exactly one DAO insert attempt");
            expect(1, sequence.keys.size(), "exactly one sequence request");
            String monthAfter = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            if (!sequence.keys.get(0).equals(monthBefore) && !sequence.keys.get(0).equals(monthAfter)) {
                throw new AssertionError("Sequence key must be the current year/month");
            }
        }
        System.out.println(scenario + ": PASS (actual Spring-wired service, JSON wrapper and DAO boundary)");
    }
}
