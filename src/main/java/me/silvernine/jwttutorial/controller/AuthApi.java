package me.silvernine.jwttutorial.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.silvernine.jwttutorial.dto.LoginRequestDto;
import me.silvernine.jwttutorial.dto.TokenResponseDto;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.entity.user.User;
import me.silvernine.jwttutorial.global.dao.RedisDao;
import me.silvernine.jwttutorial.jwt.JwtFilter;
import me.silvernine.jwttutorial.jwt.JwtTokenProvider;
import me.silvernine.jwttutorial.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthApi {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisDao redisDao;

    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        //1. AuthenticationManager를 통해 인증을 시도하고 인증이 성공하면 Authentication 객체를 리턴 받는다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        // 토큰의 인증 작업 수행, 해당 부분에 왔을 때 UserDetailsServiceImpl의 loadUserByUsername() 메소드가 실행된다.
        Authentication authentication = authenticationManager.getObject().authenticate(authenticationToken);

        //2. SecurityContextHolder에 위에서 생성한 Authentication 객체를 저장한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //3. JwtTokenProvider를 통해 JWT 토큰을 생성한다.
        String jwtToken = jwtTokenProvider.createToken(authentication);

        //레디스에 해당 토큰값 저장

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(loginRequestDto.getUsername(), jwtToken);
        log.info("Jwt token ::{}",valueOperations.get(loginRequestDto.getUsername()));

        //4. 생성한 JWT 토큰을 Response Header에 담아서 리턴한다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwtToken);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(new TokenResponseDto(jwtToken));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.join(userDto));
    }
}
