//package me.silvernine.jwttutorial.service;
//
//import lombok.RequiredArgsConstructor;
//import me.silvernine.jwttutorial.entity.user.User;
//import me.silvernine.jwttutorial.entity.user.UserRepository;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//
//@RequiredArgsConstructor
//@Service
//public class CustomUserDetailService implements UserDetailsService {
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByEmail(username)
//                .map(this::createUserDetails)
//                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다"));
//    }
//
//    private UserDetails createUserDetails(User user) {
//        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthorities().toString());
//
//        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getUserId()),
//                user.getPassword(),
//                Collections.singleton(grantedAuthority));
//    }
//
//}