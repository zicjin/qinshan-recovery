package com.sunwayworld.owned.internal.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Leaves routing to the owned identity boundary instead of Spring Security's
 * generated login page. The owned boundary performs RSA credential decoding,
 * database authentication, session creation, and protected-route checks.
 */
@Configuration
@EnableWebSecurity
@Order(0)
@ConditionalOnProperty(prefix = "owned.web.security", name = "mode", havingValue = "owned")
public class OwnedWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                // The imported supplier workspace embeds its panel documents
                // as same-origin iframes. Keep cross-origin framing blocked,
                // while matching the original SAMEORIGIN response contract.
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests().anyRequest().permitAll();
    }
}
