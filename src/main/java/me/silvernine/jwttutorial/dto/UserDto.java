package me.silvernine.jwttutorial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class UserDto {
    @NotNull
    @Size(min = 3, max = 50)
    private String userName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 50)
    private String password;
    @NotNull
    @Size(min = 3, max = 50)
    private String nickName;
}
