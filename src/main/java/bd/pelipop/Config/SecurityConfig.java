package bd.pelipop.Config;

import bd.pelipop.Security.JWT.AuthEntryPointJwt;
import bd.pelipop.Security.JWT.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity()
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsRaw;

    private final AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {

                    List<String> cleanedOrigins = Arrays.stream(allowedOriginsRaw.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .toList();

                    CorsConfiguration configuration = new CorsConfiguration();

                    if (!cleanedOrigins.isEmpty()) {
                        configuration.setAllowedOrigins(cleanedOrigins);
                        configuration.setAllowCredentials(true);
                    }

                    configuration.addAllowedMethod("*");
                    configuration.addAllowedHeader("*");
                    return configuration;
                }))
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/pelipop/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pelipop/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pelipop/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pelipop/countries").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pelipop/genders").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pelipop/movies/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/pelipop/movies/**").permitAll()
                        .requestMatchers("/pelipop/admin/**").hasRole("ADMIN")
                        .requestMatchers("/healthz/**", "/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/keep-alive/ping").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
