package me.silvernine.jwttutorial.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(token != null && jwtTokenProvider.validateToken(token)) {
            //토큰이 유효할 시 토큰으로부터 유저 정보를 받아온다
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            //SecurityContext에 Authetication 객체 저장.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

}