
package com.splitwise.splitwise_backend.repository;

//import com.splitwise.splitwise_backend.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//    // You can add custom queries later, e.g. findByEmail
//    User findByEmail(String email);
//}
import com.splitwise.splitwise_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> { }