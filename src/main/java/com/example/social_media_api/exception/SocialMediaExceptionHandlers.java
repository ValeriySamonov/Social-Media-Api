package com.example.social_media_api.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class SocialMediaExceptionHandlers{

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Пользователь не существует: " + ex.getMessage());
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

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class})
    public ResponseEntity<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка аутентификации: " + ex.getMessage());
    }

    @ExceptionHandler(value = {SubscriptionDoesNotExistException.class})
    public ResponseEntity<String> handleSubscriptionDoesNotExist(SubscriptionDoesNotExistException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Подписка/друг не существует: " + ex.getMessage());
    }

}
