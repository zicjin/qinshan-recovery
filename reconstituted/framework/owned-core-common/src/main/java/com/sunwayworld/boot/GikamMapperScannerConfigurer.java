package com.sunwayworld.boot;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.regex.Pattern;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

/**
 * Scans the supplier's two mapper conventions without treating every interface
 * as a MyBatis mapper: {@code @GikamBean} interfaces and interfaces under a
 * {@code mapper} package whose simple name ends in {@code Mapper}.
 */
public class GikamMapperScannerConfigurer extends MapperScannerConfigurer {
    private static final Pattern MAPPER_PACKAGE =
            Pattern.compile("^com\\.sunwayworld\\..+\\.mapper\\..*Mapper$");
    private String gikamBasePackage;

    @Override
    public void setBasePackage(String basePackage) {
        super.setBasePackage(basePackage);
        this.gikamBasePackage = basePackage;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        scanner.setResourceLoader(new PathMatchingResourcePatternResolver());
        scanner.setAddToConfig(true);
        scanner.setSqlSessionTemplateBeanName("sqlSessionTemplate");
        scanner.addIncludeFilter(new AnnotationTypeFilter(GikamBean.class));
        scanner.addIncludeFilter(new RegexPatternTypeFilter(MAPPER_PACKAGE));
        scanner.scan(gikamBasePackage);
    }
}
