package com.splitwise.splitwise_backend.repository;


import com.splitwise.splitwise_backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find all expenses for a given group
    List<Expense> findByGroup_Id(Long groupId);
}