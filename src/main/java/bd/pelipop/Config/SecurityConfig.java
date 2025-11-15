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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

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
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> { // Esta configuración de CORS es la que Spring Security usa directamente.
                    CorsConfiguration configuration = new CorsConfiguration();
                    // Usar addAllowedOriginPattern para mayor flexibilidad si los puertos cambian o hay subdominios.
                    configuration.addAllowedOriginPattern("http://localhost:5000");
                    configuration.addAllowedOriginPattern("http://localhost:3000");
                    configuration.addAllowedOriginPattern("http://localhost:5173");
                    configuration.addAllowedOriginPattern("http://localhost:4173");

                    configuration.addAllowedMethod("*"); // Permite todos los métodos (GET, POST, PUT, DELETE, etc.)
                    configuration.addAllowedHeader("*"); // Permite todas las cabeceras
                    configuration.setAllowCredentials(true); // Importante para cookies, autenticación basada en sesión, etc.
                    return configuration;
                }))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/pelipop/auth/**").permitAll() // Login
                                .requestMatchers(HttpMethod.POST, "/pelipop/users/**").permitAll() // Registro de usuarios
                                // Permitir GET para visualización de contenido público
                                .requestMatchers(HttpMethod.GET, "/pelipop/movies/**").permitAll()
                                .anyRequest().authenticated() // El resto requiere autenticación
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }








}
