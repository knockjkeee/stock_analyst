package org.rostovpavel.webservice.exception;


import io.grpc.StatusRuntimeException;
import lombok.SneakyThrows;
import org.rostovpavel.webservice.telegram.StockBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.rostovpavel.webservice.telegram.utils.Messages.sendExceptionToBot;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    private final StockBot bot;

    @Value("${telegram.idChat}")
    private String idChat;

    public ExceptionController(StockBot bot) {
        this.bot = bot;
    }

    @SneakyThrows
    @ExceptionHandler({StockNotFoundException.class})
    public ResponseEntity<Error> handleNotFound(Exception ex){
        sendExceptionToBot(ex, bot, idChat);
        return new ResponseEntity<>(new Error(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }

    @SneakyThrows
    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<Error> handleNoContentByLimit(Exception ex){
        sendExceptionToBot(ex, bot, idChat);
        return new ResponseEntity<>(new Error(ex.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
//        return new ResponseEntity<>(new Error(ex.getLocalizedMessage()), HttpStatus.NO_CONTENT);
    }


}
