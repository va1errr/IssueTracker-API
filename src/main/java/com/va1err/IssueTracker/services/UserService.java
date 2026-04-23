package com.va1err.IssueTracker.services;

import com.va1err.IssueTracker.dto.requests.UserRequest;
import com.va1err.IssueTracker.dto.responses.UserResponse;
import com.va1err.IssueTracker.exceptions.UserNotFoundException;
import com.va1err.IssueTracker.models.User;
import com.va1err.IssueTracker.repositories.UserRepository;
import com.va1err.IssueTracker.utils.UserUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return UserUtil.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(UserUtil::toResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        return UserUtil.toResponse(userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id)));
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id))
            throw new UserNotFoundException(id);

        userRepository.deleteById(id);
    }

    public UserResponse updateUserById(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        userRepository.save(user);

        return UserUtil.toResponse(user);
    }

}
