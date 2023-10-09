package com.demo.auth.authdemoproject.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_profile", schema = "public")
public class UserProfile {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JsonIgnore
    @NotBlank
    @Size(max = 20)
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Email
    @NotBlank
    @Size(max = 50)
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "active_ind", nullable = false)
    private Boolean activeInd;
}
