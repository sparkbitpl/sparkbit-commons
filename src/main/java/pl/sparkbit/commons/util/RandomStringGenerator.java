package pl.sparkbit.commons.util;

import java.security.SecureRandom;

@SuppressWarnings({"unused", "WeakerAccess", "checkstyle:hideutilityclassconstructor"})
public class RandomStringGenerator {

    public static final String DIGITS = "01234567890";
    public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String BASE_58_CHARACTERS = "23456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static final SecureRandom RND = new SecureRandom();

    public static String randomString(int len, String allowedCharacters) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(allowedCharacters.charAt(RND.nextInt(allowedCharacters.length())));
        }
        return sb.toString();
    }

    public static String digitsString(int len) {
        return randomString(len, DIGITS);
    }

    public static String uppercaseLetterString(int len) {
        return randomString(len, UPPERCASE_LETTERS);
    }

    public static String lowercaseLetterString(int len) {
        return randomString(len, LOWERCASE_LETTERS);
    }

    public static String base58String(int len) {
        return randomString(len, BASE_58_CHARACTERS);
    }
}
