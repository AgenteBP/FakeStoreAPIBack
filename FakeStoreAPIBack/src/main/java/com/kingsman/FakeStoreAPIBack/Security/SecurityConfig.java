package com.kingsman.FakeStoreAPIBack.Security;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.kingsman.FakeStoreAPIBack.Security.Jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;
    private final HandlerExceptionResolver exceptionResolver;

    // Constructor explícito porque el HandlerExceptionResolver necesita @Qualifier (hay más de uno en el contexto)
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authProvider,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authProvider = authProvider;
        this.exceptionResolver = exceptionResolver;
    }

    // Reglas de acceso por URL. Que un CLIENT solo vea sus propias compras y direcciones
    // no se puede resolver acá (depende del dueño del dato): lo valida CurrentUserService en cada service.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Público: login, registro, catálogo e imágenes
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/images/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/{id}", "/api/categories/**")
                        .permitAll()
                        // Solo ADMIN: administrar catálogo y stock, ver todos los usuarios y compras,
                        // dar de baja, reactivar o borrar usuarios y cambiar el estado de una compra
                        .requestMatchers("/api/products/**", "/api/categories/**", "/api/inventory/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users", "/api/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/deactivate", "/api/users/*/activate")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status").hasRole("ADMIN")
                        // Todo lo demás pide estar logueado
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Los 401/403 que corta Spring Security antes de llegar al controller se mandan al
                // GlobalExceptionHandler, así responden con el mismo ErrorResponseDTO que el resto
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> exceptionResolver
                                .resolveException(request, response, null, authException))
                        .accessDeniedHandler((request, response, accessDeniedException) -> exceptionResolver
                                .resolveException(request, response, null, accessDeniedException)))
                .build();
    }

    // CORS a nivel de Spring Security: sin esto, el navegador recibe 401 en la consulta previa (OPTIONS)
    // que hace antes de mandar un request con el header Authorization
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
