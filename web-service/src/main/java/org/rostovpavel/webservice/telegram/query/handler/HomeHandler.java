package org.rostovpavel.webservice.telegram.query.handler;

import org.rostovpavel.base.models.Ticker;
import org.rostovpavel.base.repo.TickerRepo;
import org.rostovpavel.webservice.telegram.StockBot;
import org.rostovpavel.webservice.telegram.query.SerializableInlineType;
import org.rostovpavel.webservice.telegram.query.dto.HomeDTO;
import org.rostovpavel.webservice.telegram.query.dto.IndicatorDTO;
import org.rostovpavel.webservice.telegram.utils.StringUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.rostovpavel.webservice.telegram.query.command.Messages.getNamedByTicket;

@Component
public class HomeHandler extends CallbackUpdateHandler<HomeDTO> {

    private final StockBot bot;
    private final TickerRepo repo;

    public HomeHandler(StockBot bot, TickerRepo repo) {
        this.bot = bot;
        this.repo = repo;
    }

    @Override
    protected Class<HomeDTO> getDtoType() {
        return HomeDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.HOME;
    }

    @Override
    protected void handleCallback(Update update, HomeDTO dto) throws TelegramApiException {

        Optional<Ticker> ticket = repo.findById(dto.getTickerId());

        if (ticket.isPresent()) {
            String text = getNamedByTicket(ticket.get());
            bot.execute(EditMessageText.builder()
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .text(text)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .build());

            List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
            buttons.add(List.of(InlineKeyboardButton.builder()
                    .text("Индикаторы")
                    .callbackData(StringUtil.serialize(new IndicatorDTO(dto.getTickerId())))
                    .build()));

            bot.execute(EditMessageReplyMarkup.builder()
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                    .build());
        }
    }
}
