package idlers.worldmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaSpritesTest {

    private static final int RED = 0xff0000;
    private static final int BLUE = 0x0000ff;

    /** Index: 3 junk bytes, then canvas 4 x 3, palette (transparent, red, blue), a 2x1 row-major and a 2x2 column-major sprite. */
    private final byte[] index = new Bytes().u8(9, 9, 9)
            .u16(4).u16(3).u8(3).u24(RED).u24(BLUE)
            .u8(1, 2).u16(2).u16(1).u8(0)
            .u8(0, 1).u16(2).u16(2).u8(1)
            .toArray();
    private final byte[] dat = new Bytes().u16(3).u8(1, 0).u8(1, 2, 0, 1).toArray();

    @Test
    void decodesEverySpriteInTheFile() {
        assertEquals(2, MediaSprites.decode(dat, index).length);
    }

    @Test
    void keepsTheCanvasAndTheOffsets() {
        MapSprite first = MediaSprites.decode(dat, index)[0];

        assertEquals(new MapSprite(first.pixels(), 2, 1, 1, 2, 4, 3), first);
    }

    @Test
    void paletteIndexZeroIsTransparent() {
        MapSprite first = MediaSprites.decode(dat, index)[0];

        assertArrayEquals(new int[]{MapSprite.OPAQUE | RED, 0}, first.pixels());
    }

    @Test
    void columnMajorPixelsAreTransposedIntoRows() {
        MapSprite second = MediaSprites.decode(dat, index)[1];

        assertArrayEquals(new int[]{MapSprite.OPAQUE | RED, 0, MapSprite.OPAQUE | BLUE, MapSprite.OPAQUE | RED},
                second.pixels());
    }
}
