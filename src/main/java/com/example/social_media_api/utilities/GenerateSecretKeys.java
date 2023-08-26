package com.example.social_media_api.utilities;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

// Генерация секретных ключей для создания токенов.
// Результат выводится в консоль. Для простоты не стал использовать логгеры.
public class GenerateSecretKeys {

    public static void main(String[] args) {
        System.out.println(generateSecretKey());
        System.out.println(generateSecretKey());
    }

    private static String generateSecretKey() {
        return Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
    }
}
