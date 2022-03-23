package me.silvernine.jwttutorial.service;

import lombok.RequiredArgsConstructor;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.entity.user.User;
import me.silvernine.jwttutorial.entity.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long join(UserDto userDto) {
        long userId = userRepository.save(
                User.builder()
                        .email(userDto.getEmail())
                        .password(passwordEncoder.encode(userDto.getPassword()))
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build())
                .getId();

        return userId;
    }
    public User findUserByEmail(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("아이디 혹은 비밀번호가 잘못됐습니다."));
        return user;
    }
    public boolean checkPassword(User user, UserDto userDto) {
        return passwordEncoder.matches(userDto.getPassword(), user.getPassword());
    }
}