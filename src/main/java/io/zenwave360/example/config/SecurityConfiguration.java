package io.zenwave360.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/swagger-config").permitAll()
                                .requestMatchers("/apis/**").permitAll()
                                .requestMatchers("/.well-known/**").permitAll()
                                .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
                .httpBasic(Customizer.withDefaults());
        // @formatter:on
        return http.build();
    }


    @Bean
    AuditorAware<String> springSecurityAuditorAware() {
        return new AuditorAware<String>() {

            @Override
            public Optional<String> getCurrentAuditor() {
                return Optional.of(getCurrentUserLogin().orElse("system"));
            }

            /**
             * Get the login of the current user.
             *
             * @return the login of the current user.
             */
            public static Optional<String> getCurrentUserLogin() {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
            }

            private static String extractPrincipal(Authentication authentication) {
                if (authentication == null) {
                    return null;
                } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
                    return springSecurityUser.getUsername();
                } else if (authentication.getPrincipal() instanceof String stringPrincipal) {
                    return stringPrincipal;
                }
                return null;
            }

        };
    }
}
