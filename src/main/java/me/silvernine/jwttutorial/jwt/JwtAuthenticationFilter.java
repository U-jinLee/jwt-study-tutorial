package me.silvernine.jwttutorial.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT를 받아온다.
        String accessToken = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);
        if(accessToken != null) {
            if(jwtTokenProvider.validateToken(accessToken)) {
                this.setAuthentication(accessToken);
            }
        } else if(!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
            boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);

            if(validateRefreshToken && isRefreshToken) {
                String email = jwtTokenProvider.getUserEmail(refreshToken);
                List<String> roles = jwtTokenProvider.getRoles(email);
                String newAccessToken = jwtTokenProvider.createAccessToken(email, roles);

                jwtTokenProvider.setHeaderAccessToken((HttpServletResponse) response, newAccessToken);
                this.setAuthentication(newAccessToken);
            }
        }
        chain.doFilter(request, response);
    }

    public void setAuthentication(String token) {
        //토큰으로 유저 정보를 받아온다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        //SecurityContext에 Authentication 객체를 저장한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}