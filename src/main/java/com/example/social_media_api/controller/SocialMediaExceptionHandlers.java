package com.example.social_media_api.controller;

import com.example.social_media_api.exception.UserAlreadyExistsException;
import com.example.social_media_api.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SocialMediaExceptionHandlers {

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Пользователь не существует: " + ex.getMessage());
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Подписка/друг не существует: " + ex.getMessage());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Вы не можете подписаться на себя: " + ex.getMessage());
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Пользователь с таким именем уже существует: " + ex.getMessage());
    }

}
