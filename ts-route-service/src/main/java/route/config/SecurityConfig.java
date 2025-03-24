package route.config;

import edu.fudan.common.security.jwt.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * @author fdse
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * load password encoder
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    public class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins(ALL) // Replace with your origin
                    .allowedMethods(ALL)
                    .allowedHeaders(ALL)
                    .allowCredentials(false)
                    .maxAge(3600);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // disable basic authentication
                .httpBasic(AbstractHttpConfigurer::disable)
                // disable default CSRF protection
                .csrf(AbstractHttpConfigurer::disable)
                // configure stateless session management
                .sessionManagement(cfg -> cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // set up authorization rules
                .authorizeHttpRequests( match -> match
                        .requestMatchers("/api/v1/routeservice/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/routeservice/routes").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/routeservice/routes/*").hasAnyRole("ADMIN")
                        .requestMatchers("/swagger-ui.html", "/webjars/**", "/images/**",
                                "/configuration/**", "/swagger-resources/**", "/v2/**").permitAll()
                        .anyRequest().authenticated())
                // add custom JWT filter
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class)
                // disable cache
                .headers(customizer -> customizer.cacheControl(Customizer.withDefaults()))
                // build the security filter chain
                .build();
    }




//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.httpBasic().disable()
//                // close default csrf
//                .csrf().disable()
//                // close session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/v1/routeservice/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/v1/routeservice/routes").hasAnyRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/api/v1/routeservice/routes/*").hasAnyRole("ADMIN")
//                .antMatchers("/swagger-ui.html", "/webjars/**", "/images/**",
//                        "/configuration/**", "/swagger-resources/**", "/v2/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        // close cache
//        httpSecurity.headers().cacheControl();
//    }
}