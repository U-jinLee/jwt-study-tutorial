package me.silvernine.jwttutorial.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.entity.user.User;
import me.silvernine.jwttutorial.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

//    @PostMapping("/join")
//    public ResponseEntity join(@RequestBody UserDto userDto) {
//        Long result = userService.join(userDto);
//
//        log.info("result =============>"+result);
//        return result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
//    }
//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody UserDto userDto, HttpServletResponse response) {
//        User user = userService.findUserByEmail(userDto);
//        boolean checkPw = userService.checkPassword(user, userDto);
//        if(!checkPw) {
//            throw new IllegalArgumentException("아이디 혹은 비밀번호가 잘못됐습니다");
//        }
//
////        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
//
//        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
//        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRoles());
//
//        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
//        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);
//
//        //리프레시 토큰 저장소에 저장
//        tokenRepository.save(new RefreshToken(refreshToken));
//
//        return ResponseEntity.ok().body("login success");
//    }
//
//    @GetMapping("/test")
//    public String test() {
//        return "Hello, User?";
//    }

}