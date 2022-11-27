package org.rostovpavel.webservice.telegram.query.command.handler;

import lombok.SneakyThrows;
import org.rostovpavel.webservice.TEMPO.MyTask;
import org.rostovpavel.webservice.telegram.StockBot;
import org.rostovpavel.webservice.telegram.query.command.Command;
import org.rostovpavel.webservice.telegram.query.command.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class StartCommandHandler implements CommandHandler {

    @Value("${telegram.idChat}")
    private String idChat;

    private final StockBot bot;

    private final ScheduledExecutorService executor;

    private ScheduledFuture<?> schedule;

    public StartCommandHandler(StockBot bot, ScheduledExecutorService executor) {
        this.bot = bot;
        this.executor = executor;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        if (text.equals("stop")){
            stop();
            cancel();
        }else {
            bot.execute(SendMessage.builder()
                    .chatId(idChat)
                    .text("<pre>Поиск подходящего временного интервала...</pre>")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyToMessageId(message.getMessageId())
                    .build());
            run();
            init();
        }
    }

    @Override
    public Command getCommand() {
        return Command.START;
    }

    private void run() {
        List<String> mmStart = Arrays.asList("14", "29", "44", "59");
        List<String> ssStart = Arrays.asList("50", "51",  "52",  "53",  "54", "55",  "56", "57", "58", "59");
        MyTask testMytest = new MyTask("testMytest");

        while (true) {
            String currentMM = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
            String currentSS = new SimpleDateFormat("ss").format(Calendar.getInstance().getTime());
            if (mmStart.contains(currentMM) && ssStart.contains(currentSS)) {
                System.out.println("Start -> " + new SimpleDateFormat("HH.mm.ss").format(Calendar.getInstance().getTime()));
                schedule = executor.scheduleAtFixedRate(testMytest, 0, 15, TimeUnit.MINUTES);
                break;
            }
            try {
                System.out.println("Wait -> " + new SimpleDateFormat("HH.mm.ss").format(Calendar.getInstance().getTime()));
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stop() {
        schedule.cancel(true);
    }

    @SneakyThrows
    public void init() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>❗️❗️❗️ Хомякам приготовиться, КУКАНИЩЕ от " + currentDate + ", СТАРТУЕТ!</pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
    }

    @SneakyThrows
    public void cancel() {
        bot.execute(SendMessage.builder()
                .chatId(idChat)
                .text("<pre>❗️❗️❗️ Стоп машина, на сегодня все... полна банка огурцов </pre>")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build());
    }
}
