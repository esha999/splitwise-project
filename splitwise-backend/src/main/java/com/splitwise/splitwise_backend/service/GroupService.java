package com.splitwise.splitwise_backend.service;

//import com.splitwise.splitwise_backend.model.Expense;
//import com.splitwise.splitwise_backend.model.ExpenseShare;
//import com.splitwise.splitwise_backend.model.Group;
//import com.splitwise.splitwise_backend.model.User;
//import com.splitwise.splitwise_backend.repository.ExpenseRepository;
//import com.splitwise.splitwise_backend.repository.GroupRepository;
//import com.splitwise.splitwise_backend.repository.UserRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class GroupService {
//
//    private final GroupRepository groupRepository;
//    private final UserRepository userRepository;
//    private final ExpenseRepository expenseRepository; // ✅ Add this
//
//    public GroupService(GroupRepository groupRepository,
//                        UserRepository userRepository,
//                        ExpenseRepository expenseRepository) {  // ✅ Inject it
//        this.groupRepository = groupRepository;
//        this.userRepository = userRepository;
//        this.expenseRepository = expenseRepository;  // ✅ Store it
//    }
//
//    public Group createGroup(Group group) {
//        // Fetch createdBy from DB
//        User creator = userRepository.findById(group.getCreatedBy().getId())
//                .orElseThrow(() -> new RuntimeException("Creator not found"));
//
//        // Fetch all members from DB
//        Set<User> members = new HashSet<>();
//        for (User member : group.getMembers()) {
//            User user = userRepository.findById(member.getId())
//                    .orElseThrow(() -> new RuntimeException("Member not found"));
//            members.add(user);
//        }
//
//        group.setCreatedBy(creator);
//        group.setMembers(members);
//
//        return groupRepository.save(group);
//    }
//
//    public Optional<Group> getGroupById(Long id) {
//        return groupRepository.findById(id);
//    }
//
//    public List<Group> getAllGroups() {
//        return groupRepository.findAll();
//    }
//
//    public Group addMemberToGroup(Long groupId, Long userId) {
//        Group group = groupRepository.findById(groupId)
//                .orElseThrow(() -> new RuntimeException("Group not found"));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (group.getMembers().contains(user)) {
//            throw new RuntimeException("User already in group");
//        }
//
//        group.getMembers().add(user);
//        return groupRepository.save(group);
//    }
//
//    public Map<Long, Double> calculateBalances(Long groupId) {
//        // Fetch all expenses for this group
//        List<Expense> expenses = expenseRepository.findByGroup_Id(groupId);
//
//        // Store balances per user
//        Map<Long, Double> balances = new HashMap<>();
//
//        for (Expense expense : expenses) {
//            Long payerId = expense.getCreatedBy().getId();
//            double amount = expense.getTotalAmount().doubleValue();
//
//            // Initialize payer’s balance if not present
//            balances.putIfAbsent(payerId, 0.0);
//            balances.put(payerId, balances.get(payerId) + amount);
//
//            // Loop over participants (ExpenseShare list)
//            for (ExpenseShare share : expense.getShares()) {
//                Long participantId = share.getUser().getId();
//                double shareAmount = share.getAmount().doubleValue();
//
//                // Initialize participant’s balance if not present
//                balances.putIfAbsent(participantId, 0.0);
//                balances.put(participantId, balances.get(participantId) - shareAmount);
//            }
//        }
//
//        return balances;
//    }
//}
import com.splitwise.splitwise_backend.model.*;
import com.splitwise.splitwise_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final UserRepository userRepo;

    public Group createGroup(String name, Collection<Long> userIds) {
        Group group = groupRepo.save(new Group(null, name));
        if (userIds != null) {
            for (Long uid : userIds) {
                User u = userRepo.findById(uid)
                        .orElseThrow(() -> new RuntimeException("User not found: " + uid));
                memberRepo.save(new GroupMember(null, group, u));
            }
        }
        return group;
    }

    public void addMember(Long groupId, Long userId) {
        Group g = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        memberRepo.save(new GroupMember(null, g, u));
    }

    public List<Group> getAllGroups() {
        return groupRepo.findAll();
    }

    public List<User> getMembers(Long groupId) {
        List<GroupMember> gm = memberRepo.findByGroup_Id(groupId);
        List<User> users = new ArrayList<>();
        gm.forEach(m -> users.add(m.getUser()));
        return users;
    }

    // ✅ NEW: get group by ID
    public Group getGroupById(Long groupId) {
        return groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
    }
}
