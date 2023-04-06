package me.silvernine.jwttutorial.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성, 추출 그리고 검증에 관한 클래스
 */
@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secretKey;
    private final long tokenValidityInMilliseconds;
    private Key key;

    // 1. Bean 생성 후 주입 받은 후에
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.token-validity-in-seconds}") Long tokenValidityInSeconds) {
        this.secretKey = secretKey;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds;
    }
    // 2. secret 값을 Base64로 디코딩해 Key변수에 할당
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //jwt 토큰 생성
    public String createToken(Authentication authentication) {
        //권한 값을 받아 하나의 문자열로 합침
        String authorities = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())//페이로드 주제 정보
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * 토큰에서 인증 정보 조회 후 Authentication 객체 리턴
     * @param token
     * @return
     */
    //토큰 -> 클레임 추출 -> 유저 객체 제작 -> Authentication 객체 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<? extends SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 필터에서 사용할 토큰 검증
     * @param token 필터 정보
     * @return 토큰이 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

//    public String createAccessToken(String email, List<String> roles) {
//        return this.createToken(email, roles, accessTokenValidTime);
//    }
//
//    public String createRefreshToken(String email, List<String> roles) {
//        return this.createToken(email, roles, refreshTokenValidTime);
//    }

//
//    // JWT 토큰에서 인증정보 조회
//    public Authentication getAuthentication(String jwtToken) {
//        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserEmail(jwtToken));
//        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
//    }
//
//    // 토큰에서 회원 정보 추출
//    public String getUserEmail(String jwtToken) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getSubject();
//    }

    // Request의 Header에서 token 값을 가져오기 "authorization: token"
//    public String resolveToken(HttpServletRequest request) {
//        if(request.getHeader("authorization") != null) {
//            return request.getHeader("authorization").substring(7);
//        }
//        return null;
//    }
//
//    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token'
//    public String resolveAccessToken(HttpServletRequest request) {
//        if(request.getHeader("authorization") != null) return request.getHeader("authorization").substring(7);
//        return null;
//    }
//    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token'
//    public String resolveRefreshToken(HttpServletRequest request) {
//        if(request.getHeader("refreshToken") != null) return request.getHeader("refreshToken").substring(7);
//        return null;
//    }
//
//    // 토큰의 유효성 + 만료일자 확인
//    public boolean validateToken(String jwtToken) {
//        try {
//            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch(Exception e) {
//            log.info(e.getMessage());
//            return false;
//        }
//    }
//
//    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
//        response.setHeader("authorization", "bearer "+ accessToken);
//    }
//    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
//        response.setHeader("refreshToken", "bearer "+ refreshToken);
//    }
//
//    public boolean existsRefreshToken(String refreshToken) {
//        return tokenRepository.existsByRefreshToken(refreshToken);
//    }
//    public List<String> getRoles(String email) {
//        return userRepository.findByEmail(email).get().getAuthorities().stream().map(authority -> authority.getName()).collect(Collectors.toList());
//    }
}
