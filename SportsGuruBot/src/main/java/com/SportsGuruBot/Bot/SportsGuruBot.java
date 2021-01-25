/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.SportsGuruBot.Bot;

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

    @Override
    public String getBotToken() {
        return "1597748075:AAGclJizyoHHOie97Qb85xES12JL3XvyLoM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message=new SendMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch(update.getMessage().getText()){
                case "/research":
                    message.setText("You selected to make a new research");
                    break;
                case "/clear":
                    message.setText("You selected to cancel your current research");
                    break;
                default:
                    message.setText("Unknown command, type /help for a list of available commands");
                    break;
            }
            message.setChatId(update.getMessage().getChatId().toString());
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
