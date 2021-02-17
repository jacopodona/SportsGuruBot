/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SportsGuruBot.Bot;

import com.SportsGuruBot.Bot.modules.Request;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.mongodb.client.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.Document;
import utilities.HttpRequest;

/**
 *
 * @author Jacopo
 */
public class SportsGuruBot extends TelegramLongPollingBot{
    
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Request> collection;
    private final String connectionString="mongodb+srv://admin:TMAT5Zgmy2uEPRWg@cluster0.h6e4w.mongodb.net/<dbname>?retryWrites=true&w=majority";
            
    public SportsGuruBot() {
        mongoClient=MongoClients.create(connectionString);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        database = mongoClient.getDatabase("SportsGuruDatabase").withCodecRegistry(pojoCodecRegistry);
        //database = database.withCodecRegistry(pojoCodecRegistry);
        collection= database.getCollection("UserRequestLog",Request.class);
        collection = collection.withCodecRegistry(pojoCodecRegistry);
        collection.drop();
        //collection.insertOne(new Request("123456789","1","","",0));
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
                //collection.removeByUser(chatId);
                collection.deleteOne(eq("chatId", chatId));
                Request r=new Request(chatId,"","","",0);
                //collection.add(r);
                collection.insertOne(r);
                message.setText("Inserisci il nome dell'atleta su cui vuoi conoscere una statistica");
            }
            else if(text.equals("/help")){
                System.out.println("Richiesta di aiuto");
                message.setText("Utilizza /research per effettuare una ricerca");
            }
            else if(text.equals("/ping")){
                System.out.println("Ping server");
                System.out.println(new HttpRequest("").run());
                message.setText("Ping effettuato");
            }
            else{
                //Request precedente=collection.getRequestByUser(chatId);
                Request precedente=collection.find(eq("chatId", chatId)).first();
                if(precedente==null){
                    message.setText("Comando non valido. Utilizza /research per effettuare una nuova ricerca oppure /help  per avere una lista dei comandi");
                }
                else{
                    //int index=collection.getIndex(precedente.getUserid());
                    if(precedente.getIterator()==0){//allora mi ha inviato il nome
                        String nome= update.getMessage().getText();
                        precedente.setNome(nome);
                        //collection.modify(index, precedente);
                        collection.updateOne(eq("chatId", chatId), set("iterator", precedente.getIterator()+1));
                        message.setText("Inserisci la statistica che ti interessa di "+nome);
                    }
                    else if(precedente.getIterator()==1){//allora mi ha inviato la statistica
                        String statistica= update.getMessage().getText();
                        precedente.setStatistica(statistica);
                        //collection.modify(index, precedente);
                        collection.updateOne(eq("chatId", chatId), set("iterator", precedente.getIterator()+1));
                        message.setText("Inserisci la data da cui conteggiare la statistica");
                    }
                    else if(precedente.getIterator()==2){//allora mi ha inviato la data
                        String data= update.getMessage().getText();
                        precedente.setData(data);
                        //collection.modify(index, precedente);
                        collection.updateOne(eq("chatId", chatId), set("iterator", precedente.getIterator()+1));
                        message.setText("Riepilogo: \n"+precedente.toString());
                        new HttpRequest(/*"ranking/teams/20182019"*/"").run();
                    }
                    else{
                        message.setText("Comando non valido. Utilizza /research per effettuare una nuova ricerca oppure /help  per avere una lista dei comandi");
                    }                    
                }
            }
            message.setChatId(chatId);
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                Logger.getLogger(SportsGuruBot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }

    @Override
    public String getBotUsername() {
        return "SportsGuruBot";
    }
    
}
