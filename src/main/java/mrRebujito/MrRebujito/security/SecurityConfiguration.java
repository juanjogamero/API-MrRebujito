package mrRebujito.MrRebujito.security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean DaoAuthenticationProvider authenticationProvider(UserDetailsService users,PasswordEncoder encoder){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return provider;
    }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception{
        return config.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,JWTAuthenticationFilter filter,DaoAuthenticationProvider provider,
                                  CorsConfigurationSource corsSource)throws Exception{
        return http.csrf(csrf->csrf.disable())
            .cors(cors->cors.configurationSource(corsSource))
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(provider)
            .authorizeHttpRequests(auth->auth
                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/login","/caseta","/socio").permitAll()
                .requestMatchers(HttpMethod.GET,"/ayuntamiento","/ayuntamiento/{id}").permitAll()
                .requestMatchers(HttpMethod.GET,"/caseta/socios","/caseta/carta").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.GET,"/caseta","/caseta/{id}","/caseta/carta/{id}",
                        "/producto","/producto/{id}").permitAll()
                .requestMatchers(HttpMethod.POST,"/administrador","/ayuntamiento").hasAuthority("ADMIN")
                .requestMatchers("/administrador","/administrador/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/ayuntamiento").hasAuthority("AYUNTAMIENTO")
                .requestMatchers(HttpMethod.DELETE,"/ayuntamiento").hasAuthority("AYUNTAMIENTO")
                .requestMatchers(HttpMethod.POST,"/caseta/solicitud/{ayuntamientoId}",
                        "/caseta/socios/{idSocio}","/producto").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.DELETE,"/caseta/socios/{idSocio}","/producto/{id}").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.PUT,"/caseta","/producto/{id}").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.DELETE,"/caseta").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.GET,"/solicitud/caseta","/solicitud/Caseta").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.GET,"/solicitud/ayuntamiento","/solicitud/Ayuntamiento").hasAuthority("AYUNTAMIENTO")
                .requestMatchers(HttpMethod.GET,"/solicitud/{id}").hasAnyAuthority("CASETA","AYUNTAMIENTO")
                .requestMatchers(HttpMethod.DELETE,"/solicitud/{id}").hasAuthority("CASETA")
                .requestMatchers(HttpMethod.PUT,"/solicitud/aceptar/{id}","/solicitud/rechazar/{id}").hasAuthority("AYUNTAMIENTO")
                .requestMatchers(HttpMethod.GET,"/socio/misCasetas").hasAuthority("SOCIO")
                .requestMatchers(HttpMethod.PUT,"/socio").hasAuthority("SOCIO")
                .requestMatchers(HttpMethod.DELETE,"/socio").hasAuthority("SOCIO")
                .anyRequest().denyAll()
            )
            .exceptionHandling(ex->ex
                .authenticationEntryPoint((req,res,error)->res.sendError(HttpStatus.UNAUTHORIZED.value(),"Autenticación requerida"))
                .accessDeniedHandler((req,res,error)->res.sendError(HttpStatus.FORBIDDEN.value(),"Permisos insuficientes")))
            .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(
        @Value("${app.cors.allowed-origins:http://localhost:4200}")String origins){
        CorsConfiguration cors=new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isEmpty()).toList());
        cors.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cors.setAllowedHeaders(java.util.List.of("Content-Type","Authorization"));
        cors.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource src=new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**",cors);
        return src;
    }
}
