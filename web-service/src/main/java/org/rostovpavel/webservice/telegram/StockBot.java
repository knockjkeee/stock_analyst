package org.rostovpavel.webservice.telegram;

import lombok.SneakyThrows;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.webservice.services.TickerDataService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.rostovpavel.base.utils.Stock.getNameTickers;
import static org.rostovpavel.webservice.telegram.utils.Messages.getFullInformationByTicker;

@Component
public class StockBot extends TelegramLongPollingBot{

    private final TickerDataService tickerDataService;

    public StockBot(TickerDataService tickerDataService) {
        this.tickerDataService = tickerDataService;
    }

    @Override
    public String getBotUsername() {
        return "TestBotApiTink";
    }

    @Override
    public String getBotToken() {
        return System.getenv("bot");
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage()
                .getText();
        if (text.equals("tickerlist")) {
            execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text("<pre>CURRENT SIZE - " + getNameTickers().length + "\n" + Arrays.toString(getNameTickers()) +"</pre>")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build());
        } else if(text.length() <= 4){
            String query = update.getMessage()
                    .getText()
                    .toUpperCase(Locale.ROOT);
            Ticker ticket = tickerDataService.getDataByTicker(query);
            execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text(getFullInformationByTicker(ticket))
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build());
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }


}
