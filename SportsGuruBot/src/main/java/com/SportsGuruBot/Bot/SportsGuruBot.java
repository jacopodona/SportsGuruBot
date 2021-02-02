/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SportsGuruBot.Bot;

import com.SportsGuruBot.Bot.modules.Request;
import com.SportsGuruBot.Bot.modules.RequestCollection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author Jacopo
 */
public class SportsGuruBot extends TelegramLongPollingBot{
    
    private RequestCollection collection;

    public SportsGuruBot() {
        collection=new RequestCollection();
    }
    
    
    @Override
    public String getBotToken() {
        return "1597748075:AAGclJizyoHHOie97Qb85xES12JL3XvyLoM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message=new SendMessage();
        String text;
        String chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            text=update.getMessage().getText();
            System.out.println("Testo Ricevuto: "+text);
            chatId=update.getMessage().getChatId().toString();
            if(text.equals("/research")){
                System.out.println("Genero nuova richiesta di ricerca");
                message.setText("Inserisci il nome dell'atleta su cui vuoi conoscere una statistica");
                collection.removeByUser(chatId);
                Request r=new Request(chatId);
                collection.add(r);
                //message.setText("Inserisci il nome dell'atleta su cui vuoi conoscere una statistica");
            }
            else if(text.equals("/help")){
                System.out.println("Richiesta di aiuto");
                message.setText("Utilizza /research per effettuare una ricerca");
            }
            else{
                Request precedente=collection.getRequestByUser(chatId);
                if(precedente==null){
                    message.setText("Comando non valido. Utilizza /research per effettuare una nuova ricerca oppure /help  per avere una lista dei comandi");
                }
                else{
                    int index=collection.getIndex(precedente.getUserid());
                    if(precedente.getNome()==null){//allora mi ha inviato il nome
                        String nome= update.getMessage().getText();
                        precedente.setNome(nome);
                        collection.modify(index, precedente);
                        message.setText("Inserisci la statistica che ti interessa di "+nome);
                    }
                    else if(precedente.getStatistica()==null){//allora mi ha inviato la statistica
                        String statistica= update.getMessage().getText();
                        precedente.setStatistica(statistica);
                        collection.modify(index, precedente);
                        message.setText("Inserisci la data da cui conteggiare la statistica");
                    }
                    else if(precedente.getData()==null){//allora mi ha inviato la data
                        String data= update.getMessage().getText();
                        precedente.setData(data);
                        collection.modify(index, precedente);
                        message.setText("Riepilogo: \n"+precedente.toString());
                    }
                }
            }
            message.setChatId(chatId);
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                Logger.getLogger(SportsGuruBot.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.toString());
            }
        }   
    }

    @Override
    public String getBotUsername() {
        return "SportsGuruBot";
    }
    
}
