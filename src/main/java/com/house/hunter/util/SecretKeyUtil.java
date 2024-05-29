package com.house.hunter.util;

import io.github.cdimascio.dotenv.Dotenv;

public class SecretKeyUtil {
    private static final String SECRET_KEY_ENV_NAME = "JWT_SECRET_KEY";

    public static String readEncryptedSecretFromEnv() {
        Dotenv dotenv = Dotenv.configure().load();
        return dotenv.get(SECRET_KEY_ENV_NAME);
    }

}
