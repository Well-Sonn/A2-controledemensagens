package com.projetoa2.factory;

import com.projetoa2.model.Message;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageFactory {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public static Message create(int senderId, int receiverId, String content) {
        return new Message(COUNTER.getAndIncrement(), senderId, receiverId, content, System.currentTimeMillis());
    }
}
