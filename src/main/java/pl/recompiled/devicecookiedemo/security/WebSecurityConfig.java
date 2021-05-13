package pl.recompiled.devicecookiedemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.recompiled.devicecookiedemo.security.devicecookie.DeviceCookieService;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final DeviceCookieService deviceCookieService;
    private final WebSecurityProperties properties;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        final InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> builder = auth.inMemoryAuthentication();
        if (properties.getPredefinedUsers() != null && !properties.getPredefinedUsers().isEmpty()) {
            for (WebSecurityProperties.User user: properties.getPredefinedUsers()) {
                builder.withUser(user.getUsername())
                        .password(passwordEncoder().encode(user.getPassword()))
                        .roles(user.getRoles().toArray(new String[0]));
            }
        }
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new PreAuthFilter(deviceCookieService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlingFilter(), PreAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/login*").permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                .failureHandler(new CustomAuthenticationFailureHandler(deviceCookieService))
                .successHandler(new CustomAuthenticationSuccessHandler(deviceCookieService))
                .and()

                .logout()
                .logoutSuccessHandler(((request, response, authentication) -> response.setStatus(OK.value())))
                .and()

                .httpBasic()
                .authenticationEntryPoint((request, response, authentication) -> response.setStatus(UNAUTHORIZED.value()))
                .and()

                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
