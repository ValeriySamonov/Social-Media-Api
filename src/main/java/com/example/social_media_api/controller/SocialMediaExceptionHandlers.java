package com.example.social_media_api.controller;

import com.example.social_media_api.exception.SomethingWentWrongException;
import com.example.social_media_api.exception.UserAlreadyExistsException;
import com.example.social_media_api.exception.UserNotFoundException;
import com.example.social_media_api.exception.UsersAreNotFriendsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                .body("Вы не можете подписаться на себя/писать себе: " + ex.getMessage());
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Пользователь с таким именем уже существует: " + ex.getMessage());
    }

    @ExceptionHandler(value = {UsersAreNotFriendsException.class})
    public ResponseEntity<String> handleUsersAreNotFriendsException(UsersAreNotFriendsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Пользователь не являются вашим другом: " + ex.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Данные вводятся в неверном формате: " + ex.getMessage());
    }

    @ExceptionHandler(value = {SomethingWentWrongException.class})
    public ResponseEntity<String> handleSomethingWentWrongException(SomethingWentWrongException ex) {
        return ResponseEntity
                .status(HttpStatus.IM_USED)
                .body("Что-то пошло не так: " + ex.getMessage());
    }

}
