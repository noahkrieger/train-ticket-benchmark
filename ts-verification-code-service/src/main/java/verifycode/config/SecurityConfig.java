package verifycode.config;

import edu.fudan.common.security.jwt.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//

import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * @author LeBW
 *
 * Modified to remove WebSecurityConfigurerAdapter, which has been removed from Spring
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig  {

    /**
     * load password encoder
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    /**
//     * allow cors domain
//     * header  By default, only six fields can be taken from the header, and the other fields can only be specified in the header.
//     * credentials   Cookies are not sent by default and can only be true if a Cookie is needed
//     * Validity of this request
//     *
//     * @return WebMvcConfigurer
//     */
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins(ALL)
//                        .allowedMethods(ALL)
//                        .allowedHeaders(ALL)
//                        .allowCredentials(false)
//                        .maxAge(3600);
//            }
//        };
//    }

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
                    .requestMatchers("/api/v1/verifycode/**").permitAll()
                    .anyRequest().authenticated())
                // add custom JWT filter
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class)
                // disable cache
                .headers(customizer -> customizer.cacheControl(Customizer.withDefaults()))
                // build the security filter chain
                .build();
    }

//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.httpBasic().disable()
//                // close default csrf
//                .csrf().disable()
//                // close session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/v1/verifycode/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        // close cache
//        httpSecurity.headers().cacheControl();
//    }
}
