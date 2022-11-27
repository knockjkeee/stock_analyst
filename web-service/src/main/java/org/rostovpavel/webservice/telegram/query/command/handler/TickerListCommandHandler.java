package org.rostovpavel.webservice.telegram.query.command.handler;

import org.rostovpavel.webservice.telegram.StockBot;
import org.rostovpavel.webservice.telegram.query.command.Command;
import org.rostovpavel.webservice.telegram.query.command.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

import static org.rostovpavel.base.utils.Stock.getNameTickers;

@Component
public class TickerListCommandHandler implements CommandHandler {

    @Value("${telegram.idChat}")
    private String idChat;

    private final StockBot bot;

    public TickerListCommandHandler(StockBot bot) {
        this.bot = bot;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>CURRENT SIZE - " + getNameTickers().length + "\n" + Arrays.toString(getNameTickers()) +"</pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyToMessageId(message.getMessageId())
                .build());
    }

    @Override
    public Command getCommand() {
        return Command.TICKER_LIST;
    }
}
