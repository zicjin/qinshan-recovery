package com.sunwayworld.framework;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import java.util.List;

/** Contract test for owned context, configuration, and identity behavior. */
public final class ApplicationContextHelperContractTest {
    private ApplicationContextHelperContractTest() {}

    public static void main(String[] args) {
        if (!"lims-owned".equals(ApplicationContextHelper.getAppId())) {
            throw new AssertionError("owned app id fallback mismatch");
        }
        long first = ApplicationContextHelper.getNextIdentity();
        long second = ApplicationContextHelper.getNextIdentity();
        if (second != first + 1L) throw new AssertionError("identity allocation is not monotonic");
        List<Long> batch = ApplicationContextHelper.getNextIdentityList(3);
        if (batch.size() != 3 || batch.get(1) != batch.get(0) + 1L) {
            throw new AssertionError("identity batch contract failed");
        }
        if (!"127.0.0.1".equals(ApplicationContextHelper.getServerAddress())) {
            throw new AssertionError("fallback server address mismatch");
        }
        if (ApplicationContextHelper.getConstantValue("owned.missing") != null) {
            throw new AssertionError("missing constant must remain absent");
        }
        System.out.println("application context helper contract checks passed");
    }
}
