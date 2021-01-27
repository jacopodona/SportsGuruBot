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
    
    private String userid, nome, statistica, data;

    public Request(String userid) {
        this.userid=userid;
        nome=null;
        statistica=null;
        data=null;
    }
    
    
    public void setUserId(String userid) {
        this.userid = userid;
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

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getStatistica() {
        return statistica;
    }

    public String getUserid() {
        return userid;
    }
    
    
    
    public String toString(){
        return "Utente: "+userid+"Atleta: "+nome+"\nStatistica: "+statistica+"\nData: "+data;
    }
    
}
    
