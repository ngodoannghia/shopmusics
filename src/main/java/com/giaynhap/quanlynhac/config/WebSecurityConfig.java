package com.giaynhap.quanlynhac.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Value("${firebase.pathjson}")
    String firebasePathJson;
    @Value("${firebase.database}")
    String firebaseDatabaseUrl;
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        FirebaseConfig.createFireBaseApp(firebasePathJson,firebaseDatabaseUrl);
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors().and().csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests()
                .antMatchers(
                        "/user/authenticate",
	                    "/refreshToken",
	                    "/admin/authenticate",
	                    "/admin/add",
	                    "/admin/signup",
	                    "/makepass/*",
	                    "/user/register",
	                    "/music/demo/*",
	                    "/music/detail/*",
	                    "/music/detailbydemo/*",
	                    "/music/getResource/*",
	                    "/demo/stream/*/*",
	                    "/admin/stream/*/*",
	                    "/user/info/*",
	                    "/utils/letter/avatar/*",
	                    "/util/photo/*",
	                    "/util/avatar/*",
	                    "/user/forget/sendemail/*",
	                    "/very/email",
	                    "/music/categories",
	                    "/music/category/*/*",
	                    "/very/success",
                        "/utils/make_public/*",
                        "/utils/get_public/*",
                        "/utils/make_demo_public/*",
                        "/static/photo/*"
                        ).permitAll()
		        .antMatchers("/ws").permitAll()
                .anyRequest()
                .authenticated().
                and(). exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

}