package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReagentWriteController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/** Contract guard for the source-owned reagent write route family. */
public final class OwnedReagentWriteControllerContractTest {
    public static void main(String[] args) {
        int post = 0, put = 0, delete = 0;
        for (Method method : OwnedReagentWriteController.class.getDeclaredMethods()) {
            if (method.getAnnotation(PostMapping.class) != null) post++;
            if (method.getAnnotation(PutMapping.class) != null) put++;
            if (method.getAnnotation(DeleteMapping.class) != null) delete++;
        }
        if (post != 3 || put != 1 || delete != 2)
            throw new AssertionError("reagent write route counts=" + post + "/" + put + "/" + delete);
        System.out.println("owned reagent write controller contract checks passed (" + post + "/" + put + "/" + delete + " routes)");
    }
}
