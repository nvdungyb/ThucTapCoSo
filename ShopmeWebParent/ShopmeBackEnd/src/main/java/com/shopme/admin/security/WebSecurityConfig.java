package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        return new ShopmeUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeRequests(request -> request
                        .requestMatchers("/resources/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/signin").permitAll()
                        .requestMatchers("/users/**").hasAuthority("Admin")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .loginProcessingUrl("/authentication/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/process_logout")           // Vì csrf đã disable nên bất kì http request nào cũng được cho phép, mặc định csrf được kích hoạt thì logout url sẽ là POST method và được khyến khích sử dụng POST để tránh csrf attack.
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }


}
