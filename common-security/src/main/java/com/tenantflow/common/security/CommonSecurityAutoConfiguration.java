package com.tenantflow.common.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Auto-configured because this package sits outside each service's component-scan root.
 */
@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class CommonSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtValidator jwtValidator(JwtProperties jwtProperties) {
        return new JwtValidator(jwtProperties);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass({OncePerRequestFilter.class, SecurityFilterChain.class})
    static class ServletJwtSecurityConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtValidator jwtValidator) {
            return new JwtAuthenticationFilter(jwtValidator);
        }

        @Bean
        @ConditionalOnMissingBean
        public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
            return new JwtAuthenticationEntryPoint();
        }

        /**
         * Backs off when a service declares its own chain (identity-service does).
         */
        @Bean
        @ConditionalOnMissingBean(SecurityFilterChain.class)
        public SecurityFilterChain commonJwtSecurityFilterChain(HttpSecurity http,
                                                                JwtAuthenticationFilter jwtAuthenticationFilter,
                                                                JwtAuthenticationEntryPoint entryPoint,
                                                                JwtProperties jwtProperties) throws Exception {
            String[] publicPaths = jwtProperties.getPublicPaths().toArray(new String[0]);

            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> {
                        if (publicPaths.length > 0) {
                            auth.requestMatchers(publicPaths).permitAll();
                        }
                        auth.anyRequest().authenticated();
                    })
                    .exceptionHandling(handling -> handling.authenticationEntryPoint(entryPoint))
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }
}
