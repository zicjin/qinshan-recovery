package com.sunwayworld.framework;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Compares the principal value contract with the recorded supplier class. */
public final class LoginUserDifferentialTest {
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        urls.add(framework.toURI().toURL());
        File[] jars = deps.listFiles(); Arrays.sort(jars);
        for (File jar : jars) urls.add(jar.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }
    @SuppressWarnings("unchecked")
    private static List<String> observe(ClassLoader loader) throws Exception {
        Class<?> type = loader.loadClass("com.sunwayworld.framework.security.bean.LoginUser");
        Object user = type.getConstructor(String.class, String.class).newInstance("alice", null);
        List<String> result = new ArrayList<String>();
        result.add("username=" + type.getMethod("getUsername").invoke(user));
        result.add("password=" + type.getMethod("getPassword").invoke(user));
        result.add("authorities=" + type.getMethod("getAuthorities").invoke(user));
        for (String getter : new String[]{"getOrgId", "getOrgName", "getRoleId", "getRoleName", "getTenantId", "getTenantName", "getCasToken", "getTrustee", "getAdditionalCheck", "isBadPassword", "getLoginType", "getLoginToken", "isEnabled", "isAccountNonExpired", "isAccountNonLocked", "isCredentialsNonExpired"}) {
            result.add(getter + "-default=" + type.getMethod(getter).invoke(user));
        }
        for (String property : new String[]{"OrgId", "OrgName", "RoleName", "CasToken", "LoginType", "TenantName", "LoginToken"}) {
            type.getMethod("set" + property, String.class).invoke(user, "value-" + property);
            result.add(property + "=" + type.getMethod("get" + property).invoke(user));
        }
        type.getMethod("setRoleId", Long.class).invoke(user, 7L);
        type.getMethod("setTenantId", Long.class).invoke(user, 9L);
        type.getMethod("setAdditionalCheck", boolean.class).invoke(user, false);
        type.getMethod("setBadPassword", boolean.class).invoke(user, true);
        result.add("ids=" + type.getMethod("getRoleId").invoke(user) + ":" + type.getMethod("getTenantId").invoke(user));
        result.add("flags=" + type.getMethod("getAdditionalCheck").invoke(user) + ":" + type.getMethod("isBadPassword").invoke(user));
        Map<String,Object> ext = (Map<String,Object>) type.getMethod("getExt").invoke(user);
        type.getMethod("addExtValue", String.class, Object.class).invoke(user, "x", "y");
        result.add("ext=" + ext + ":" + type.getMethod("getExtValue", String.class).invoke(user, "x"));
        Object trustee = type.getConstructor(String.class, String.class).newInstance("bob", "secret");
        type.getMethod("setTrustee", type).invoke(user, trustee);
        result.add("trustee=" + type.getMethod("getTrustee").invoke(user).getClass().getName());
        result.add("trustee-identity=" + (type.getMethod("getTrustee").invoke(user) == trustee));
        Map<String, Object> replacement = new java.util.HashMap<String, Object>();
        type.getMethod("setExt", Map.class).invoke(user, replacement);
        replacement.put("MixedCase", "value");
        result.add("ext-identity=" + (replacement == type.getMethod("getExt").invoke(user)));
        result.add("case-sensitive=" + type.getMethod("getExtValue", String.class).invoke(user, "mixedcase"));
        type.getMethod("setExt", Map.class).invoke(user, new Object[]{null});
        result.add("null-ext=" + type.getMethod("getExt").invoke(user));
        for (String operation : new String[]{"addExtValue", "getExtValue"}) {
            try {
                if (operation.equals("addExtValue")) type.getMethod(operation, String.class, Object.class).invoke(user, "x", "y");
                else type.getMethod(operation, String.class).invoke(user, "x");
                result.add(operation + "-null=accepted");
            } catch (java.lang.reflect.InvocationTargetException failure) {
                result.add(operation + "-null=" + failure.getCause().getClass().getName());
            }
        }
        for (String username : new String[]{null, "", " ", "bob"}) {
            try {
                Object candidate = type.getConstructor(String.class, String.class).newInstance(username, "test-only");
                result.add("username-" + username + "=" + type.getMethod("getUsername").invoke(candidate));
                Object sameName = type.getConstructor(String.class, String.class).newInstance(username, "other-test-only");
                result.add("equals-" + username + "=" + candidate.equals(sameName) + ":" + (candidate.hashCode() == sameName.hashCode()));
                type.getMethod("eraseCredentials").invoke(candidate);
                result.add("erased-" + username + "=" + type.getMethod("getPassword").invoke(candidate));
            } catch (java.lang.reflect.InvocationTargetException failure) {
                result.add("username-" + username + "=" + failure.getCause().getClass().getName());
            }
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader reference = loader(new File(args[0]), new File(args[1]));
             URLClassLoader owned = loader(new File(args[0]), new File(args[2]))) {
            List<String> expected = observe(reference), actual = observe(owned);
            if (!expected.equals(actual)) throw new AssertionError("login user observations differ: " + expected + " / " + actual);
            System.out.println("login user differential: PASS (" + actual.size() + " observations)");
        }
    }
}
