package com.duyhvt.spring_boot_library.service;

import com.duyhvt.spring_boot_library.dao.MessageRepository;
import com.duyhvt.spring_boot_library.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessagesService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessagesService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Request message
    public void postMessage(Message messageRequest, String userEmail) {
        Message message = new Message(messageRequest.getTitle(), messageRequest.getQuestion());
        message.setUserEmail(userEmail);

        messageRepository.save(message);
    }
}
