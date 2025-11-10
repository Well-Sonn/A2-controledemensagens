package com.projetoa2.service;

import com.projetoa2.factory.MessageFactory;
import com.projetoa2.model.Message;
import com.projetoa2.model.User;
import com.projetoa2.observer.MessageObserver;
import com.projetoa2.repository.MessageRepository;
import com.projetoa2.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final List<MessageObserver> observers = new ArrayList<>();

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void registerObserver(MessageObserver o) { observers.add(o); }

    public void sendMessageToOne(int senderId, int receiverId, String content) {
        User receiver = userRepository.findById(receiverId);
        if (receiver == null) return;
        Message m = MessageFactory.create(senderId, receiverId, content);
        messageRepository.save(m);
        notifyObservers(m);
    }

    public void broadcast(int senderId, String content) {
        List<User> all = userRepository.findAll();
        for (User u : all) {
            if (u.getId() == senderId) continue;
            Message m = MessageFactory.create(senderId, u.getId(), content);
            messageRepository.save(m);
            notifyObservers(m);
        }
    }

    public List<Message> getMessagesForUser(int userId) {
        return messageRepository.findByReceiverId(userId);
    }

    public Message findById(int id) { return messageRepository.findById(id); }

    private void notifyObservers(Message m) {
        for (MessageObserver o : observers) o.onMessageSent(m);
    }
}
