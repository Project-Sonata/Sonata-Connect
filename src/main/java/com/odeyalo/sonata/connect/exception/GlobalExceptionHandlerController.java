package com.odeyalo.sonata.connect.exception;

import com.odeyalo.sonata.common.context.MalformedContextUriException;
import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.ExceptionMessages;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionMessages> handleWebExchangeBindException(WebExchangeBindException ex) {
        ExceptionMessages messages = ExceptionMessages.empty();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            ExceptionMessage message = ExceptionMessage.of(error.getDefaultMessage());
            messages.addMessage(message);
        }

        return badRequest().body(messages);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionMessage> handleIllegalStateException(IllegalStateException ex) {
        return badRequest().body(ExceptionMessage.of(ex.getMessage()));
    }

    @ExceptionHandler(NoActiveDeviceException.class)
    public ResponseEntity<ReasonCodeAwareExceptionMessage> handleNoActiveDeviceException(NoActiveDeviceException ex) {
        return badRequest().body(ReasonCodeAwareExceptionMessage.of(ex.getReasonCode(), ex.getMessage()));
    }

    @ExceptionHandler(MalformedContextUriException.class)
    public ResponseEntity<?> handleMalformedContextUriException(MalformedContextUriException ex) {
        if (ex instanceof ReasonCodeAware) {
            ReasonCodeAwareExceptionMessage message = ReasonCodeAwareExceptionMessage.of(((ReasonCodeAware) ex).getReasonCode(), ex.getMessage());
            return badRequest().body(message);
        }
        ExceptionMessage message = ExceptionMessage.of(ex.getMessage());
        return badRequest().body(message);
    }
}
