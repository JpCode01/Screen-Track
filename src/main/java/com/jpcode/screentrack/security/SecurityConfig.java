package com.jpcode.screentrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        auth -> {

                            /*
                             * ROTAS PÚBLICAS
                             */
                            auth.requestMatchers(
                                    "/auth/**",
                                    "/register",
                                    "/verify-account",
                                    "/verify-account/resend",
                                    "/refresh-token",
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/v3/api-docs.yaml"
                            ).permitAll();


                            /*
                             * REVIEWS PRIVADAS
                             */

                            // Usuário autenticado consulta suas próprias reviews
                            auth.requestMatchers(
                                    HttpMethod.GET,
                                    "/reviews/my-reviews"
                            ).hasRole("USER");


                            // Usuário cria review
                            auth.requestMatchers(
                                    HttpMethod.POST,
                                    "/reviews"
                            ).hasRole("USER");


                            // Usuário atualiza review
                            auth.requestMatchers(
                                    HttpMethod.PUT,
                                    "/reviews/**"
                            ).hasRole("USER");


                            // Usuário exclui apenas suas próprias reviews
                            auth.requestMatchers(
                                    HttpMethod.DELETE,
                                    "/reviews/my-reviews/**"
                            ).hasRole("USER");


                            // Admin exclui qualquer review
                            auth.requestMatchers(
                                    HttpMethod.DELETE,
                                    "/reviews/**"
                            ).hasRole("ADMIN");


                            /*
                             * CONSULTAS PÚBLICAS
                             */
                            auth.requestMatchers(
                                    HttpMethod.GET,
                                    "/scores/**",
                                    "/reviews/**",
                                    "/users/**",
                                    "/medias/**",
                                    "/feed/**"
                            ).permitAll(); 


                            /*
                             * PERFIL
                             */
                            auth.requestMatchers(
                                    HttpMethod.PUT,
                                    "/users/**"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.PATCH,
                                    "/users/**"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.DELETE,
                                    "/users/**"
                            ).hasRole("USER");


                            /*
                             * FAVORITOS
                             */

                            // Consulta pública
                            auth.requestMatchers(
                                    HttpMethod.GET,
                                    "/favorites/*/movies",
                                    "/favorites/*/series"
                            ).permitAll();

                            // consultas privadas
                            auth.requestMatchers(
                                    HttpMethod.POST,
                                    "/favorites"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.PUT,
                                    "/favorites/**"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.DELETE,
                                    "/favorites/**"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.GET,
                                    "/favorites/me/**"
                            ).hasRole("USER");


                            /*
                             * WATCHLIST
                             */
                            auth.requestMatchers(
                                    HttpMethod.GET,
                                    "/watchlist/users/**"
                            ).permitAll();
                            auth.requestMatchers(
                                    HttpMethod.GET,
                                    "/watchlist/me"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.POST,
                                    "/watchlist"
                            ).hasRole("USER");

                            auth.requestMatchers(
                                    HttpMethod.DELETE,
                                    "/watchlist/**"
                            ).hasRole("USER");


                            /*
                             * ADMIN
                             */
                            auth.requestMatchers("/admin/**")
                                    .hasRole("ADMIN");

                            auth.anyRequest().authenticated();

                        }
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
