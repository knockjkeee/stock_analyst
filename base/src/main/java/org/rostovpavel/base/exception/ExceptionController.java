package org.rostovpavel.base.exception;


import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler({StockNotFoundException.class})
    public ResponseEntity<Error> handleNotFound(Exception ex){
        return new ResponseEntity<>(new Error(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<Error> handleStatusRuntimeExc(Exception ex){
        return new ResponseEntity<>(new Error(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }
}
