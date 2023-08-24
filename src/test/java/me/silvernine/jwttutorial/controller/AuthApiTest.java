package me.silvernine.jwttutorial.controller;

import me.silvernine.jwttutorial.BaseTest;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.global.config.RestDocsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RestDocsConfiguration.class)
@AutoConfigureRestDocs
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
        )
          .andDo(print())
          .andDo(document("sign-up",
            requestFields(
              fieldWithPath("userName").description("Name of user"),
              fieldWithPath("password").description("Password of user"),
              fieldWithPath("nickName").description("NickName of user")
            ),
            responseFields(
              fieldWithPath("userId").description("Primary id of user"),
              fieldWithPath("username").description("Name of user"),
              fieldWithPath("password").description("Password of user"),
              fieldWithPath("nickname").description("Nickname of user"),
              fieldWithPath("activated").description("Activated status of user"),
              fieldWithPath("authorities[].name").description("Authorities of user")
            )
            ))
          .andExpect(status().isCreated())
        ;

    }
}