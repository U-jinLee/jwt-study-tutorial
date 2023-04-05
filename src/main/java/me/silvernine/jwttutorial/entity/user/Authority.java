package me.silvernine.jwttutorial.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "authority")
public class Authority {
    @Id
    @Column(name = "authority_name", length = 50)
    private String name;
}
