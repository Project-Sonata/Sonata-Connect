package com.odeyalo.sonata.connect.exception;

import com.odeyalo.sonata.common.context.MalformedContextUriException;
import com.odeyalo.sonata.connect.dto.ExceptionMessage;
import com.odeyalo.sonata.connect.dto.ExceptionMessages;
import com.odeyalo.sonata.connect.dto.ReasonCodeAwareExceptionMessage;
import com.odeyalo.sonata.connect.exception.web.MissingRequestParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import static org.springframework.http.ResponseEntity.*;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    public static final String NO_ACTIVE_DEVICE_ERROR_DESCRIPTION = "Player command failed: No active device found";

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
        return badRequest().body(ReasonCodeAwareExceptionMessage.of(ex.getReasonCode(), NO_ACTIVE_DEVICE_ERROR_DESCRIPTION));
    }


    @ExceptionHandler(MultipleTargetDevicesNotSupportedException.class)
    public ResponseEntity<ReasonCodeAwareExceptionMessage> handleMultipleTargetDevicesNotSupportedException(MultipleTargetDevicesNotSupportedException ex) {
        return badRequest().body(ReasonCodeAwareExceptionMessage.of(ex.getReasonCode(), ex.getMessage()));
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ReasonCodeAwareExceptionMessage> handleDeviceNotFoundException(DeviceNotFoundException ex) {
        return unprocessableEntity().body(ReasonCodeAwareExceptionMessage.of(ex.getReasonCode(), ex.getMessage()));
    }

    @ExceptionHandler(TargetDeviceRequiredException.class)
    public ResponseEntity<ReasonCodeAwareExceptionMessage> handleTargetDeviceRequiredException(TargetDeviceRequiredException ex) {
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

    @ExceptionHandler(InvalidVolumeException.class)
    public ResponseEntity<?> handleInvalidVolumeException(final InvalidVolumeException ex) {
        return ResponseEntity.badRequest()
                .body(ReasonCodeAwareExceptionMessage.of("invalid_volume", ex.getMessage()));
    }

    @ExceptionHandler(MissingRequestParameterException.class)
    public ResponseEntity<?> handleMissingRequestParameterException(final MissingRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(ExceptionMessage.of(ex.getMessage()));
    }

    @ExceptionHandler(MissingPlayableItemException.class)
    public ResponseEntity<?> handleMissingPlayableItemException(final MissingPlayableItemException ex) {
        final var body = ReasonCodeAwareExceptionMessage.of(ex.getReasonCode(), "Player command error: no item is playing");

        return ResponseEntity.badRequest()
                .body(body);
    }

    @ExceptionHandler(InvalidSeekPositionException.class)
    public ResponseEntity<?> handleInvalidSeekPositionException(final InvalidSeekPositionException ex) {
        final var body = ReasonCodeAwareExceptionMessage.of(ex.getReasonCode(), "Player command error: position must be positive");

        return ResponseEntity.badRequest()
                .body(body);
    }
}
