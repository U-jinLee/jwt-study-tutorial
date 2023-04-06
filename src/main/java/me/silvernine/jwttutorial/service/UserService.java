package me.silvernine.jwttutorial.service;

import lombok.RequiredArgsConstructor;
import me.silvernine.jwttutorial.entity.user.User;
import me.silvernine.jwttutorial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 로그인 시에 DB에서 유저정보와 권한정보를 가져와 UserDetails 타입으로 리턴한다.
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
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
    };

//    public Long join(UserDto userDto) {
//        long userId = userRepository.save(
//                User.builder()
//                        .email(userDto.getEmail())
//                        .password(passwordEncoder.encode(userDto.getPassword()))
//                        .roles(Collections.singletonList("ROLE_USER"))
//                        .build())
//                .getId();
//
//        return userId;
//    }
//    public User findUserByEmail(UserDto userDto) {
//        User user = userRepository.findByEmail(userDto.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("아이디 혹은 비밀번호가 잘못됐습니다."));
//        return user;
//    }
//    public boolean checkPassword(User user, UserDto userDto) {
//        return passwordEncoder.matches(userDto.getPassword(), user.getPassword());
//    }
}