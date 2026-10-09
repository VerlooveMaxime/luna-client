package idlers;

import idlers.worldmap.MapSprite;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpriteFitTest {

    private static final int RED = 0xff0000;

    private static int opaque(int rgb) {
        return MapSprite.OPAQUE | rgb;
    }

    @Test
    void aSpriteSmallerThanTheWidgetKeepsItsSizeAndIsCentred() {
        MapSprite sprite = new MapSprite(new int[]{opaque(RED), opaque(RED), opaque(RED), opaque(RED)}, 2, 2, 0, 0, 2, 2);

        SpriteFit.Fitted fitted = SpriteFit.fit(sprite, 4, 4);

        assertArrayEquals(new int[]{
                0, 0, 0, 0,
                0, RED, RED, 0,
                0, RED, RED, 0,
                0, 0, 0, 0}, fitted.pixels());
    }

    @Test
    void theFittedSpriteHasTheWidgetsSize() {
        SpriteFit.Fitted fitted = SpriteFit.fit(new MapSprite(new int[]{opaque(RED)}, 1, 1, 0, 0, 1, 1), 3, 2);

        assertEquals(3, fitted.width());
        assertEquals(2, fitted.height());
    }

    @Test
    void theOpaquePixelsAreCentredWhateverRoomTheirCanvasLeaves() {
        MapSprite flag = new MapSprite(new int[]{opaque(RED), 0, 0}, 1, 3, 0, 0, 1, 6);

        assertArrayEquals(new int[]{0, RED, 0}, SpriteFit.fit(flag, 1, 3).pixels());
    }

    @Test
    void aSpriteWithNoOpaquePixelLeavesTheWidgetEmpty() {
        MapSprite blank = new MapSprite(new int[]{0, 0}, 2, 1, 0, 0, 2, 1);

        assertArrayEquals(new int[]{0, 0, 0}, SpriteFit.fit(blank, 3, 1).pixels());
    }

    @Test
    void blackIsStoredAsOneSinceZeroIsTransparent() {
        MapSprite sprite = new MapSprite(new int[]{opaque(0)}, 1, 1, 0, 0, 1, 1);

        assertArrayEquals(new int[]{1}, SpriteFit.fit(sprite, 1, 1).pixels());
    }

    @Test
    void shrinkingAveragesThePixelsATargetPixelCovers() {
        MapSprite sprite = new MapSprite(new int[]{opaque(0x100000), opaque(0x300000), opaque(0x100000), opaque(0x300000)},
                2, 2, 0, 0, 2, 2);

        assertArrayEquals(new int[]{0x200000}, SpriteFit.fit(sprite, 1, 1).pixels());
    }

    @Test
    void aPixelCoveredPartlyCountsByTheShareItCovers() {
        MapSprite sprite = new MapSprite(new int[]{opaque(0x600000), opaque(0), opaque(0x600000)}, 3, 1, 0, 0, 3, 1);

        assertArrayEquals(new int[]{0x400000, 0x400000}, SpriteFit.fit(sprite, 2, 1).pixels());
    }

    @Test
    void aTargetPixelLessThanHalfOpaqueIsTransparent() {
        MapSprite sprite = new MapSprite(new int[]{opaque(RED), 0, 0, 0, opaque(RED)}, 5, 1, 0, 0, 5, 1);

        assertArrayEquals(new int[]{0}, SpriteFit.fit(sprite, 1, 1).pixels());
    }

    @Test
    void aTargetPixelHalfOpaqueTakesTheOpaquePixelsColour() {
        MapSprite sprite = new MapSprite(new int[]{opaque(0x200000), 0, 0, opaque(0x400000)}, 2, 2, 0, 0, 2, 2);

        assertArrayEquals(new int[]{0x300000}, SpriteFit.fit(sprite, 1, 1).pixels());
    }

    @Test
    void aTallSpriteShrinksToTheWidgetsHeightAndKeepsItsShape() {
        int[] pixels = {opaque(RED), opaque(RED), opaque(RED), opaque(RED), opaque(RED), opaque(RED), opaque(RED), opaque(RED)};
        MapSprite sprite = new MapSprite(pixels, 2, 4, 0, 0, 2, 4);

        assertArrayEquals(new int[]{
                0, RED, 0, 0,
                0, RED, 0, 0}, SpriteFit.fit(sprite, 4, 2).pixels());
    }

    @Test
    void aClientSpriteKeepsItsColoursAndItsTransparency() {
        MapSprite sprite = SpriteFit.clientSprite(new int[]{RED, 0}, 2, 1);

        assertArrayEquals(new int[]{opaque(RED), 0}, sprite.pixels());
        assertEquals(2, sprite.canvasWidth());
    }
}
