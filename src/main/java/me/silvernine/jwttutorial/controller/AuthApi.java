package me.silvernine.jwttutorial.controller;

import lombok.RequiredArgsConstructor;
import me.silvernine.jwttutorial.dto.LoginRequestDto;
import me.silvernine.jwttutorial.dto.TokenResponseDto;
import me.silvernine.jwttutorial.jwt.JwtFilter;
import me.silvernine.jwttutorial.jwt.JwtTokenProvider;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManager;
    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        //1. AuthenticationManager를 통해 인증을 시도하고 인증이 성공하면 Authentication 객체를 리턴받는다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManager.getObject().authenticate(authenticationToken);

        //2. SecurityContextHolder에 위에서 생성한 Authentication 객체를 저장한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //3. JwtTokenProvider를 통해 JWT 토큰을 생성한다.
        String jwtToken = jwtTokenProvider.createToken(authentication);

        //4. 생성한 JWT 토큰을 Response Header에 담아서 리턴한다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwtToken);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(new TokenResponseDto(jwtToken));
    }
}
