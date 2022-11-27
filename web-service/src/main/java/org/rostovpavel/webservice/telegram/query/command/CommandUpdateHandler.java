package org.rostovpavel.webservice.telegram.query.command;

import org.rostovpavel.webservice.telegram.query.UpdateHandler;
import org.rostovpavel.webservice.telegram.query.UpdateHandlerStage;
import org.rostovpavel.webservice.telegram.query.dto.ParseDTO;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static org.rostovpavel.webservice.telegram.query.command.Messages.getMessage;

@Component
public class CommandUpdateHandler implements UpdateHandler {

    private final CommandParser commandParser;
    private final CommandHandlerFactory commandHandlerFactory;

    public CommandUpdateHandler(CommandParser commandParser, CommandHandlerFactory commandHandlerFactory) {
        this.commandParser = commandParser;
        this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        Message message = getMessage(update);
        if (message == null) return false;
        String text = message.hasPhoto() ? message.getCaption() : message.getText();
        Optional<ParseDTO> command = commandParser.parseCommand(text);
        if (command.isEmpty()) {
            return false;
        }

        //fake stop
        boolean isStop = command.get().getCommand().getName().equals(Command.STOP.getName());
        Optional<Command> tempCommandStart = Command.parseCommand("/start");

        handleCommand(update,
                isStop ? tempCommandStart.get() : command.get().getCommand(),
                isStop ? "stop" : command.get().getText());
        return true;
    }

    private void handleCommand(Update update, Command command, String text) throws TelegramApiException {
        CommandHandler commandHandler = commandHandlerFactory.getHandler(command);
        commandHandler.handleCommand(update.getMessage(), text);
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.COMMAND;
    }
}
