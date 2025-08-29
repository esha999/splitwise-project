package com.splitwise.splitwise_backend;

import com.splitwise.splitwise_backend.model.User;
import com.splitwise.splitwise_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SplitwiseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SplitwiseBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository) {
        return args -> {
            // Create a new user
//            User user = new User("Esha", "esha@test.com", "123");
//            userRepository.save(user);

            // Fetch all users
            userRepository.findAll().forEach(System.out::println);
        };
    }
}

