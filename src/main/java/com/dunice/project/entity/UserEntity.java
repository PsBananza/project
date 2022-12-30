package com.dunice.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "users")
@Accessors(chain = true)
public class UserEntity {

    private String avatar;
    private String email;
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String role;
    private String password;

}
