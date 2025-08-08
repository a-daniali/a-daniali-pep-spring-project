package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SocialMediaController {

    // Spring will handle object lifetimes with autowired

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // 1: Our API should be able to process new User registrations.
    @PostMapping("/register")
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
        }

        if (accountService.getAccountByUsername(account.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        Account newAccount = accountService.registerAccount(account);
        return ResponseEntity.ok(newAccount);
    }

    // 2: Our API should be able to process User logins.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Optional<Account> existing = accountService.verifyLogin(account.getUsername(), account.getPassword());
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // 3: Our API should be able to process the creation of new messages.
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() ||
            message.getMessageText().length() > 255 ||
            !accountService.getAccountById(message.getPostedBy()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid message or user");
        }

        Message newMessage = messageService.createMessage(message);
        return ResponseEntity.ok(newMessage);
    }

    // 4: Our API should be able to retrieve all messages.
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    // 5: Our API should be able to retrieve a message by its ID.
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable int messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(message.orElse(null));
    }

    // 6: Our API should be able to delete a message identified by a message ID.
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable int messageId) {
        int rowsAffected = messageService.deleteMessageById(messageId);
        if (rowsAffected == 1) { // Can never be more than 1
            return ResponseEntity.ok(rowsAffected);
        } else {
            return ResponseEntity.ok().build(); 
        }
    }

    // 7: Our API should be able to update a message text identified by a message ID.
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessageText(@PathVariable int messageId, @RequestBody Message message) {
        String newText = message.getMessageText();
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid message text");
        }

        int rowsUpdated = messageService.updateMessageText(messageId, newText);
        if (rowsUpdated == 1) { // Can never be more than 1
            return ResponseEntity.ok(rowsUpdated);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not found or update failed");
        }
    }

    // 8. Our API should be able to retrieve all messages written by a particular user.
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccount(@PathVariable int accountId) {
        return ResponseEntity.ok(messageService.getMessagesByAccountId(accountId));
    }
}
