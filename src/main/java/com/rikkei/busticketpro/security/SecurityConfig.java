package com.rikkei.busticketpro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth
                // Tài nguyên tĩnh & trang công khai
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**",
                    "/",
                    "/search",
                    "/trips/**",
                    "/api/seats/**",
                    "/book-ticket",
                    "/tickets/lookup",
                    "/tickets/{code}",
                    "/cancel-ticket",
                    "/payment/**",
                    "/register",
                    "/login",
                    "/access-denied"
                ).permitAll()

                // Chỉ ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // STAFF và ADMIN
                .requestMatchers("/staff/**").hasAnyRole("STAFF", "ADMIN")

                // Hành khách đã đăng nhập (xem lịch sử vé, hồ sơ)
                .requestMatchers("/profile/**", "/my-tickets/**")
                    .hasAnyRole("PASSENGER", "STAFF", "ADMIN")

                // Tất cả URL còn lại cần xác thực
                .anyRequest().authenticated()
            )

            // form login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)          // Điều hướng theo Role
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // trang 403
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
