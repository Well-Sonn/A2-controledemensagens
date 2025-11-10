package com.projetoa2.observer;

import com.projetoa2.model.Message;

public class ConsoleNotifier implements MessageObserver {
    @Override
    public void onMessageSent(Message m) {
        System.out.println("[NOTIFIER] Mensagem enviada: id=" + m.getId() + " para user=" + m.getReceiverId());
    }
}
