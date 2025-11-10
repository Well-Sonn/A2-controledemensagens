package com.projetoa2.observer;

import com.projetoa2.model.Mensagem;

public class NotificadorConsole implements ObservadorMensagem {
    @Override
    public void aoEnviarMensagem(Mensagem m) {
        System.out.println("[NOTIFICADOR] Mensagem enviada: id=" + m.getId() + " para user=" + m.getIdDestinatario());
    }
}
