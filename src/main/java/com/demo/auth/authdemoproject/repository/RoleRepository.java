package com.demo.auth.authdemoproject.repository;


import com.demo.auth.authdemoproject.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    void delete(Role role);

}