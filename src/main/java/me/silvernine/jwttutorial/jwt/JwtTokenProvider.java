package me.silvernine.jwttutorial.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.silvernine.jwttutorial.entity.user.TokenRepository;
import me.silvernine.jwttutorial.entity.user.UserRepository;
import me.silvernine.jwttutorial.service.CustomUserDetailService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
//    private long tokenValidTime = 30 * 60 * 1000L; // 토큰 유효 시간 | 30분
    private long accessTokenValidTime = 10 * 20 * 1000;
    private long refreshTokenValidTime = 30 * 60 * 1000;


    private final CustomUserDetailService customUserDetailService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    // 의존성 주입 후, 초기화 수행
    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        log.info("secretKey=======================>"+secretKey);
    }

    public String createAccessToken(String email, List<String> roles) {
        return this.createToken(email, roles, accessTokenValidTime);
    }

    public String createRefreshToken(String email, List<String> roles) {
        return this.createToken(email, roles, refreshTokenValidTime);
    }

    //jwt 토큰 생성
    public String createToken(String user, List<String> roles, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(user);
        claims.put("roles", roles);

        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenValid))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT 토큰에서 인증정보 조회
    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserEmail(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserEmail(String jwtToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져오기 "authorization: token"
//    public String resolveToken(HttpServletRequest request) {
//        if(request.getHeader("authorization") != null) {
//            return request.getHeader("authorization").substring(7);
//        }
//        return null;
//    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token'
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("authorization") != null) return request.getHeader("authorization").substring(7);
        return null;
    }
    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token'
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null) return request.getHeader("refreshToken").substring(7);
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch(Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("authorization", "bearer "+ accessToken);
    }
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "bearer "+ refreshToken);
    }

    public boolean existsRefreshToken(String refreshToken) {
        return tokenRepository.existsByRefreshToken(refreshToken);
    }
    public List<String> getRoles(String email) {
        return userRepository.findByEmail(email).get().getRoles();
    }
}
