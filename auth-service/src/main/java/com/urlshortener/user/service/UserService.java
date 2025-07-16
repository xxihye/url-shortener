package com.urlshortener.user.service;

import com.urlshortener.user.entity.User;
import com.urlshortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(String userId, String password){
        User user = User.builder()
                        .userId(userId)
                        .password(password)
                        .build();

        userRepository.save(user);
    }

    public Optional<User> findByUserId(String userId){
        return userRepository.findByUserId(userId);
    }

    public boolean existsByUserId(String userId){
        return userRepository.existsByUserId(userId);
    }

    public Optional<User> findByUserNo(Long userNo){
        return userRepository.findByUserNo(userNo);
    }
}
