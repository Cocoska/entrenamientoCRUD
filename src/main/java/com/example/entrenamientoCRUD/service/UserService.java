package com.example.entrenamientoCRUD.service;

import com.example.entrenamientoCRUD.model.User;
import com.example.entrenamientoCRUD.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetail) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userDetail.getName());
                    existingUser.setLastName(userDetail.getLastName());
                    existingUser.setEmail(userDetail.getEmail());
                    if (userDetail.getPassword() != null || userDetail.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(userDetail.getPassword()));
                    }
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("El usuario con el id:" + id + " no existe."));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("El usuario con el id:" + id + " no existe.");
        }
        userRepository.deleteById(id);
    }

    public Optional<User> autenticateUser(String name, String password) {
        return userRepository.findByName(name)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}
