package com.kalia.friday.util;

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
 * Utility class to obtain a hasher to hash character strings with SHA-512
 */
public final class SHA512Hasher {

    private static final Path SALT_PATH = Path.of("resources", "salt.txt");
    private static final Logger LOGGER = LoggerFactory.getLogger(SHA512Hasher.class);

    private final MessageDigest md;
    private final byte[] salt;

    private SHA512Hasher(MessageDigest md, byte[] salt) {
        this.md = requireNonNull(md);
        this.salt = requireNonNull(salt);
    }

    /**
     * Obtains a SHA-512 hasher.
     * If a file `resources/salt.txt` exists, it will take the first 16 bytes
     * of said file to create a salt for the hasher.
     * <p><b>Warning</b> : if the salt is lost... all your passwords will be unrecoverable.</p>
     *
     * @return a salted SHA-512 hasher
     * @throws NoSuchAlgorithmException (should never happen, unless SHA-512 doesn't exist anymore)
     */
    public static SHA512Hasher getHasher() throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        try {
            salt = Files.readString(SALT_PATH).substring(0, 16).getBytes(StandardCharsets.UTF_8);
            LOGGER.info("Hasher initialized");
        } catch (IOException e) {
            LOGGER.warn("No salt found ; an empty salt will be added");
        }
        var md = MessageDigest.getInstance("SHA-512");
        return new SHA512Hasher(md, salt);
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
