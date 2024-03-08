package com.shopme.admin.security;

//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebSecurityConfig {
    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    public class SecurityConfiguration {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll() // Cho phép truy cập tất cả các yêu cầu mà không cần xác thực
                    .and();

            return http.build();
        }
    }
}

/* Để sử dụng được WebSecurityConfigurerAdapter thì phải import vào maven
<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-config</artifactId>
			<version> 5.6.12</version>
</dependency>
_ in Spring Security 5.7.0-M2 WebSecurityConfigurerAdapter was deprecated.

_ Vẫn không sử dụng được WebSecurityConfigurerAdapter vì bị xung đột.

_ Phần security này cần phải học thật cẩn thận.
 */