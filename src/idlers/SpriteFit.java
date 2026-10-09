package idlers;

import idlers.worldmap.MapSprite;

/**
 * Fits a media sprite into a widget: the sprite on its whole canvas (so a set of icons keeps its alignment), shrunk
 * to fit when the canvas is bigger than the widget, never enlarged, and centred. Shrinking averages every source pixel
 * a target pixel covers, so pixel art keeps its shape better than when pixels are skipped.
 */
public final class SpriteFit {

    private SpriteFit() {
    }

    /** Pixels as the client's {@code RgbSprite} holds them: 0xRRGGBB, 0 transparent, so black is stored as 1. */
    public record Fitted(int[] pixels, int width, int height) {
    }

    public static Fitted fit(MapSprite sprite, int width, int height) {
        int canvasWidth = sprite.canvasWidth();
        int canvasHeight = sprite.canvasHeight();
        double scale = Math.min(1.0, Math.min((double) width / canvasWidth, (double) height / canvasHeight));
        int fittedWidth = Math.max(1, (int) Math.round(canvasWidth * scale));
        int fittedHeight = Math.max(1, (int) Math.round(canvasHeight * scale));
        int left = (width - fittedWidth) / 2;
        int top = (height - fittedHeight) / 2;
        int[] canvas = canvas(sprite);
        int[] pixels = new int[width * height];
        for (int y = 0; y < fittedHeight; y++) {
            for (int x = 0; x < fittedWidth; x++) {
                pixels[(top + y) * width + left + x] = average(canvas, canvasWidth, canvasHeight,
                        (double) x * canvasWidth / fittedWidth, (double) (x + 1) * canvasWidth / fittedWidth,
                        (double) y * canvasHeight / fittedHeight, (double) (y + 1) * canvasHeight / fittedHeight);
            }
        }
        return new Fitted(pixels, width, height);
    }

    private static int[] canvas(MapSprite sprite) {
        int[] canvas = new int[sprite.canvasWidth() * sprite.canvasHeight()];
        for (int y = 0; y < sprite.height(); y++) {
            for (int x = 0; x < sprite.width(); x++) {
                canvas[(sprite.offsetY() + y) * sprite.canvasWidth() + sprite.offsetX() + x] = sprite.pixels()[y * sprite.width() + x];
            }
        }
        return canvas;
    }

    /** The covered source pixels' average colour, or transparent when less than half the area is opaque. */
    private static int average(int[] canvas, int canvasWidth, int canvasHeight, double left, double right, double top,
            double bottom) {
        double area = 0;
        double opaque = 0;
        double red = 0;
        double green = 0;
        double blue = 0;
        for (int y = (int) top; y < Math.min(canvasHeight, Math.ceil(bottom)); y++) {
            double rowCover = Math.min(bottom, y + 1) - Math.max(top, y);
            for (int x = (int) left; x < Math.min(canvasWidth, Math.ceil(right)); x++) {
                double cover = rowCover * (Math.min(right, x + 1) - Math.max(left, x));
                int argb = canvas[y * canvasWidth + x];
                area += cover;
                if (MapSprite.isOpaque(argb)) {
                    opaque += cover;
                    red += cover * (argb >> 16 & 0xff);
                    green += cover * (argb >> 8 & 0xff);
                    blue += cover * (argb & 0xff);
                }
            }
        }
        if (opaque * 2 < area) {
            return 0;
        }
        int rgb = (int) Math.round(red / opaque) << 16 | (int) Math.round(green / opaque) << 8 | (int) Math.round(blue / opaque);
        return rgb == 0 ? 1 : rgb;
    }
}
