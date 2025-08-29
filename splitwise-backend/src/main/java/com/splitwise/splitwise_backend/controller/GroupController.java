package com.splitwise.splitwise_backend.controller;

//import com.splitwise.splitwise_backend.model.Group;
//import com.splitwise.splitwise_backend.service.GroupService;
//import com.splitwise.splitwise_backend.service.SettlementService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//@CrossOrigin(origins = "http://localhost:3000")
//@RestController
//@RequestMapping("/api/groups")
//public class GroupController {
//
//    private final GroupService groupService;
//    private final SettlementService settlementService;
//
//
//    public GroupController(GroupService groupService, SettlementService settlementService) {
//        this.groupService = groupService;
//        this.settlementService = settlementService;
//    }
//    // 1. Create a group
//    @PostMapping
//    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
//        Group savedGroup = groupService.createGroup(group);
//        return ResponseEntity.ok(savedGroup);
//    }
//
//    // 2. Get group by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
//        return groupService.getGroupById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // 3. Add a member to a group
//    @PostMapping("/{groupId}/members/{userId}")
//    public ResponseEntity<Group> addMemberToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
//        Group updatedGroup = groupService.addMemberToGroup(groupId, userId);
//        return ResponseEntity.ok(updatedGroup);
//    }
//
//    // 4. List all groups
//    @GetMapping
//    public ResponseEntity<List<Group>> getAllGroups() {
//        return ResponseEntity.ok(groupService.getAllGroups());
//    }
//
//    @GetMapping("/{groupId}/balances")
//    public Map<Long, Double> getGroupBalances(@PathVariable Long groupId) {
//        return groupService.calculateBalances(groupId);
//    }
//}
import com.splitwise.splitwise_backend.dto.AddMemberRequest;
import com.splitwise.splitwise_backend.dto.CreateGroupRequest;
import com.splitwise.splitwise_backend.model.Group;
import com.splitwise.splitwise_backend.model.User;
import com.splitwise.splitwise_backend.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> create(@RequestBody CreateGroupRequest req) {
        return ResponseEntity.ok(
                groupService.createGroup(req.getName(), req.getMemberIds())
        );
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long groupId, @RequestBody AddMemberRequest req) {
        groupService.addMember(groupId, req.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Group>> list() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    // ✅ NEW ENDPOINT: fetch group details by ID
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<User>> members(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getMembers(groupId));
    }
}
