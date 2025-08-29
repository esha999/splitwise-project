package com.splitwise.splitwise_backend.repository;

//import com.splitwise.splitwise_backend.model.SettlementRecord;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface SettlementRepository extends JpaRepository<SettlementRecord, Long> {
//    List<SettlementRecord> findByGroup_Id(Long groupId);
//}

import com.splitwise.splitwise_backend.model.SettlementRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SettlementRepository extends JpaRepository<SettlementRecord, Long> {
    List<SettlementRecord> findByGroup_Id(Long groupId);
    List<SettlementRecord> findByFromUser_IdAndToUser_Id(Long fromId, Long toId);
}