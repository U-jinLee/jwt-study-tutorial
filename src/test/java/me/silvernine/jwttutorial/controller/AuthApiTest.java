package me.silvernine.jwttutorial.controller;

import me.silvernine.jwttutorial.BaseTest;
import me.silvernine.jwttutorial.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends BaseTest {

    @Test
    @DisplayName("유저 회원가입 테스트")
    void signUp_test() throws Exception {
        UserDto userDto = new UserDto(
                "Jinjinjara",
                "1q2w3e4r!",
                "Jinjinjara"
        );

        mvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
        ).andDo(print())
                .andExpect(
                        status().isCreated()
                );

    }
}