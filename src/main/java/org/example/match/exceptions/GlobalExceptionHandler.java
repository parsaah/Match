package org.example.match.exceptions;

import org.example.match.user.exception.EmailAlreadyExistsException;
import org.example.match.user.exception.UserNotFoundException;
import org.example.match.user.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<String> handleUserNotFound(
//            UserNotFoundException ex) {
//
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(ex.getMessage());
//    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

//    @ExceptionHandler(UsernameAlreadyExistsException.class)
//    public ResponseEntity<String> handleUsernameAlreadyExists(
//            UsernameAlreadyExistsException ex) {
//
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(ex.getMessage());
//    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(
            UsernameAlreadyExistsException ex) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

//    @ExceptionHandler(EmailAlreadyExistsException.class)
//    public ResponseEntity<String> handleEmailAlreadyExists(
//            EmailAlreadyExistsException ex) {
//
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(ex.getMessage());
//    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}