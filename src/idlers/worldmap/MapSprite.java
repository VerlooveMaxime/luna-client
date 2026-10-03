package idlers.worldmap;

/**
 * A sprite as the client's media format stores it: a cropped block of {@code width} by {@code height} pixels placed at
 * ({@code offsetX}, {@code offsetY}) on a canvas of {@code canvasWidth} by {@code canvasHeight}. Pixels are ARGB;
 * any alpha means opaque, alpha 0 transparent.
 */
public record MapSprite(int[] pixels, int width, int height, int offsetX, int offsetY, int canvasWidth,
                        int canvasHeight) {

    public static final int OPAQUE = 0xff000000;

    public MapSprite {
        if (pixels.length != width * height) {
            throw new IllegalArgumentException("pixels hold " + pixels.length + ", need " + width * height);
        }
    }

    public static boolean isOpaque(int argb) {
        return (argb & OPAQUE) != 0;
    }
}
