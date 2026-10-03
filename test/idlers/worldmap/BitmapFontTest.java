package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.awt.Font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitmapFontTest {

    private final BitmapFont font = TestMaps.blockFont();

    @Test
    void aGlyphRejectsAMaskOfTheWrongSize() {
        assertThrows(IllegalArgumentException.class, () -> new BitmapFont.Glyph(new byte[3], 2, 2, 0, 0, 2));
    }

    @Test
    void widthAddsTheAdvancesAndIgnoresUnknownCharacters() {
        assertEquals(3 + 2 + 3, font.width("A AĀ?"));
    }

    @Test
    void drawPutsTheGlyphAboveTheBaseline() {
        Raster raster = Raster.blank(4, 5);

        font.draw(raster, "A", 1, 4, 9);

        assertEquals(9, raster.pixel(1, 1));
        assertEquals(9, raster.pixel(2, 3));
        assertEquals(0, raster.pixel(1, 4));
        assertEquals(0, raster.pixel(1, 0));
    }

    @Test
    void drawAdvancesThePenBetweenGlyphs() {
        Raster raster = Raster.blank(8, 4);

        font.draw(raster, "AA", 0, 3, 9);

        assertEquals(9, raster.pixel(3, 0));
        assertEquals(0, raster.pixel(2, 0));
    }

    @Test
    void drawSkipsUnknownCharactersWithoutMoving() {
        Raster raster = Raster.blank(8, 4);

        font.draw(raster, "?A", 0, 3, 9);

        assertEquals(9, raster.pixel(0, 0));
    }

    @Test
    void theShadowGoesOnePixelRightAndDown() {
        Raster raster = Raster.blank(4, 5);
        raster.clear(1);

        font.drawShadowed(raster, "A", 0, 3, 9);

        assertEquals(0, raster.pixel(2, 0));
        assertEquals(0, raster.pixel(0, 3));
        assertEquals(9, raster.pixel(1, 2));
    }

    @Test
    void drawCentredCentresOnTheGivenX() {
        Raster raster = Raster.blank(6, 4);

        font.drawCentred(raster, "AA", 3, 3, 9, false);

        assertEquals(9, raster.pixel(0, 0));
        assertEquals(9, raster.pixel(4, 0));
        assertEquals(0, raster.pixel(5, 0));
    }

    @Test
    void drawCentredCanAddTheShadow() {
        Raster raster = Raster.blank(6, 5);
        raster.clear(1);

        font.drawCentred(raster, "A", 2, 3, 9, true);

        assertEquals(0, raster.pixel(1, 3));
    }

    @Test
    void anAwtFontGivesEveryPrintableCharacterAnAdvance() {
        BitmapFont awt = BitmapFont.fromAwt(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        assertTrue(awt.width("W") > 0);
        assertTrue(awt.width(" ") > 0);
    }

    @Test
    void anAwtGlyphDrawsInk() {
        BitmapFont awt = BitmapFont.fromAwt(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        Raster raster = Raster.blank(20, 20);

        awt.draw(raster, "W", 2, 16, 0xffffff);

        assertTrue(inkCount(raster) > 10);
    }

    @Test
    void anAwtFontKeepsTheMetricsAscentAndHeight() {
        BitmapFont awt = BitmapFont.fromAwt(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        assertTrue(awt.ascent() > 0 && awt.lineHeight() > awt.ascent());
    }

    private static int inkCount(Raster raster) {
        int count = 0;
        for (int y = 0; y < raster.height(); y++) {
            for (int x = 0; x < raster.width(); x++) {
                count += raster.pixel(x, y) != 0 ? 1 : 0;
            }
        }
        return count;
    }
}
