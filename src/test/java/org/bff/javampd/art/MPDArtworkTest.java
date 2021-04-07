package org.bff.javampd.art;

import org.bff.javampd.artist.MPDArtist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MPDArtworkTest {
    @Test
    void testEqualsSameObject() {
        MPDArtwork artwork = new MPDArtwork("name", "path");
        assertEquals(artwork, artwork);
    }

    @Test
    void testGetName() {
        String name = "name";
        MPDArtwork artwork = new MPDArtwork(name, "path");
        assertEquals(name, artwork.getName());
    }

    @Test
    void testGetPath() {
        String path = "path";
        MPDArtwork artwork = new MPDArtwork("name", path);
        assertEquals(path, artwork.getPath());
    }

    @Test
    void testEqualsSamePath() {
        MPDArtwork artwork1 = new MPDArtwork("name", "path");
        MPDArtwork artwork2 = new MPDArtwork("name", "path");
        assertEquals(artwork1, artwork2);
    }

    @Test
    void testEqualsDifferentPath() {
        MPDArtwork artwork1 = new MPDArtwork("name", "path1");
        MPDArtwork artwork2 = new MPDArtwork("name", "path2");
        assertNotEquals(artwork1, artwork2);
    }

    @Test
    void testEqualsNullPath() {
        MPDArtwork artwork1 = new MPDArtwork("name", null);
        MPDArtwork artwork2 = new MPDArtwork("name", "path2");
        assertNotEquals(artwork1, artwork2);
    }

    @Test
    void testEqualsNullPathParameter() {
        MPDArtwork artwork1 = new MPDArtwork("name", "path1");
        MPDArtwork artwork2 = new MPDArtwork("name", null);
        assertNotEquals(artwork1, artwork2);
    }

    @Test
    void testEqualsDifferentObject() {
        MPDArtwork artwork = new MPDArtwork("name", "path");
        MPDArtist artist = new MPDArtist("artist");
        assertNotEquals(artwork, artist);
    }

    @Test
    void testEqualsBothNull() {
        MPDArtwork artwork1 = new MPDArtwork("name", null);
        MPDArtwork artwork2 = new MPDArtwork("name", null);
        assertEquals(artwork1, artwork2);
    }

    @Test
    void testHashCodeSamePath() {
        MPDArtwork artwork1 = new MPDArtwork("name", "path");
        MPDArtwork artwork2 = new MPDArtwork("name", "path");
        assertEquals(artwork1.hashCode(), artwork2.hashCode());
    }

    @Test
    void testHashCodeDifferentPath() {
        MPDArtwork artwork1 = new MPDArtwork("name", "path1");
        MPDArtwork artwork2 = new MPDArtwork("name", "path2");
        assertNotEquals(artwork1.hashCode(), artwork2.hashCode());
    }

    @Test
    void testGetBytes() {
        byte[] bytes = {
                0
        };

        MPDArtwork artwork = new MPDArtwork("name", "path");
        artwork.setBytes(bytes);

        assertEquals(bytes, artwork.getBytes());
    }
}
