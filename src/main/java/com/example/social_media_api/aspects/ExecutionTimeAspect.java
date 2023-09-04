package com.example.social_media_api.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);
    private long startTime;

    @Before("execution(* com.example.social_media_api.service.auth.AuthService.*(..)) || " +
            "execution(* com.example.social_media_api.service.message.MessageService.*(..)) || " +
            "execution(* com.example.social_media_api.service.social.SocialMediaService.*(..)) || " +
            "execution(* com.example.social_media_api.service.user.UserService.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        logger.info("Запустился метод: " + joinPoint.getSignature().toShortString());
        startTime = System.currentTimeMillis();
    }

    @AfterReturning("execution(* com.example.social_media_api.service.auth.AuthService.*(..)) || " +
            "execution(* com.example.social_media_api.service.message.MessageService.*(..)) || " +
            "execution(* com.example.social_media_api.service.social.SocialMediaService.*(..)) || " +
            "execution(* com.example.social_media_api.service.user.UserService.*(..))")
    public void logMethodExit(JoinPoint joinPoint) {
        logger.info("Выполнился метод: " + joinPoint.getSignature().toShortString());
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        logger.info(
                "{} время выполнения {}ms",
                joinPoint.getSignature().toShortString(),
                executionTime
        );
    }
}
