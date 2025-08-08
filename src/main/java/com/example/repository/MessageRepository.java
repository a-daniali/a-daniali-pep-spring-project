package com.example.repository;

import com.example.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    // Spring will automatically generate the sql statements needed.
    
    List<Message> findByPostedBy(int accountId);
}
