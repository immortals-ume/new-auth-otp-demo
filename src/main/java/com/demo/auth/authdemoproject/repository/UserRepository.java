package com.demo.auth.authdemoproject.repository;


import com.demo.auth.authdemoproject.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select user from User user where user.activeInd = true and user.userName= :userNameOrEmail or user.email = : userNameOrEmail")
    User findByUserNameOrEmailAndActiveIndTrue(String userNameOrEmail);

    Boolean existsByUserName(String username);

}
