package org.rostovpavel.webservice.telegram.query.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CommandHandler {
    void handleCommand(Message message, String text) throws TelegramApiException;

    Command getCommand();
}
