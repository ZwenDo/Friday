package com.kalia.friday.util;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.util.Objects.requireNonNull;

/**
 * Utility class to inject a hasher to hash character strings with SHA-512
 */
@Singleton
public final class SHA512Hasher {

    private final Path saltPath = Path.of("resources", "salt.txt");
    private final Logger logger = LoggerFactory.getLogger(SHA512Hasher.class);
    private final MessageDigest md;
    private final byte[] salt;

    /**
     * Constructs an SHA-512 hasher with (or without) a salt located in resources/salt.txt.
     */
    public SHA512Hasher() {
        salt = getSalt();
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-512 algorithm doesn't exist anymore");
        }
    }

    private byte[] getSalt() {
        byte[] salt;
        try {
            salt = Files.readString(saltPath).substring(0, 16).getBytes(StandardCharsets.UTF_8);
            logger.info("Hasher initialized");
        } catch (IOException e) {
            salt = new byte[16];
            logger.warn("No salt found ; an empty salt will be added");
        }
        return salt;
    }

    /**
     * Hashes a character string.
     *
     * @param word the character string to hash
     * @return the hashed character string
     */
    public String hash(String word) {
        requireNonNull(word);
        md.update(salt);
        var hashed = md.digest(word.getBytes(StandardCharsets.UTF_8));
        return new String(hashed);
    }
}
