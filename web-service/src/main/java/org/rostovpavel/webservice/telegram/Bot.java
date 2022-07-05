package org.rostovpavel.webservice.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot{

    @Override
    public String getBotUsername() {
        return "TestBotApi";
    }

    @Override
    public String getBotToken() {
        return System.getenv("bot");
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        //notificationSubscribers(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }


}
