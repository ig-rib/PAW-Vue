package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.JWTAuthenticationFilter;
import ar.edu.itba.paw.webapp.auth.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.util.StreamUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

@EnableWebSecurity
@ComponentScan({ "ar.edu.itba.paw.webapp.auth"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetails;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/goodbye", "/login", "/login_error", "/signup", "/send-recovery-email").anonymous()
                .antMatchers("/recover-password", "/reset-password").anonymous()
                .antMatchers("/verify-email", "/resend-email-verification").hasAnyRole("USER", "ADMIN")
                .antMatchers("/flagged/**", "/snippet/**/flag").hasRole("ADMIN")
                .antMatchers("/favorites/**", "/following/**", "/upvoted/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/snippet/**/votes", "/snippet/**/fav").hasAnyRole("USER", "ADMIN")
                .antMatchers("/snippet/create", "/snippet/**/delete", "/snippet/**/report", "/snippet/**/report/delete"). hasRole("USER")
                .antMatchers("/user/**/active", "/user/**/deleted", "user/**/active/edit", "user/**/deleted/edit"). hasRole("USER")
                .antMatchers("/tags/**/follow").hasAnyRole("USER", "ADMIN")
                .antMatchers("/tags/**/delete", "/languages/**/delete").hasRole("ADMIN")
                .antMatchers("/**")
                .permitAll()
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
        ;
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/src/main/resources/css/**", "/src/main/resources/js/**", "/src/main/resources/img/**", "/favicon.ico", "/403");
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
        corsConfiguration.addExposedHeader("link");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    private String getRememberMeKey() {
        ClassPathResource keyResource = new ClassPathResource("authKey.key");
        try {
            return StreamUtils.copyToString(keyResource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Remember me key threw IOException");
        }
    }

}
