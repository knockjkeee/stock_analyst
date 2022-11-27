package org.rostovpavel.webservice.telegram.query.command.handler;

import org.apache.commons.io.FileUtils;
import org.rostovpavel.webservice.TEMPO.GenerateFile;
import org.rostovpavel.webservice.telegram.StockBot;
import org.rostovpavel.webservice.telegram.query.command.Command;
import org.rostovpavel.webservice.telegram.query.command.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class StatisticCommandHandler implements CommandHandler {

    @Value("${telegram.idChat}")
    private String idChat;

    private final StockBot bot;

    public StatisticCommandHandler(StockBot bot) {
        this.bot = bot;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        List<String> nameD = List.of("All_stocks", "history", "superTrend");
        nameD.forEach(name -> {
            String resNamed = GenerateFile.generateCSV(name, bot, idChat);
            if (resNamed != null && resNamed.length() > 0) {
                try {
                    byte[] bytesFile = FileUtils.readFileToByteArray(new File(resNamed));
                    bot.execute(SendDocument.builder()
                            .chatId(idChat)
                            .document(new InputFile(new ByteArrayInputStream(bytesFile), resNamed))
                            .build());
                } catch (IOException | TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Command getCommand() {
        return Command.STATISTIC;
    }
}
