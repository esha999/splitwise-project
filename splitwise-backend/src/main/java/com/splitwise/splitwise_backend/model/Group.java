package com.splitwise.splitwise_backend.model;

//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//@Table(name = "groups") // 'group' is reserved keyword in SQL
//@Data @NoArgsConstructor @AllArgsConstructor @Builder
//public class Group {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @ManyToOne
//    @JoinColumn(name = "created_by", nullable = false)
//    private User createdBy;
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "group_members",
//            joinColumns = @JoinColumn(name = "group_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> members = new HashSet<>();
//}
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "groups")
@Data @NoArgsConstructor @AllArgsConstructor
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}


