package org.rostovpavel.webservice.telegram.query.command.handler;

import org.rostovpavel.webservice.telegram.StockBot;
import org.rostovpavel.webservice.telegram.query.command.Command;
import org.rostovpavel.webservice.telegram.query.command.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyIDCommandHandler implements CommandHandler {

    @Value("${telegram.idChat}")
    private String idChat;

    private final StockBot bot;

    public MyIDCommandHandler(StockBot bot) {
        this.bot = bot;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {

        //String chatId = message.getChatId().toString();
        String txt = "<pre>Ваш id : " + message.getFrom().getId() + "</pre>";
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .parseMode("html")
                .text(txt)
                .build());
    }

    @Override
    public Command getCommand() {
        return Command.MY_ID;
    }
}
