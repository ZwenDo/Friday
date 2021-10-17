package com.notkamui;

import com.notkamui.utils.SHA512Hasher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

public class HasherTest {

    @Test
    public void testHasherTwice() throws NoSuchAlgorithmException {
        var toHash = "APZOEI102938";

        var hasher1 = new SHA512Hasher();
        var hasher2 = new SHA512Hasher();

        var hashed1 = hasher1.hash(toHash);
        var hashed2 = hasher2.hash(toHash);

        System.out.printf("AAAAAAAAAAAAAAAAAAAAAAAA%n%s%n%s%n", hashed1, hashed2);

        Assertions.assertEquals(hashed1, hashed2);
    }
}
