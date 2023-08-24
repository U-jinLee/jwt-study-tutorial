package me.silvernine.jwttutorial.controller;

import me.silvernine.jwttutorial.BaseTest;
import me.silvernine.jwttutorial.dto.LoginRequestDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@AutoConfigureRestDocs
class AuthApiTest extends BaseTest {

    @Test
    @DisplayName("유저 회원 가입 테스트")
    void sign_up_test() throws Exception {
        //given
        UserDto userDto = new UserDto(
                "Jinjinjara",
                "1q2w3e4r!",
                "Jinjinjara"
        );
        //when
        mvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
        )
          //then
          .andDo(print())

          .andExpect(status().isCreated())
          .andExpect(jsonPath("userId").exists())
          .andExpect(jsonPath("username").exists())
          .andExpect(jsonPath("password").exists())
          .andExpect(jsonPath("nickname").exists())
          .andExpect(jsonPath("activated").exists())
          .andExpect(jsonPath("authorities").exists())

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
            ));

    }

    @Test
    @DisplayName("유저 로그인 테스트")
    void login_test() throws Exception {
        //given
        String userName = "admin";
        String password = "1q2w3e4r!";
        LoginRequestDto requestDto = new LoginRequestDto(userName, password);
        //when
        mvc
          .perform(
            post("/api/auth/authenticate")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestDto))
          )
          //then
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(header().exists("Authorization"))
          .andExpect(jsonPath("token").exists())
          .andDo(document("login",
            requestFields(
              fieldWithPath("username").description("Name of user"),
              fieldWithPath("password").description("Password of user")
            ),
            responseFields(
              fieldWithPath("token").description("JWT Token")
            )
            )
          );
    }
}