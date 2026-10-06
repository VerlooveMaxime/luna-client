package idlers;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdleTabIconTest {

    @Test
    void everyRowIsAsWideAsTheIcon() {
        assertTrue(Arrays.stream(IdleTabIcon.ART).allMatch(row -> row.length() == IdleTabIcon.WIDTH));
    }

    @Test
    void everyCharacterIsTransparentOrInThePalette() {
        assertTrue(String.join("", IdleTabIcon.ART).chars()
                .allMatch(c -> c == ' ' || IdleTabIcon.PALETTE.containsKey((char) c)));
    }

    @Test
    void aSpaceShowsTheTabThroughAndALetterIsItsColour() {
        int[] pixels = IdleTabIcon.pixels();

        assertEquals(0, pixels[0]);
        assertEquals(0x2a2016, pixels[1]);
        assertEquals(0xf0d070, pixels[10 * IdleTabIcon.WIDTH + 7]);
    }

    @Test
    void theIconSitsInTabSevensCell() {
        assertEquals(15, IdleTabIcon.WIDTH);
        assertEquals(22, IdleTabIcon.HEIGHT);
        assertEquals(56, IdleTabIcon.X);
        assertEquals(7, IdleTabIcon.Y);
    }
}
