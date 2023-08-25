package com.odeyalo.sonata.connect.exception;

import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.ExceptionMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionMessages> handleWebExchangeBindException(WebExchangeBindException ex) {
        ExceptionMessages messages = ExceptionMessages.empty();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            ExceptionMessage message = ExceptionMessage.of(error.getDefaultMessage());
            messages.addMessage(message);
        }

        return ResponseEntity.badRequest().body(messages);
    }
}
