package me.silvernine.jwttutorial.service;

import lombok.RequiredArgsConstructor;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.entity.user.Authority;
import me.silvernine.jwttutorial.entity.user.User;
import me.silvernine.jwttutorial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * 로그인 시에 DB에서 유저정보와 권한정보를 가져와 UserDetails 타입으로 리턴한다.
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if(user.isActivated()) throw new RuntimeException(username + " -> 활성화되지 않은 사용자입니다.");

        List<GrantedAuthority> authorities = user.getAuthorities().stream().map(auth ->
                        new SimpleGrantedAuthority(auth.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Transactional
    public User join(UserDto userDto) {
        this.userRepository.findByUsername(userDto.getUserName()).ifPresent(user ->
                new RuntimeException(user.getUserId() + "는 이미 존재하는 아이디입니다."));

        Authority authority = Authority.builder()
                .name("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUserName())
                .password(this.passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickName())
                .activated(true)
                .authorities(Collections.singleton(authority))
                .build();

        return this.userRepository.save(user);
    }
}