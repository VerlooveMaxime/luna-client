package idlers.worldmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RasterTest {

    @Test
    void rejectsAPixelArraySmallerThanItsSize() {
        assertThrows(IllegalArgumentException.class, () -> new Raster(new int[5], 2, 3));
    }

    @Test
    void plotOutsideTheBoundsIsIgnored() {
        Raster raster = Raster.blank(2, 2);

        raster.plot(-1, 0, 7);
        raster.plot(0, 2, 7);

        assertEquals(0, raster.pixel(0, 0) + raster.pixel(1, 0) + raster.pixel(0, 1) + raster.pixel(1, 1));
    }

    @Test
    void plotAboveOrRightOfTheBoundsIsIgnored() {
        Raster raster = Raster.blank(2, 2);

        raster.plot(0, -1, 7);
        raster.plot(2, 0, 7);

        assertEquals(0, raster.pixel(0, 0) + raster.pixel(1, 0) + raster.pixel(0, 1) + raster.pixel(1, 1));
    }

    @Test
    void clearPaintsEveryPixel() {
        Raster raster = Raster.blank(2, 2);

        raster.clear(5);

        assertEquals(5, raster.pixel(1, 1));
    }

    @Test
    void fillRectIsClippedToTheRaster() {
        Raster raster = Raster.blank(3, 3);

        raster.fillRect(-1, 2, 3, 5, 9);

        assertEquals(9, raster.pixel(1, 2));
        assertEquals(0, raster.pixel(2, 2));
        assertEquals(0, raster.pixel(0, 1));
    }

    @Test
    void fillRectEntirelyOutsideDrawsNothing() {
        Raster raster = Raster.blank(3, 3);

        raster.fillRect(5, 0, 2, 2, 9);

        assertEquals(0, raster.pixel(2, 0));
    }

    @Test
    void halfAlphaBlendsHalfway() {
        Raster raster = Raster.blank(1, 1);
        raster.clear(0x000000);

        raster.fillRectAlpha(0, 0, 1, 1, 0xff80ff, 128);

        assertEquals(0x7f407f, raster.pixel(0, 0));
    }

    @Test
    void fullAlphaPaintsSolid() {
        Raster raster = Raster.blank(1, 1);
        raster.clear(0x123456);

        raster.fillRectAlpha(0, 0, 1, 1, 0xabcdef, 256);

        assertEquals(0xabcdef, raster.pixel(0, 0));
    }

    @Test
    void drawRectOutlinesWithoutFilling() {
        Raster raster = Raster.blank(3, 3);

        raster.drawRect(0, 0, 3, 3, 1);

        assertEquals(1, raster.pixel(2, 2));
        assertEquals(1, raster.pixel(0, 1));
        assertEquals(0, raster.pixel(1, 1));
    }

    @Test
    void fillCircleCoversTheDiscButNotTheCorners() {
        Raster raster = Raster.blank(5, 5);

        raster.fillCircle(2, 2, 2, 0xffffff, 256);

        assertEquals(0xffffff, raster.pixel(2, 0));
        assertEquals(0xffffff, raster.pixel(3, 3));
        assertEquals(0, raster.pixel(0, 0));
    }

    @Test
    void drawSpriteSkipsTransparentPixelsAndAppliesTheOffset() {
        MapSprite sprite = new MapSprite(new int[]{MapSprite.OPAQUE | 0x112233, 0}, 2, 1, 1, 1, 4, 4);
        Raster raster = Raster.blank(4, 4);
        raster.clear(7);

        raster.drawSprite(sprite, 1, 0);

        assertEquals(0x112233, raster.pixel(2, 1));
        assertEquals(7, raster.pixel(3, 1));
    }

    @Test
    void drawSpriteScaledStretchesTheWholeCanvas() {
        MapSprite sprite = new MapSprite(new int[]{MapSprite.OPAQUE | 0xaa}, 1, 1, 1, 0, 2, 1);
        Raster raster = Raster.blank(4, 2);

        raster.drawSpriteScaled(sprite, 0, 0, 4, 2);

        assertEquals(0, raster.pixel(1, 0));
        assertEquals(0xaa, raster.pixel(2, 1));
        assertEquals(0xaa, raster.pixel(3, 0));
    }

    @Test
    void drawSpriteScaledLeavesTheCanvasAroundTheCroppedPixels() {
        MapSprite sprite = new MapSprite(new int[]{MapSprite.OPAQUE | 0xaa}, 1, 1, 1, 1, 3, 3);
        Raster raster = Raster.blank(3, 3);

        raster.drawSpriteScaled(sprite, 0, 0, 3, 3);

        assertEquals(java.util.List.of(0xaa, 0, 0, 0), java.util.List.of(raster.pixel(1, 1), raster.pixel(1, 0),
                raster.pixel(2, 1), raster.pixel(1, 2)));
    }

    @Test
    void drawSpriteScaledSkipsTransparentPixels() {
        MapSprite sprite = new MapSprite(new int[]{0}, 1, 1, 0, 0, 1, 1);
        Raster raster = Raster.blank(2, 2);
        raster.clear(3);

        raster.drawSpriteScaled(sprite, 0, 0, 2, 2);

        assertEquals(3, raster.pixel(1, 1));
    }

    @Test
    void copyFromTakesEveryPixel() {
        Raster source = Raster.blank(2, 1);
        source.plot(1, 0, 4);
        Raster target = Raster.blank(2, 1);

        target.copyFrom(source);

        assertEquals(4, target.pixel(1, 0));
    }

    @Test
    void copyFromRejectsADifferentWidth() {
        Raster target = Raster.blank(2, 1);

        assertThrows(IllegalArgumentException.class, () -> target.copyFrom(Raster.blank(1, 1)));
    }

    @Test
    void copyFromRejectsADifferentHeight() {
        Raster target = Raster.blank(2, 1);

        assertThrows(IllegalArgumentException.class, () -> target.copyFrom(Raster.blank(2, 2)));
    }
}
