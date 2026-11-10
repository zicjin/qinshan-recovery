import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.context.support.StaticMessageSource;

/** Public metadata-clearing contracts, independent of Spring/DB runtime state. */
public final class PersistableMetadataCheck {
    private static int assertions;
    private static final LocalDateTime CREATED = LocalDateTime.of(2026, 1, 2, 3, 4, 5);

    public static void main(String[] args) {
        StaticMessageSource messages = new StaticMessageSource();
        messages.setUseCodeAsDefaultMessage(true);
        new I18nHelper().setMessageSource(messages);
        if (!"remove-created".equals(args[0])) {
            resetRequest();
            resetInheritedAuditFields();
        }
        if (!"reset-request".equals(args[0])) {
            removeCreated();
        }
        System.out.println("PASS " + args[0] + ": " + assertions + " assertions");
    }

    private static void resetRequest() {
        CoreRequestUrlBean item = new CoreRequestUrlBean();
        item.setId(71L);
        item.setMenuId("test-menu");
        item.setUrl("api/test/items/{id}");
        item.setMethod("GET");
        item.setNote("business note");
        seedCreation(item);
        item.setExt$Item("custom", "business extension");

        // A one-argument setter must receive one null, including on repeated reset.
        PersistableHelper.resetBasicProperties(item);
        requestIdentityUnchanged(item);
        creationCleared(item);
        PersistableHelper.resetBasicProperties(item);
        requestIdentityUnchanged(item);
        creationCleared(item);
    }

    private static void requestIdentityUnchanged(CoreRequestUrlBean item) {
        equal(71L, item.getId(), "request id");
        equal("test-menu", item.getMenuId(), "request menu");
        equal("api/test/items/{id}", item.getUrl(), "request URL");
        equal("GET", item.getMethod(), "request method");
        equal("business note", item.getNote(), "request note");
        equal("business extension", item.getExt$Item("custom"), "request extension");
    }

    private static void creationCleared(Insertable<?> item) {
        equal(null, item.getCreatedById(), "request createdById");
        equal(null, item.getCreatedByName(), "request createdByName");
        equal(null, item.getCreatedByOrgId(), "request createdByOrgId");
        equal(null, item.getCreatedByOrgName(), "request createdByOrgName");
        equal(null, item.getCreatedTime(), "request createdTime");
    }

    private static void seedCreation(Insertable<?> item) {
        item.setCreatedById("test-author");
        item.setCreatedByName("Test Author");
        item.setCreatedByOrgId("test-org");
        item.setCreatedByOrgName("Test Organization");
        item.setCreatedTime(CREATED);
    }

    private static CoreCodeBean code() {
        CoreCodeBean item = new CoreCodeBean();
        item.setId(72L);
        item.setCode("test-code");
        item.setCodeName("Business name");
        item.setCodeCategoryId("test-category");
        item.setOrderNo(3L);
        item.setRemark("Business remark");
        seedCreation(item);
        item.setLastSuspendedFlag("1");
        item.setLastSuspendedById("test-reviewer");
        item.setLastSuspendedByName("Test Reviewer");
        item.setLastSuspendedTime(CREATED.plusDays(1));
        return item;
    }

    private static void codeIdentityUnchanged(CoreCodeBean item) {
        equal(72L, item.getId(), "code id");
        equal("test-code", item.getCode(), "code");
        equal("Business name", item.getCodeName(), "code name");
        equal("test-category", item.getCodeCategoryId(), "category");
        equal(3L, item.getOrderNo(), "business order");
        equal("Business remark", item.getRemark(), "remark");
    }

    private static void resetInheritedAuditFields() {
        CoreCodeBean item = code();
        PersistableHelper.resetBasicProperties(item);
        creationCleared(item);
        codeIdentityUnchanged(item);
        equal(null, item.getLastSuspendedFlag(), "suspended flag");
        equal(null, item.getLastSuspendedById(), "suspended by id");
        equal(null, item.getLastSuspendedByName(), "suspended by name");
        equal(null, item.getLastSuspendedTime(), "suspended time");
    }

    private static void removeCreated() {
        CoreRequestUrlBean request = new CoreRequestUrlBean();
        request.setId(71L);
        request.setMenuId("test-menu");
        request.setUrl("api/test/items/{id}");
        request.setMethod("GET");
        request.setNote("business note");
        request.setExt$Item("custom", "business extension");
        seedCreation(request);
        PersistableHelper.removeCreatedUserData(request);
        requestIdentityUnchanged(request);
        creationCleared(request);
        PersistableHelper.removeCreatedUserData(request);
        creationCleared(request);

        CoreCodeBean inherited = code();
        PersistableHelper.removeCreatedUserData(inherited);
        creationCleared(inherited);
        codeIdentityUnchanged(inherited);
        // This narrower operation removes creation metadata only.
        equal("1", inherited.getLastSuspendedFlag(), "preserve suspended flag");
        equal("test-reviewer", inherited.getLastSuspendedById(), "preserve suspended by id");
        equal("Test Reviewer", inherited.getLastSuspendedByName(), "preserve suspended by name");
        equal(CREATED.plusDays(1), inherited.getLastSuspendedTime(), "preserve suspended time");
    }

    private static void equal(Object expected, Object actual, String description) {
        assertions++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(description + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
