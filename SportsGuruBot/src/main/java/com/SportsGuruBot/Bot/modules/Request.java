/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SportsGuruBot.Bot.modules;

/**
 *
 * @author Jacopo
 */
public class Request {
    
    private String chatId, nome, statistica, data;
    private int iterator;

    public Request() {
    }

    /*public Request(String userid) {
        this.userid=userid;
        nome=null;
        statistica=null;
        data=null;
        iterator=0;
    }*/

    public Request(String chatId, String nome, String statistica, String data, int iterator) {
        this.chatId = chatId;
        this.nome = nome;
        this.statistica = statistica;
        this.data = data;
        this.iterator = iterator;
    }

    public String getChatId() {
        return chatId;
    }
    
    public String getNome() {
        return nome;
    }

    public String getStatistica() {
        return statistica;
    }
    
    public int getIterator() {
            return iterator;
    }
    
    public String getData() {
        return data;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public void setData(String data) {
        this.data = data;
    }

    public void setStatistica(String statistica) {
        this.statistica = statistica;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }
    
    public String toString(){
        return "Utente: "+chatId+"\nAtleta: "+nome+"\nStatistica: "+statistica+"\nData: "+data+"\nIterator: "+iterator;
    }
    
}
    
