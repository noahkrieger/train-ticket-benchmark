package auth.config;


import edu.fudan.common.security.jwt.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class WebSecurityConfig {


    @Autowired
    @Qualifier("userDetailServiceImpl")
    private UserDetailsService userDetailsService;

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManager() throws Exception {
//        return super.authenticationManager();
//    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
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
                        .requestMatchers("/api/v1/auth", "/api/v1/auth/hello", "/api/v1/user/hello").permitAll()
                        .requestMatchers("/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")
                        // create user and role while user register
                        .requestMatchers("/user/**").permitAll()
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


    /**
     * load password encoder
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/v1/auth", "/api/v1/auth/hello", "/api/v1/user/hello").permitAll()
//                .antMatchers("/api/v1/users/login").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/api/v1/users/*").hasRole("ADMIN")
//                // create user and role while user register
//                .antMatchers("/user/**").permitAll()
//                .antMatchers("/swagger-ui.html", "/webjars/**", "/images/**",
//                        "/configuration/**", "/swagger-resources/**", "/v2/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        httpSecurity.headers().cacheControl();
//    }
}
