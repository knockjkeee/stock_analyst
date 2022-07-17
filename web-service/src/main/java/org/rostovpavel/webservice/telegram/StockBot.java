package org.rostovpavel.webservice.telegram;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.webservice.TEMPO.GenerateFile;
import org.rostovpavel.webservice.services.TickerDataService;
import org.rostovpavel.webservice.telegram.query.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.rostovpavel.base.utils.Stock.getNameTickers;
import static org.rostovpavel.webservice.telegram.utils.Messages.getFullInformationByTicker;

@Component
public class StockBot extends TelegramLongPollingBot{

    @Value("${telegram.idChat}")
    private String idChat;

    private final TickerDataService tickerDataService;

    @Autowired
    private List<UpdateHandler> updateHandlers;

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



        if (update.hasCallbackQuery()) {
            for (UpdateHandler updateHandler : updateHandlers) {
                try {
                    if (updateHandler.handleUpdate(update)) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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
        } else if (text.equals("docsitem")){
            List<String> nameD = List.of("All_stocks", "history", "superTrend");
            nameD.forEach(name -> {
                String resNamed = GenerateFile.generateCSV(name, this, idChat);
                if (resNamed != null && resNamed.length() > 0) {
                    try {
                        byte[] bytesFile = FileUtils.readFileToByteArray(new File(resNamed));
                        execute(SendDocument.builder()
                                .chatId(idChat)
                                .document(new InputFile(new ByteArrayInputStream(bytesFile), resNamed))
                                .build());
                    } catch (IOException | TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            });

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

    @SneakyThrows
    @PostConstruct
    public void init() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>❗️❗️❗️ Хомякам приготовиться, КУКАНИЩЕ от " + currentDate + ", СТАРТУЕТ!</pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
    }
}
