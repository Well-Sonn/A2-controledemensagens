package com.projetoa2.factory;

import com.projetoa2.model.Mensagem;

import java.util.concurrent.atomic.AtomicInteger;

public class FabricaMensagem {
    private static final AtomicInteger CONTADOR = new AtomicInteger(1);

    public static Mensagem criar(int idRemetente, int idDestinatario, String conteudo) {
        Mensagem m = new Mensagem(CONTADOR.getAndIncrement(), idRemetente, idDestinatario, conteudo, System.currentTimeMillis());
        m.setLida(false);
        return m;
    }
}
