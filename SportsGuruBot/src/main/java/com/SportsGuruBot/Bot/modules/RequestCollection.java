/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SportsGuruBot.Bot.modules;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jacopo
 */
public class RequestCollection {
    
    private ArrayList<Request> collection;

    public RequestCollection() {
        collection=new ArrayList<>();
    }
    
    public void add(Request r){
        collection.add(r);
    }
    
    public Request getRequestByUser(String userid){
        Request request=null;
        for(Request r:collection){
            if(r.getUserid().equals(userid)){
                request=r;
            }
        }
        return request;
    }
    
    public int getIndex(String chatId){
        int result=-1;
        for(int i=0;i<collection.size();i++){
            if(collection.get(i).getUserid().equals(chatId)){
                result=i;
            }
        }
        return result;
    }
    
    public void modify(int index, Request request){
        collection.set(index, request);
    }
    
    public void removeByUser(String userId){
        for(Request r:collection){
            if(r.getUserid().equals(userId)){
                collection.remove(r);
                System.out.println("Richiesta dell'utente "+userId+" rimossa");
            }
        }
    }
    
    
}
