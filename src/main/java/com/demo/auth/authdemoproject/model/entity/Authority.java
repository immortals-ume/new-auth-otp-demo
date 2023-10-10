package com.demo.auth.authdemoproject.model.entity;


import com.demo.auth.authdemoproject.model.enums.AuthorityName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "authority")
public class Authority {

    @Column(name = "name", length = 50)
    @NotNull
    @Id
    @Enumerated(EnumType.STRING)
    private AuthorityName oname;

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    private List<User> users;

}