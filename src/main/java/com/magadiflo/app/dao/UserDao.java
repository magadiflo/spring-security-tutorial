package com.magadiflo.app.dao;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {

    // password: 12345 para ambos
    private final static List<UserDetails> APPLICATION_USERS = Arrays.asList(
            new User("admin@gmail.com", "$2a$10$u98UVvWxO3kXqlt1dP9gNeKmh3xmBj2UwJmitxO8HaK/i5vjXfZu6", Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))),
            new User("user@gmail.com", "$2a$10$yzCRTNhP46MV4rmrYvSBvun5lHhCiuuvALdFNA.AKgMLDRfdIvAhq", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
    );

    public UserDetails findUserByEmail(String email) {
        return APPLICATION_USERS.stream()
                .filter(userDetails -> userDetails.getUsername().equals(email))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("No user was found!!!"));
    }
}
