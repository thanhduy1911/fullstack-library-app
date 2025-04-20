package com.duyhvt.spring_boot_library.controller;

import com.duyhvt.spring_boot_library.entity.Message;
import com.duyhvt.spring_boot_library.request_models.AdminQuestionRequest;
import com.duyhvt.spring_boot_library.service.MessagesService;
import com.duyhvt.spring_boot_library.utils.ExtractJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private MessagesService messagesService;

    @Autowired
    public MessageController(MessagesService messagesService) {

        this.messagesService = messagesService;
    }

    @PostMapping("/secure/add/message")
    public void postMessage(@RequestHeader(value = "Authorization") String token,
                            @RequestBody Message message) {

        String userEmail = ExtractJWT.payloadJWTExtraction(token, "sub");
        messagesService.postMessage(message, userEmail);
    }

    @PutMapping("/secure/admin/message")
    public void putMessage(@RequestHeader(value = "Authorization") String token,
                           @RequestBody AdminQuestionRequest adminQuestionRequest) throws Exception {

        String userEmail = ExtractJWT.payloadJWTExtraction(token, "sub");
        String admin = ExtractJWT.payloadJWTExtraction(token, "userType");
        if (admin == null || !admin.equals("admin")) {
            throw new Exception("Administrator page only");
        }

        messagesService.putMessage(adminQuestionRequest, userEmail);
    }
}
