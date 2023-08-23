package com.example.social_media_api.model;

import com.example.social_media_api.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String email;

    private String password;

    private Set<Role> roles;

}

