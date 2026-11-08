import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcInvoiceDao;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcReturnedDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcReturnedService;
import com.sunwayworld.emc.module.mdm.invoices.service.impl.EmcInvoiceServiceImpl;
import com.sunwayworld.emc.module.mdm.invoices.service.impl.EmcReturnedServiceImpl;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationServiceException;

/** Public original service seam; only persistence is replaced, without runtime/DB claims. */
public final class InvoiceReturnedAmountTest {
    private static final Long INVOICE_ID = 41L;

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

    /** Captures persistence effects; service code performs all amount and status calculations. */
    private static final class Store {
        final EmcInvoiceBean invoice = new EmcInvoiceBean();
        final Map<Long, EmcReturnedBean> returns = new LinkedHashMap<>();
        int invoiceWrites;
        int returnWrites;

        Store(String amount) {
            invoice.setId(INVOICE_ID);
            invoice.setInvoiceAmount(amount);
            invoice.setDoneFlag("0");
        }

        Object invoiceDao(Object proxy, Method method, Object[] args) {
            if (method.getName().equals("selectById")) {
                expect(INVOICE_ID, args[0], "invoice selection");
                EmcInvoiceBean selected = new EmcInvoiceBean();
                selected.setId(invoice.getId());
                selected.setInvoiceAmount(invoice.getInvoiceAmount());
                selected.setDoneFlag(invoice.getDoneFlag());
                return selected;
            }
            if (method.getName().equals("updateIfChanged")) {
                invoiceWrites++;
                EmcInvoiceBean updated = args[0] instanceof List ? (EmcInvoiceBean) ((List<?>) args[0]).get(0) : (EmcInvoiceBean) args[0];
                expect(INVOICE_ID, updated.getId(), "updated invoice");
                invoice.setDoneFlag(updated.getDoneFlag());
                return args[0] instanceof List ? args[0] : updated;
            }
            throw new AssertionError("Unexpected invoice DAO call: " + method);
        }

        Object returnedDao(Object proxy, Method method, Object[] args) {
            if (method.getName().equals("selectListByOneColumnValue")) {
                expect(INVOICE_ID, args[0], "return collection parent");
                expect("invoiceId", args[1], "return collection column");
                return new ArrayList<>(returns.values());
            }
            if (method.getName().equals("insert") && args.length == 1 && args[0] instanceof EmcReturnedBean) {
                returnWrites++;
                EmcReturnedBean row = (EmcReturnedBean) args[0];
                if (row.getId() == null || returns.containsKey(row.getId())) {
                    throw new AssertionError("Return requires a fresh, nonnull identity");
                }
                returns.put(row.getId(), row);
                return null;
            }
            throw new AssertionError("Unexpected returned DAO call: " + method);
        }
    }

    private static RestJsonWrapperBean body(String amount) {
        return new RestJsonWrapperBean(Collections.emptyMap(),
            Collections.singletonList(Collections.singletonList(amount == null ? "{}" : com.alibaba.fastjson.JSON.toJSONString(Collections.singletonMap("returnedAmount", amount)))),
            Collections.singletonList(null));
    }

    private static void rejected(EmcReturnedService service, Store store, String amount, String message) {
        int invoiceWrites = store.invoiceWrites;
        int returnWrites = store.returnWrites;
        int rowCount = store.returns.size();
        String doneFlag = store.invoice.getDoneFlag();
        try {
            service.insert(INVOICE_ID, body(amount));
            throw new AssertionError("Expected business validation rejection");
        } catch (AuthenticationServiceException error) {
            expect(message, error.getMessage(), "business validation message");
        }
        expect(invoiceWrites, store.invoiceWrites, "rejection must not update invoice");
        expect(returnWrites, store.returnWrites, "rejection must not insert return");
        expect(rowCount, store.returns.size(), "rejection preserves return collection");
        expect(doneFlag, store.invoice.getDoneFlag(), "rejection preserves invoice done flag");
    }

