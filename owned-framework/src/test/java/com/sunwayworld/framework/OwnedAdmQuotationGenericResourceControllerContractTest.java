package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedAdmQuotationGenericResourceController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Contract guard for the shared administrative/quotation read controller. */
public final class OwnedAdmQuotationGenericResourceControllerContractTest {
    public static void main(String[] args) {
        Set<String> posts = new HashSet<String>(); Set<String> gets = new HashSet<String>();
        for (Method method : OwnedAdmQuotationGenericResourceController.class.getDeclaredMethods()) {
            PostMapping post = method.getAnnotation(PostMapping.class); if (post != null) posts.addAll(Arrays.asList(post.path()));
            GetMapping get = method.getAnnotation(GetMapping.class); if (get != null) gets.addAll(Arrays.asList(get.path()));
        }
        require(posts, "/secure/emc/module/item/adm-areas/queries");
        require(posts, "/secure/emc/module/item/quotation/addprices/queries/searchable");
        require(posts, "/secure/emc/module/item/quotation/detectionfactors/queries/raw");
        require(posts, "/secure/emc/module/item/quotation/discounts/queries");
        require(posts, "/secure/emc/module/item/quotation/quotationinformations/queries");
        require(gets, "/secure/emc/module/item/adm-areas/info/status");
        require(gets, "/secure/emc/module/item/quotation/quotationinformations/info/status");
        System.out.println("owned administrative/quotation generic resource contract checks passed");
    }
    private static void require(Set<String> paths, String path) { if (!paths.contains(path)) throw new AssertionError(path); }
}
