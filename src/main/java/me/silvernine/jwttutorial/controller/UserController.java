package me.silvernine.jwttutorial.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.silvernine.jwttutorial.entity.user.User;
import me.silvernine.jwttutorial.entity.user.UserRepository;
import me.silvernine.jwttutorial.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/join")
    public Long join(@RequestBody Map<String, String> user) {
        return userRepository.save(User.builder()
                        .email(user.get("email"))
                        .password(user.get("password"))
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build()).getId();
    }
    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        log.info(member.getPassword()+", "+user.get("password"));
//        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
//            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
//        }
        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
    }
}