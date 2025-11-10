package com.projetoa2.repository;

import com.projetoa2.model.Message;

import java.util.List;

public interface MessageRepository extends Repository<Message> {
    List<Message> findByReceiverId(int receiverId);
}
