package com.kalia.friday.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HasherTest {

    @Test
    public void testHasherTwiceWithOneInstance() {
        var toHash = "APZOEI102938";

        var hasher = new SHA512Hasher();

        var hashed1 = hasher.hash(toHash);
        var hashed2 = hasher.hash(toHash);

        Assertions.assertEquals(hashed1, hashed2);
    }

    @Test
    public void testHasherTwiceWithTwoInstances() {
        var toHash = "APZOEI102938";

        var hasher1 = new SHA512Hasher();
        var hasher2 = new SHA512Hasher();

        var hashed1 = hasher1.hash(toHash);
        var hashed2 = hasher2.hash(toHash);

        Assertions.assertEquals(hashed1, hashed2);
    }
}
