package br.com.sigest.tesouraria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for H2 console
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                // Configure headers to allow frames for H2 console
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Static resources

                        .requestMatchers(AntPathRequestMatcher.antMatcher("/assets/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/relatorios/**"))
                        .hasAnyRole("ADMIN", "TESOUREIRO")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/cadastros/**"))
                        .hasAnyRole("ADMIN", "TESOUREIRO")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/movimentacoes/**"))
                        .hasAnyRole("ADMIN", "TESOUREIRO")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}