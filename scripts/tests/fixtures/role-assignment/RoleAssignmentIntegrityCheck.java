import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.dao.CoreRoleDao;
import com.sunwayworld.module.sys.role.dao.CoreRoleUserDao;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import com.sunwayworld.module.sys.role.service.impl.CoreRoleServiceImpl;
import com.sunwayworld.module.sys.role.service.impl.CoreRoleUserServiceImpl;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Real public service seam; DAO adapters model the external storage boundary. */
public class RoleAssignmentIntegrityCheck {
    private static final String USER = "role-assignment-fixture";
    private static final String MESSAGE = "record does not exist";
    private static int checks;

    private static class Store {
        final List<CoreRoleUserBean> assignments = new ArrayList<>();
        final CoreRoleBean role = new CoreRoleBean();
        Store() { role.setId(100L); }
        CoreRoleDao roles() {
            return (CoreRoleDao) Proxy.newProxyInstance(CoreRoleDao.class.getClassLoader(),
                new Class<?>[] {CoreRoleDao.class}, (proxy, method, args) -> {
                    if (method.getName().equals("selectByIdIfPresent")) {
                        return role.getId().equals(args[0]) ? role : null;
                    }
                    throw new AssertionError("unexpected role storage operation: " + method.getName());
                });
        }
        CoreRoleUserDao roleUsers() {
            return (CoreRoleUserDao) Proxy.newProxyInstance(CoreRoleUserDao.class.getClassLoader(),
                new Class<?>[] {CoreRoleUserDao.class}, (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.equals("insert")) {
                        @SuppressWarnings("unchecked")
                        List<CoreRoleUserBean> rows = (List<CoreRoleUserBean>) args[0];
                        assignments.addAll(rows);
                        return null;
                    }
                    if (name.equals("selectAll")) return new ArrayList<>(assignments);
                    if (name.equals("selectListByOneColumnValue")) {
                        List<CoreRoleUserBean> rows = new ArrayList<>();
                        for (CoreRoleUserBean row : assignments) {
                            if (row.getUserId().equals(args[0])) rows.add(row);
                        }
                        return rows;
                    }
                    throw new AssertionError("unexpected assignment storage operation: " + name);
                });
        }
    }

    private static void set(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static CoreRoleUserService service(Store store, ConcurrentMapCacheManager caches) throws Exception {
        CoreRoleServiceImpl roles = new CoreRoleServiceImpl();
        set(roles, "roleDao", store.roles());
        CoreRoleUserServiceImpl assignments = new CoreRoleUserServiceImpl();
        set(assignments, "roleUserDao", store.roleUsers());
        set(assignments, "coreRoleService", roles);
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheManager(caches);
        interceptor.setCacheOperationSources(new AnnotationCacheOperationSource());
        interceptor.afterPropertiesSet();
        interceptor.afterSingletonsInstantiated();
        ProxyFactory factory = new ProxyFactory(assignments);
        factory.setInterfaces(CoreRoleUserService.class);
        factory.addAdvice(interceptor);
        return (CoreRoleUserService) factory.getProxy();
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void rejectedWithoutChanges(List<Long> roleIds) throws Exception {
        Store store = new Store();
        ConcurrentMapCacheManager caches = new ConcurrentMapCacheManager("T_CORE_ROLE_USER", "T_CORE_ROLE_USER.BY_USERID");
        CoreRoleUserService service = service(store, caches);
        List<CoreRoleUserBean> beforeUser = service.selectByUserId(USER);
        List<CoreRoleUserBean> beforeAll = service.selectAll();
        boolean rejected = false;
        try {
            service.insertByUserId(USER, roleIds);
        } catch (ApplicationWarningException expected) {
            rejected = true;
            check(MESSAGE.equals(expected.getMessage()), "missing role uses the existing fixed safe message");
            check(expected.getClass().getAnnotation(ResponseStatus.class).value() == HttpStatus.BAD_REQUEST,
                "missing role is a controlled HTTP 400 warning");
        }
        check(rejected, "nonexistent/null role must be rejected before assigning a user: " + roleIds);
        check(store.assignments.isEmpty(), "a rejected role list must not persist any associations");
        check(caches.getCache("T_CORE_ROLE_USER.BY_USERID").get(USER).get() == beforeUser,
            "rejected assignment preserves the warmed user cache");
        check(caches.getCache("T_CORE_ROLE_USER").get("ALL").get() == beforeAll,
            "rejected assignment preserves the warmed all-assignment cache");
        check(service.selectByUserId(USER).isEmpty() && service.selectAll().isEmpty(),
            "rejected assignment remains absent through both public reads");
    }

    private static void validAndEmptyContracts() throws Exception {
        Store store = new Store();
        ConcurrentMapCacheManager caches = new ConcurrentMapCacheManager("T_CORE_ROLE_USER", "T_CORE_ROLE_USER.BY_USERID");
        CoreRoleUserService service = service(store, caches);
        service.selectByUserId(USER);
        service.selectAll();
        Long id = service.insertByUserId(USER, Arrays.asList(100L));
        check(id != null, "valid assignment returns its generated association ID");
        check(caches.getCache("T_CORE_ROLE_USER.BY_USERID").get(USER) == null
            && caches.getCache("T_CORE_ROLE_USER").get("ALL") == null,
            "valid assignment invalidates both warmed caches");
        List<CoreRoleUserBean> rows = service.selectByUserId(USER);
        check(rows.size() == 1 && rows.get(0).getId().equals(id)
            && rows.get(0).getRoleId().equals(100L) && USER.equals(rows.get(0).getUserId()),
            "valid assignment is readable with the existing role and user");
        check(service.selectAll().size() == 1, "valid assignment is visible in the shared public list");
        check(service.insertByUserId(USER, Collections.<Long>emptyList()) == null,
            "an empty role list retains the null no-op result");
        check(service.selectByUserId(USER).size() == 1, "empty assignment retains existing associations");
    }

    private static void roleManagementEntryRejectsMissingRole() throws Exception {
        Store store = new Store();
        ConcurrentMapCacheManager caches = new ConcurrentMapCacheManager("T_CORE_ROLE_USER", "T_CORE_ROLE_USER.BY_USERID");
        CoreRoleUserService service = service(store, caches);
        CoreRoleServiceImpl roleManagement = new CoreRoleServiceImpl();
        set(roleManagement, "roleUserService", service);
        RestJsonWrapperBean wrapper = new RestJsonWrapperBean(Collections.<String, String>emptyMap(),
            Arrays.asList(Arrays.asList("{\"userId\":\"" + USER + "\"}")),
            Arrays.<GenericService<?, ?>>asList((GenericService<?, ?>) null));
        boolean rejected = false;
        try {
            roleManagement.insertRoleUser(999L, wrapper);
        } catch (ApplicationWarningException expected) {
            rejected = true;
        }
        check(rejected, "role management entry delegates to the shared missing-role guard");
        check(service.selectByUserId(USER).isEmpty() && service.selectAll().isEmpty(),
            "role management rejection leaves no association through public reads");
    }

    public static void main(String[] args) throws Exception {
        // Seed the ID generator only in this disposable JVM, avoiding real Redis.
        Field worker = GikamIdHelper.class.getDeclaredField("worker");
        worker.setAccessible(true);
        java.lang.reflect.Constructor<?> constructor = worker.getType().getDeclaredConstructor(long.class);
        constructor.setAccessible(true);
        worker.set(null, constructor.newInstance(0L));
        StaticApplicationContext context = new StaticApplicationContext();
        context.refresh();
        ApplicationContextHelper.setApplicationContext(context);
        StaticMessageSource messages = new StaticMessageSource();
        messages.addMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", Locale.getDefault(), MESSAGE);
        messages.addMessage("GIKAM.EXCEPTION.NO_VALUE_EXISTS", Locale.CHINA, MESSAGE);
        new I18nHelper().setMessageSource(messages);
        try {
            rejectedWithoutChanges(Arrays.asList(999L));
            rejectedWithoutChanges(Arrays.asList(100L, 999L));
            rejectedWithoutChanges(Arrays.asList(100L, null));
            validAndEmptyContracts();
            roleManagementEntryRejectsMissingRole();
            System.out.println("role assignment public service regression: PASS (" + checks + " checks)");
        } finally {
            ApplicationContextHelper.setApplicationContext(null);
            context.close();
        }
    }
}
