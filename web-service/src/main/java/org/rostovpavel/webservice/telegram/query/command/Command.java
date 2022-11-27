package org.rostovpavel.webservice.telegram.query.command;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.rostovpavel.webservice.telegram.utils.StringUtil;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Command {

    MY_ID("/my_id", "Информация обо мне"),
    TICKER_LIST("/tickerlist", "Список тикеров"),
    STATISTIC("/statistic", "Статистика"),
    START("/start", "Включить бота"),
    STOP("/stop", "Отключить бота"),
    ;
    private final String name;
    private final String desc;

    public static Optional<Command> parseCommand(String command) {
        if (StringUtil.isBlank(command)) {
            return Optional.empty();
        }
        String formatName = StringUtil.trim(command).toLowerCase();
        return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(formatName)).findFirst();
    }
}
