package idlers.worldmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapSpriteTest {

    @Test
    void rejectsPixelsThatDoNotMatchTheSize() {
        assertThrows(IllegalArgumentException.class, () -> new MapSprite(new int[3], 2, 2, 0, 0, 2, 2));
    }

    @Test
    void anyAlphaIsOpaque() {
        assertTrue(MapSprite.isOpaque(0x01000000));
    }

    @Test
    void zeroAlphaIsTransparentEvenWithAColour() {
        assertFalse(MapSprite.isOpaque(0x00ffffff));
    }
}
