package com.sunwayworld.framework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.owned.internal.web.OwnedFolderGenericResourceController;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the owned folder GenericResource read seam. */
public final class OwnedFolderGenericResourceControllerContractTest {
    private static final String PREFIX = "/secure/emc/module/bp/bp/folders";

    public static void main(String[] args) {
        Set<String> paths = mappedPaths();
        require(paths, PREFIX + "/queries");
        require(paths, PREFIX + "/queries/raw");
        require(paths, PREFIX + "/queries/searchable");
        require(paths, PREFIX + "/queries/tasks");
        require(paths, PREFIX + "/info/status");
        require(paths, PREFIX + "/{id}/info/status");
        for (String path : paths) {
            if (path.contains("{resource}")) throw new AssertionError("unbounded folder resource path: " + path);
        }
        boolean registered = false;
        for (Method method : OwnedIdentityConfiguration.class.getDeclaredMethods()) {
            if (method.getReturnType() == OwnedFolderGenericResourceController.class) registered = true;
        }
        if (!registered) throw new AssertionError("folder GenericResource controller is not registered");
        JSONObject status = JSON.parseObject(OwnedFolderGenericResourceController.statusBody());
        JSONObject audit = status.getJSONObject("auditStatus");
        if (audit == null || !"draft".equals(audit.getString("processStatus"))
                || !"draft".equals(audit.getString("statusCode"))) {
            throw new AssertionError("folder status must expose the draft auditStatus envelope");
        }
        if (!status.containsKey("ext$")) throw new AssertionError("folder status must retain ext$ envelope");
        try {
            Field profile = OwnedFolderGenericResourceController.class.getDeclaredField("FOLDER_TYPE_PROFILE");
            profile.setAccessible(true);
            if (((Map<?, ?>) profile.get(null)).size() != 78) throw new AssertionError("folder supplier type profile must contain 78 fields");
        } catch (ReflectiveOperationException error) { throw new AssertionError("cannot inspect folder type profile", error); }
        System.out.println("owned folder GenericResource mapping/status checks passed");
    }

    private static Set<String> mappedPaths() {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedFolderGenericResourceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class);
            if (post != null) paths.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class);
            if (get != null) paths.addAll(Arrays.asList(get.path()));
        }
        return paths;
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing folder GenericResource path: " + path);
    }
}
