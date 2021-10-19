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

    private static final Path saltPath = Path.of("resources", "salt.txt");
    private static final Logger logger = LoggerFactory.getLogger(SHA512Hasher.class);

    private final MessageDigest md;
    private final byte[] salt;

    private SHA512Hasher(MessageDigest md, byte[] salt) {
        requireNonNull(md);
        requireNonNull(salt);
        this.md = md;
        this.salt = salt;
    }

    public static SHA512Hasher getHasher() throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        try {
            salt = Files.readString(saltPath).substring(0, 16).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn("No salt found ; an empty salt will be added");
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
