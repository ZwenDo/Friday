package com.notkamui.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public final class SHA512Hasher {
    private final MessageDigest md;

    public SHA512Hasher() throws NoSuchAlgorithmException {
        var is = getClass().getClassLoader().getResourceAsStream("salt.txt");
        char[] salt = new char[16];
        if (is == null) {
            System.out.println("No salt found ; no salt will be added");
        } else {
            try (var isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                isr.read(salt);
            } catch (IOException e) {
                System.out.println("No salt found ; no salt will be added");
            }
        }
        md = MessageDigest.getInstance("SHA-512");
        md.update(new String(salt).getBytes(StandardCharsets.UTF_8));
    }

    public String hash(String word) {
        Objects.requireNonNull(word);
        var hashed = md.digest(word.getBytes(StandardCharsets.UTF_8));
        return new String(hashed);
    }
}
