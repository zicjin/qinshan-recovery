package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedTemplateViewConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;

/** Ensures project templates win over the default Thymeleaf resolver. */
public final class OwnedTemplateViewResolverContractTest {
    public static void main(String[] args) throws Exception {
        OwnedTemplateViewConfiguration configuration = new OwnedTemplateViewConfiguration();
        ViewResolver resolver = configuration.ownedTemplateViewResolver();
        if (!(resolver instanceof Ordered)) throw new AssertionError("owned resolver is not ordered");
        int value = ((Ordered) resolver).getOrder();
        if (value != Ordered.HIGHEST_PRECEDENCE) throw new AssertionError("owned resolver order=" + value);
        Bean bean = OwnedTemplateViewConfiguration.class.getMethod("ownedTemplateViewResolver").getAnnotation(Bean.class);
        if (bean == null || bean.name().length != 2 || !"thymeleafViewResolver".equals(bean.name()[1])) {
            throw new AssertionError("owned resolver does not claim thymeleaf resolver name");
        }
        System.out.println("owned template view resolver contract checks passed");
    }
}
