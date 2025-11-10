package com.projetoa2.observer;

import com.projetoa2.model.Message;

public interface MessageObserver {
    void onMessageSent(Message m);
}
