package me.silvernine.jwttutorial.entity.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @Column(nullable = false)
    private String refreshToken;

}
