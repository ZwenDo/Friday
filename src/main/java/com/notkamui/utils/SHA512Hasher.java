package com.notkamui.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public final class SHA512Hasher {
    private final SecureRandom random = new SecureRandom();
    private final byte[] salt = new byte[16];
    private final MessageDigest md;

    public SHA512Hasher() throws NoSuchAlgorithmException {
        random.nextBytes(salt);
        md = MessageDigest.getInstance("SHA-512");
    }

    public String hash(String word) {
        Objects.requireNonNull(word);
        var hashed = md.digest(word.getBytes(StandardCharsets.UTF_8));
        return new String(hashed);
    }
}
