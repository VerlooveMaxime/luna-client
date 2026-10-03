package idlers.worldmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes every sprite of one name in a Jagex media archive ({@code <name>.dat} plus the shared {@code index.dat}),
 * the format the client's {@code RgbSprite} and {@code IndexedSprite} read one sprite at a time. Palette index 0 is
 * transparent.
 */
public final class MediaSprites {

    private static final int COLUMN_MAJOR = 1;

    private MediaSprites() {
    }

    public static MapSprite[] decode(byte[] dat, byte[] index) {
        ByteReader pixels = new ByteReader(dat);
        ByteReader header = new ByteReader(index);
        header.skip(pixels.u16());
        int canvasWidth = header.u16();
        int canvasHeight = header.u16();
        int[] palette = new int[header.u8()];
        for (int i = 1; i < palette.length; i++) {
            palette[i] = header.u24();
        }
        List<MapSprite> sprites = new ArrayList<>();
        while (pixels.remaining() > 0) {
            int offsetX = header.u8();
            int offsetY = header.u8();
            int width = header.u16();
            int height = header.u16();
            boolean columnMajor = header.u8() == COLUMN_MAJOR;
            int[] argb = new int[width * height];
            for (int i = 0; i < argb.length; i++) {
                int target = columnMajor ? (i % height) * width + i / height : i;
                int colour = pixels.u8();
                argb[target] = colour == 0 ? 0 : MapSprite.OPAQUE | palette[colour];
            }
            sprites.add(new MapSprite(argb, width, height, offsetX, offsetY, canvasWidth, canvasHeight));
        }
        return sprites.toArray(new MapSprite[0]);
    }
}
