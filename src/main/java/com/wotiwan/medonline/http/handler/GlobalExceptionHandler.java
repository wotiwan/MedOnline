package com.wotiwan.medonline.http.handler;

import com.wotiwan.medonline.dto.ResponseMessage;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class) // Обрабатывает все необработанные исключения
    public ResponseEntity<ResponseMessage<String>> handleGenericException(
            Exception e
    ) {
        log.error("Handle exception: ", e);

        ResponseMessage<String> response = new ResponseMessage<>(
                "Internal Server Error",
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseMessage<String>> handleEntityNotFoundException(
            EntityNotFoundException e
    ) {
        log.error("Handle entityNotFoundException: ", e);

        var response = new ResponseMessage<>(
                "Not Found!",
                e.getMessage()
        );

        System.out.println(response);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }


    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ResponseMessage<String>> handleBadRequest(
            Exception e
    ) {
        log.error("Handle handleBadRequest: ", e);

        var response = new ResponseMessage<>(
                "Bad request!",
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseMessage<String>> handleForbidden(
            AuthorizationDeniedException e
    ) {

        log.error("Handle handleForbidden: ", e);

        var response = new ResponseMessage<>(
                "Forbidden!",
                e.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

}
