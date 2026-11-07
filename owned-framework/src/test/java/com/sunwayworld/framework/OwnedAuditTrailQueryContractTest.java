package com.sunwayworld.framework;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.web.OwnedAuditTrailController;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;

/** Public MVC contract: original wrapper resolution must run before the guarded service seam. */
public final class OwnedAuditTrailQueryContractTest {
    public static void main(String[] args) throws Exception {
        for (String name : new String[]{"records", "recordLines"}) {
            Method method = OwnedAuditTrailController.class.getMethod(name,
                    HttpServletRequest.class, RestJsonWrapperBean.class);
            if (method.getParameters()[1].isAnnotationPresent(RequestBody.class)) {
                throw new AssertionError("@RequestBody bypasses the original RestJsonWrapper resolver");
            }
        }
        System.out.println("owned audit query public MVC wrapper contract passed");
    }
}
