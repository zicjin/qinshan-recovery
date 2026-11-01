package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedMobileProjectController;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.PostMapping;

/** Static guard for the first mobile project read-family slice. */
public final class OwnedMobileProjectControllerContractTest {
    public static void main(String[] args) throws Exception {
        Set<String> paths = new HashSet<String>();
        for (Method method : OwnedMobileProjectController.class.getDeclaredMethods()) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            if (mapping != null) paths.addAll(Arrays.asList(mapping.path()));
        }
        require(paths, "/secure/emc/module/bp/mobile/projects/queries");
        require(paths, "/secure/emc/module/bp/mobile/projects/operation-types/queries");
        require(paths, "/secure/emc/module/bp/mobile/projects/static-data/queries");
        require(paths, "/secure/emc/module/bp/mobile/projects/static-data/pagination/queries");
        require(paths, "/secure/emc/module/bp/mobile/projects/detail-label/queries");
        require(paths, "/secure/emc/module/bp/mobile/projects/label-detail/queries");
        require(paths, "/secure/emc/module/bp/mobile/projects/todo-accept/queries");
        Field field = OwnedMobileProjectController.class.getDeclaredField("OPERATION_TYPES");
        field.setAccessible(true);
        List<?> types = (List<?>) field.get(null);
        if (types.size() != 34) throw new AssertionError("unexpected mobile operation type count: " + types.size());
        System.out.println("owned mobile project read contract checks passed");
    }

    private static void require(Set<String> paths, String path) {
        if (!paths.contains(path)) throw new AssertionError("missing mobile project path: " + path);
    }
}
