package com.splitwise.splitwise_backend.service;

//import com.splitwise.splitwise_backend.model.User;
//import com.splitwise.splitwise_backend.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public User createUser(User user) {
//        return userRepository.save(user);
//    }
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public Optional<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }
//}
import com.splitwise.splitwise_backend.model.User;
import com.splitwise.splitwise_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;

    public User createUser(String name) {
        return userRepo.save(new User(null, name));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}