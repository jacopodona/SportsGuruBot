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
import org.json.JSONArray;
import org.json.JSONObject;
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
                message.setText("Utilizza /research per effettuare una ricerca\n"
                        + "Utilizza /stats per ottenere la lista di statistiche disponibili per ogni sport");
            }
            else if(text.equals("/start")){
                System.out.println("Richiesta di avvio");
                message.setText("Benvenuto su SportsGuruBot!\n"
                        + "Con questo bot puoi cercare le statistiche dei tuoi atleti preferiti inserendo il loro nome, la statistica che ti interessa e la data da cui conteggiare la tua statistica\n\n"
                        + "Per iniziare una ricerca, utilizza il comando /research\n"
                        + "Per conoscere le statistiche disponibili per ogni sport, utilizza /stats"
                        + "Se ti trovi in difficoltà, utilizza il comando /help per rivedere la lista dei comandi disponibili");
            }
            else if(text.equals("/stats")){
                System.out.println("Richiesta di statistiche");
                message.setText("Ecco un elenco di statistiche disponibili per la ricerca \n"+statsList());
            }
            else if(text.equals("/ping")){
                System.out.println("Ping server");
                String request=new HttpRequest("player/yellows/Ronaldo%20C.&20182019").run();
                System.out.println(request);
                System.out.println(extractFromJSON(request, "yellows"));
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
                        if(nome.charAt(nome.length()-1)==' '){
                            nome=trimWhiteSpace(nome);
                        }
                        precedente.setNome(nome);
                        //collection.modify(index, precedente);
                        collection.updateOne(eq("chatId", chatId), set("nome", precedente.getNome()));
                        collection.updateOne(eq("chatId", chatId), set("iterator", precedente.getIterator()+1));
                        message.setText("Inserisci la statistica che ti interessa di "+nome+"\nPer sapere che statistiche sono disponibili per ogni sport utilizza /stats");
                    }
                    else if(precedente.getIterator()==1){//allora mi ha inviato la statistica
                        String statistica= update.getMessage().getText();
                        precedente.setStatistica(statistica);
                        //collection.modify(index, precedente);
                        collection.updateOne(eq("chatId", chatId), set("statistica", precedente.getStatistica()));
                        collection.updateOne(eq("chatId", chatId), set("iterator", precedente.getIterator()+1));
                        message.setText("Inserisci la data da cui conteggiare la statistica (formato gg/mm/aaaa)");
                    }
                    else if(precedente.getIterator()==2){//allora mi ha inviato la data
                        String data= update.getMessage().getText();
                        precedente.setData(data);
                        //collection.modify(index, precedente);
                        collection.updateOne(eq("chatId", chatId), set("data", precedente.getData()));
                        collection.updateOne(eq("chatId", chatId), set("iterator", precedente.getIterator()+1));
                        //message.setText("Riepilogo: \n"+precedente.toString());
                        System.out.println(precedente.toString());
                        String nomeURL=precedente.getNome().replace(" ", "%20");
                        String response=new HttpRequest("player/date/"+precedente.getStatistica()+"/"+nomeURL+"&"+precedente.getData().replace('/', '.')).run();
                        if(response==null){
                            message.setText("Qualcosa è andato storto nell'esecuzione della richiesta, controlla di aver inserito i parametri correttamente");
                        }
                        else{
                            int result=extractFromJSON(response,precedente.getStatistica());
                            System.out.println("Result: \n"+prepareMessage(precedente,result));
                            System.out.println("Valore Statisica: "+result);
                            System.out.println("Finito: "+result);
                            message.setText(prepareMessage(precedente,result));
                        }
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

    private int extractFromJSON(String response,String statistica) {
        JSONObject json=new JSONObject(response);
        /*JSONArray data=json.getJSONArray("data");
        System.out.println(data.toString());*/
        JSONObject result=json.getJSONObject("data");
        int value=result.getInt("value");
        return value;
    }

    private String prepareMessage(Request precedente, int result) {
        return "Dal "+precedente.getData()+" il giocatore "+precedente.getNome()+" ha effettuato "+result+" "+precedente.getStatistica();
    }

    private String statsList() {
        String calcio="Calcio: \ngoals - mostra i goal di un giocatore\n"
                + "yellows - mostra i cartellini gialli di un giocatore \n"
                + "reds - mostra i cartellini rossi di un giocatore\n"
                + "cards - mostra i cartellini totali di un giocatore\n";
        return calcio;
    }

    private String trimWhiteSpace(String nome) {//Assicurarsi che se ci sono spazi dopo il nome dell'atleta vengano rimossi per non generare errori
        while(nome.charAt(nome.length()-1)==' '){
            nome=nome.substring(0, nome.length()-2);
        }
        return nome;
    }
    
}
