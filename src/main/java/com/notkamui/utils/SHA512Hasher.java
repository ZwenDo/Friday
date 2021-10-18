package com.notkamui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Objects.requireNonNull;

public final class SHA512Hasher {
    private final MessageDigest md;
    private final byte[] salt;

    private SHA512Hasher(MessageDigest md, byte[] salt) {
        requireNonNull(md);
        requireNonNull(salt);
        this.md = md;
        this.salt = salt;
    }

    public static SHA512Hasher getHasher() throws NoSuchAlgorithmException {
        Logger logger = LoggerFactory.getLogger(SHA512Hasher.class);
        byte[] salt = new byte[16];
        var path = Path.of("resources", "salt.txt");
        try {
            salt = Files.readString(path).substring(0, 16).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn("No salt found ; no salt will be added");
        }
        var md = MessageDigest.getInstance("SHA-512");
        return new SHA512Hasher(md, salt);
    }

    public String hash(String word) {
        requireNonNull(word);
        md.update(salt);
        var hashed = md.digest(word.getBytes(StandardCharsets.UTF_8));
        return new String(hashed);
    }
}
