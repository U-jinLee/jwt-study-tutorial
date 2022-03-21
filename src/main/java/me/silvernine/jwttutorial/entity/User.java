package me.silvernine.jwttutorial.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name= "user")
@Entity
public class User {

    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserId;

    @Column(name="username", length = 50, unique = true)
    private String userName;

    @Column(name="password", length = 100)
    private String password;

    @Column(name="nickname", length = 50)
    private String nickname;

    @Column(name="activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}