    private static Long returned(EmcReturnedService service, Store store, String amount,
                                 String remaining, String doneFlag) {
        Long id = service.insert(INVOICE_ID, body(amount));
        EmcReturnedBean row = store.returns.get(id);
        if (row == null) throw new AssertionError("Returned identity must identify the persisted return");
        expect(id, row.getId(), "returned row identity");
        expect(INVOICE_ID, row.getInvoiceId(), "return belongs to invoice");
        expect("100", row.getInvoiceAmount(), "invoice amount retained");
        expect(amount, row.getReturnedAmount(), "actual returned amount retained");
        expect(remaining, row.getUncollectedAmount(), "cumulative uncollected amount");
        expect(doneFlag, store.invoice.getDoneFlag(), "persisted invoice done flag");
        return id;
    }

    private static void useLocalIdentityWorker() throws Exception {
        // Isolate the real Snowflake generator to this disposable JVM, without Redis allocation.
        Class<?> type = Class.forName("com.sunwayworld.framework.id.SnowflakeIdWorker");
        Constructor<?> constructor = type.getDeclaredConstructor(long.class);
        constructor.setAccessible(true);
        Field worker = GikamIdHelper.class.getDeclaredField("worker");
        worker.setAccessible(true);
        worker.set(null, constructor.newInstance(1L));
    }

    public static void main(String[] args) throws Exception {
        String scenario = args[0];
        String amount = scenario.equals("null") ? null : scenario.equals("empty") ? ""
            : scenario.equals("blank") ? " \t\r\n " : "100";
        Store store = new Store(amount);
        useLocalIdentityWorker();
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getBeanFactory().registerSingleton("invoiceDao", adapter(EmcInvoiceDao.class, store::invoiceDao));
            context.getBeanFactory().registerSingleton("returnedDao", adapter(EmcReturnedDao.class, store::returnedDao));
            context.getBeanFactory().registerSingleton("contractService", adapter(EmcContractService.class,
                (proxy, method, params) -> { throw new AssertionError("Returns must not call contract service"); }));
            context.registerBean(EmcInvoiceServiceImpl.class);
            context.registerBean(EmcReturnedServiceImpl.class);
            context.refresh();
            ApplicationContextHelper.setApplicationContext(context);
            EmcInvoiceService invoices = context.getBean(EmcInvoiceService.class);
            EmcReturnedService service = context.getBean(EmcReturnedService.class);
            if (scenario.equals("invalid-input")) {
                for (String value : java.util.Arrays.asList(null, "", " \t ", "not-a-number", "1,000", "NaN", "Infinity")) {
                    boolean rejected = false;
                    try { service.insert(INVOICE_ID, body(value)); }
                    catch (com.sunwayworld.framework.exception.checked.CheckedException error) {
                        rejected = true;
                        expect("GIKAM.EXCEPTION.PARAM_ERROR", error.getMessage(), "controlled amount input error");
                    }
                    expect(true, rejected, "invalid monetary input rejected");
                    expect(0, store.invoiceWrites, "invalid input does not update parent");
                    expect(0, store.returnWrites, "invalid input does not insert return");
                }
            } else if (scenario.equals("null") || scenario.equals("empty") || scenario.equals("blank")) {
                rejected(service, store, "1", "请输入开票金额！");
                expect(amount, invoices.selectById(INVOICE_ID).getInvoiceAmount(), "rejected invoice amount retained");
            } else {
                Long partial = returned(service, store, "30", "70", "0");
                if (scenario.equals("overpay")) {
                    // With 70 still outstanding, a return of 71 must reject without any writes.
                    rejected(service, store, "71", "输入的回款金额大于未回款金额！");
                } else {
                    Long full = returned(service, store, "70", "0", "1");
                    if (partial.equals(full)) throw new AssertionError("Each return needs its own identity");
                    expect(2, store.invoiceWrites, "two accepted invoice updates");
                    expect(2, store.returnWrites, "two accepted returns");
                    rejected(service, store, "1", "输入的回款金额大于未回款金额！");
                    expect("1", invoices.selectById(INVOICE_ID).getDoneFlag(), "fully paid invoice stays done");
                }
            }
        }
        System.out.println(scenario + ": PASS (actual Spring-wired services, JSON wrapper and DAO boundary)");
    }
}
