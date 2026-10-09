package idlers;

import idlers.worldmap.MapSprite;

/**
 * Fits a sprite into a widget: its opaque pixels, whatever room its canvas leaves around them (the minimap's flag
 * sits at the top of a canvas twice its height), shrunk to fit when they are bigger than the widget, never enlarged,
 * and centred. Shrinking averages every source pixel a target pixel covers, so pixel art keeps its shape better than
 * when pixels are skipped.
 */
public final class SpriteFit {

    private SpriteFit() {
    }

    /** Pixels as the client's {@code RgbSprite} holds them: 0xRRGGBB, 0 transparent, so black is stored as 1. */
    public record Fitted(int[] pixels, int width, int height) {
    }

    /** A sprite the client drew, such as an item's inventory icon, read as a media sprite: 0 is transparent. */
    public static MapSprite clientSprite(int[] pixels, int width, int height) {
        int[] argb = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            argb[i] = pixels[i] == 0 ? 0 : MapSprite.OPAQUE | pixels[i];
        }
        return new MapSprite(argb, width, height, 0, 0, width, height);
    }

    public static Fitted fit(MapSprite sprite, int width, int height) {
        int[] pixels = new int[width * height];
        Bounds art = Bounds.of(sprite);
        if (art == null) {
            return new Fitted(pixels, width, height);
        }
        double scale = Math.min(1.0, Math.min((double) width / art.width(), (double) height / art.height()));
        int fittedWidth = Math.max(1, (int) Math.round(art.width() * scale));
        int fittedHeight = Math.max(1, (int) Math.round(art.height() * scale));
        int left = (width - fittedWidth) / 2;
        int top = (height - fittedHeight) / 2;
        for (int y = 0; y < fittedHeight; y++) {
            for (int x = 0; x < fittedWidth; x++) {
                pixels[(top + y) * width + left + x] = average(sprite,
                        art.left() + (double) x * art.width() / fittedWidth, art.left() + (double) (x + 1) * art.width() / fittedWidth,
                        art.top() + (double) y * art.height() / fittedHeight, art.top() + (double) (y + 1) * art.height() / fittedHeight);
            }
        }
        return new Fitted(pixels, width, height);
    }

    /** The smallest rectangle of the sprite's pixels holding every opaque one. */
    private record Bounds(int left, int top, int width, int height) {

        /** Null for a sprite with no opaque pixel. */
        static Bounds of(MapSprite sprite) {
            int left = sprite.width();
            int top = sprite.height();
            int right = -1;
            int bottom = -1;
            for (int y = 0; y < sprite.height(); y++) {
                for (int x = 0; x < sprite.width(); x++) {
                    if (MapSprite.isOpaque(sprite.pixels()[y * sprite.width() + x])) {
                        left = Math.min(left, x);
                        top = Math.min(top, y);
                        right = Math.max(right, x);
                        bottom = Math.max(bottom, y);
                    }
                }
            }
            return right < 0 ? null : new Bounds(left, top, right - left + 1, bottom - top + 1);
        }
    }

    /** The covered source pixels' average colour, or transparent when less than half the area is opaque. */
    private static int average(MapSprite sprite, double left, double right, double top, double bottom) {
        double area = 0;
        double opaque = 0;
        double red = 0;
        double green = 0;
        double blue = 0;
        for (int y = (int) top; y < Math.ceil(bottom); y++) {
            double rowCover = Math.min(bottom, y + 1) - Math.max(top, y);
            for (int x = (int) left; x < Math.ceil(right); x++) {
                double cover = rowCover * (Math.min(right, x + 1) - Math.max(left, x));
                int argb = sprite.pixels()[y * sprite.width() + x];
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
