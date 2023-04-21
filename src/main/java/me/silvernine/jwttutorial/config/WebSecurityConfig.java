package me.silvernine.jwttutorial.config;

import lombok.RequiredArgsConstructor;
import me.silvernine.jwttutorial.jwt.JwtAccessDeniedHandler;
import me.silvernine.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import me.silvernine.jwttutorial.jwt.JwtSecurityConfig;
import me.silvernine.jwttutorial.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 회원가입 시 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
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
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //jwt는 필요가 없으므로 csrf 비활성화
                .csrf().disable()

                //세션 사용하지 않음
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                //Exception 처리를 위한 부분
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()

                .authorizeRequests() //ServletRequest를 사용하는 요청에 대한 접근 제한 설정
                .antMatchers(HttpMethod.POST, "/api/auth/**").permitAll() //검증 없이 이용가능(Post 요청 가능)
                .antMatchers("/redis/**").permitAll()
                .anyRequest().authenticated()
                .and()

                //JwtSecurityConfig 적용
                .apply(new JwtSecurityConfig(jwtTokenProvider));

    }
}