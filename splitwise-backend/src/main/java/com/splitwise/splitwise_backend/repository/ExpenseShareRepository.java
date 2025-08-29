package com.splitwise.splitwise_backend.repository;

import com.splitwise.splitwise_backend.model.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    List<ExpenseShare> findByExpense_Id(Long expenseId);
    List<ExpenseShare> findByExpense_Group_Id(Long groupId);
}
