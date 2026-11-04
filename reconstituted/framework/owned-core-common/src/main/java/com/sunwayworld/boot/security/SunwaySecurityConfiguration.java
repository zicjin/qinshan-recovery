package com.sunwayworld.boot.security;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.security.filter.RefreshFilterSecurityInterceptor;
import com.sunwayworld.framework.spring.filter.GikamFilterRegistry;
import com.sunwayworld.framework.spring.filter.GikamFilterConfig.FilterPos;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.support.AjaxAuthenticationFailureHandler;
import com.sunwayworld.framework.security.support.AjaxAuthenticationSuccessHandler;
import com.sunwayworld.framework.security.support.DefaultDaoAuthenticationProvider;
import com.sunwayworld.framework.security.support.Http401UnauthorizedEntryPoint;
import com.sunwayworld.framework.security.service.DefaultTokenBasedRememberMeServices;
import com.sunwayworld.framework.security.service.DefaultUserDetailsService;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class SunwaySecurityConfiguration extends WebSecurityConfigurerAdapter {
   public static final String REMEMBER_ME_COOKIE_NAME = "sunway-remember-me-cookie";
   public static final String REMEMBER_ME_KEY = "sunway-remember-me";
   public static final String CUSTOM_SECURITY_CONFIGURE_FUNCTION_NAME = "customSecurityConfigure";
   public static final String LOGIN_ORG_ID_KEY = "orgId";
   public static final String LOGIN_ROLE_ID_KEY = "roleId";
   public static final String LOGIN_TENANT_ID_KEY = "tenantId";
   public static final String PASSWORD_ENCODER_TYPE_MD5 = "MD5";
   public static final String PASSWORD_ENCODER_TYPE_SM3 = "SM3";
   @Value("${sunway.security.ant-pattern.deny-all:}")
   private String denyAll;
   @Value("${sunway.security.ant-pattern.permit-all:}")
   private String permitAll;
   @Value("${sunway.security.ant-pattern.authenticated:}")
   private String authenticated;
   @Autowired(required = false)
   private Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint;

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder amb) throws Exception { amb.authenticationProvider(this.authenticationProvider()); }

   @Bean
   @Override
   public UserDetailsService userDetailsService() { return new DefaultUserDetailsService(); }

   @Bean
   public AuthenticationProvider authenticationProvider() throws Exception {
        DefaultDaoAuthenticationProvider provider = new DefaultDaoAuthenticationProvider();
        provider.setUserDetailsService(this.userDetailsService());
        provider.setPasswordEncoder(this.passwordEncoder());
        return provider;
    }

   @Bean
   public RememberMeServices rememberMeServices() {
        DefaultTokenBasedRememberMeServices services = new DefaultTokenBasedRememberMeServices();
        services.setTokenValiditySeconds(this.getApplicationContext().getEnvironment().getProperty("sunway.security.token-validity-seconds", Integer.class, -1));
        return services;
    }

   private void init(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(this.http401UnauthorizedEntryPoint != null ? this.http401UnauthorizedEntryPoint : new Http401UnauthorizedEntryPoint())
                .and()
                .formLogin()
                    .loginProcessingUrl("/core/module/sys/login")
                    .usernameParameter("id")
                    .passwordParameter("password")
                    .successHandler(new AjaxAuthenticationSuccessHandler())
                    .failureHandler(new AjaxAuthenticationFailureHandler())
                .and()
                .logout()
                    .logoutUrl("/core/module/sys/logout")
                .and()
                .rememberMe()
                    .rememberMeServices(this.rememberMeServices())
                    .key(REMEMBER_ME_KEY);
    }

   @Bean
   @Order(Integer.MAX_VALUE)
   @ConditionalOnMissingBean({PasswordEncoder.class})
   public PasswordEncoder passwordEncoder() {
      final String passwordEncoderType = this.getApplicationContext().getEnvironment().getProperty("sunway.user.password.encoder.type", String.class, "SM3");
      return new PasswordEncoder() {
         @Override
         public String encode(CharSequence rawPassword) {
            MemoryCacheManager.put("" + Thread.currentThread().getId(), rawPassword);
            return "MD5".equals(passwordEncoderType) ? EncryptUtils.MD5Encrypt((String)rawPassword) : EncryptUtils.SM3Encrypt((String)rawPassword);
         }

         @Override
         public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword != null && this.encode(rawPassword).equals(encodedPassword);
         }
      };
   }

   @Bean
   public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
      StrictHttpFirewall firewall = new StrictHttpFirewall();
      firewall.setAllowUrlEncodedPercent(true);
      firewall.setAllowUrlEncodedSlash(true);
      firewall.setAllowSemicolon(true);
      firewall.setAllowUrlEncodedDoubleSlash(true);
      return firewall;
   }

   @Override
   public void configure(WebSecurity web) throws Exception {
      web.httpFirewall(this.allowUrlEncodedSlashHttpFirewall());
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      this.init(http);
      if (this.getApplicationContext().containsBean("customSecurityConfigure")) {
         Function<HttpSecurity, HttpSecurity> function = (Function<HttpSecurity, HttpSecurity>)this.getApplicationContext().getBean("customSecurityConfigure");
         function.apply(http);
      }

      Environment env = this.getApplicationContext().getEnvironment();
      http.csrf().disable();
      if (Boolean.TRUE.equals(env.getProperty("sunway.security.headers.xss", Boolean.class))) {
         http.headers().frameOptions().sameOrigin().addHeaderWriter(new XXssProtectionHeaderWriter());
      } else {
         http.headers().frameOptions().disable().addHeaderWriter(new XXssProtectionHeaderWriter());
      }

      GikamFilterRegistry.INSTANCE.getConfigList().forEach(c -> {
         if (FilterPos.AFTER.equals(c.getFilterPos())) {
            http.addFilterAfter(c.getFilter(), c.getFilterClass());
         } else if (FilterPos.BEFORE.equals(c.getFilterPos())) {
            http.addFilterBefore(c.getFilter(), c.getFilterClass());
         } else if (FilterPos.AT.equals(c.getFilterPos())) {
            http.addFilterAt(c.getFilter(), c.getFilterClass());
         }
      });
      http.authorizeRequests()
         .antMatchers(
            ArrayUtils.concat(
               new String[]{"/**/*.md", "/**/*.sql", "/**/*.jar", "/**/*.jsp", "/**/*.asp", "/**/*.aspx", "/**/*.php", "/**/*.py"},
               StringUtils.isBlank(this.denyAll) ? new String[0] : StringUtils.split(this.denyAll, ",")
            )
         )
         .denyAll()
         .antMatchers(
            ArrayUtils.concat(
               new String[]{
                  "/secure/core/module/mdm/user-permissions/login-org/queries/post-selectable",
                  "/secure/core/module/mdm/user-permissions/login-role/queries/post-selectable",
                  "/secure/core/module/item/tenants/login-tenant/queries/post-selectable",
                  "/*",
                  "/error/**",
                  "/static/**",
                  "/open/**",
                  "/files/open/**",
                  "/core/module/sys/login",
                  "/core/module/sys/licenses/number-of-online-users",
                  "/doc.html",
                  "/swagger-ui/**",
                  "/swagger-ui.html",
                  "/webjars/**"
               },
               StringUtils.isBlank(this.permitAll) ? new String[0] : StringUtils.split(this.permitAll, ",")
            )
         )
         .permitAll()
         .antMatchers(
            ArrayUtils.concat(
               new String[]{
                  "/module/index/workspaces/**",
                  "/core/module/sys/logins/account",
                  "/core/module/sys/role-users/**",
                  "/core/module/sys/logout",
                  "/core/module/item/files/**",
                  "/core/module/sys/statuses/action/keep-online",
                  "/core/module/sys/logs/**",
                  "/secure/**"
               },
               StringUtils.isBlank(this.authenticated) ? new String[0] : StringUtils.split(this.authenticated, ",")
            )
         )
         .authenticated();
   }

   static {
      GikamFilterRegistry.INSTANCE.registerAfter(new RefreshFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
   }
}
