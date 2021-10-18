package com.notkamui;

import com.notkamui.utils.SHA512Hasher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

public class HasherTest {

    @Test
    public void testHasherTwiceWithOneInstance() throws NoSuchAlgorithmException {
        var toHash = "APZOEI102938";

        var hasher = SHA512Hasher.getHasher();

        var hashed1 = hasher.hash(toHash);
        var hashed2 = hasher.hash(toHash);

        Assertions.assertEquals(hashed1, hashed2);
    }

    @Test
    public void testHasherTwiceWithTwoInstances() throws NoSuchAlgorithmException {
        var toHash = "APZOEI102938";

        var hasher1 = SHA512Hasher.getHasher();
        var hasher2 = SHA512Hasher.getHasher();

        var hashed1 = hasher1.hash(toHash);
        var hashed2 = hasher2.hash(toHash);

        Assertions.assertEquals(hashed1, hashed2);
    }
}
