package idlers.worldmap;

/** An RGB pixel buffer the world map draws into, with every primitive clipped to its bounds. */
public final class Raster {

    private final int[] pixels;
    private final int width;
    private final int height;

    public Raster(int[] pixels, int width, int height) {
        if (pixels.length < width * height) {
            throw new IllegalArgumentException("pixels hold " + pixels.length + ", need " + width * height);
        }
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public static Raster blank(int width, int height) {
        return new Raster(new int[width * height], width, height);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int pixel(int x, int y) {
        return pixels[y * width + x];
    }

    public void plot(int x, int y, int rgb) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            pixels[y * width + x] = rgb;
        }
    }

    public void clear(int rgb) {
        java.util.Arrays.fill(pixels, 0, width * height, rgb);
    }

    public void fillRect(int x, int y, int w, int h, int rgb) {
        int left = Math.max(x, 0);
        int right = Math.min(x + w, width);
        int top = Math.max(y, 0);
        int bottom = Math.min(y + h, height);
        for (int row = top; row < bottom; row++) {
            java.util.Arrays.fill(pixels, row * width + left, row * width + Math.max(left, right), rgb);
        }
    }

    /** Blends {@code rgb} over the area; {@code alpha} 0 leaves it unchanged, 256 paints it solid. */
    public void fillRectAlpha(int x, int y, int w, int h, int rgb, int alpha) {
        int left = Math.max(x, 0);
        int right = Math.min(x + w, width);
        int top = Math.max(y, 0);
        int bottom = Math.min(y + h, height);
        for (int row = top; row < bottom; row++) {
            for (int column = left; column < right; column++) {
                int index = row * width + column;
                pixels[index] = blend(rgb, pixels[index], alpha);
            }
        }
    }

    public void drawRect(int x, int y, int w, int h, int rgb) {
        hline(x, y, w, rgb);
        hline(x, y + h - 1, w, rgb);
        vline(x, y, h, rgb);
        vline(x + w - 1, y, h, rgb);
    }

    public void hline(int x, int y, int length, int rgb) {
        fillRect(x, y, length, 1, rgb);
    }

    public void vline(int x, int y, int length, int rgb) {
        fillRect(x, y, 1, length, rgb);
    }

    /** A disc of {@code radius} around the centre, blended like {@link #fillRectAlpha}. */
    public void fillCircle(int centreX, int centreY, int radius, int rgb, int alpha) {
        for (int y = centreY - radius; y <= centreY + radius; y++) {
            int dy = y - centreY;
            int half = (int) Math.sqrt((double) radius * radius - dy * dy);
            fillRectAlpha(centreX - half, y, half * 2 + 1, 1, rgb, alpha);
        }
    }

    /** Draws the sprite's opaque pixels with its canvas top-left corner at (x, y). */
    public void drawSprite(MapSprite sprite, int x, int y) {
        int left = x + sprite.offsetX();
        int top = y + sprite.offsetY();
        for (int row = 0; row < sprite.height(); row++) {
            for (int column = 0; column < sprite.width(); column++) {
                int argb = sprite.pixels()[row * sprite.width() + column];
                if (MapSprite.isOpaque(argb)) {
                    plot(left + column, top + row, argb & 0xffffff);
                }
            }
        }
    }

    /** Draws the sprite's whole canvas stretched to {@code w} by {@code h}, nearest neighbour. */
    public void drawSpriteScaled(MapSprite sprite, int x, int y, int w, int h) {
        for (int row = 0; row < h; row++) {
            int sourceY = row * sprite.canvasHeight() / h - sprite.offsetY();
            for (int column = 0; column < w; column++) {
                int sourceX = column * sprite.canvasWidth() / w - sprite.offsetX();
                if (sourceX >= 0 && sourceY >= 0 && sourceX < sprite.width() && sourceY < sprite.height()) {
                    int argb = sprite.pixels()[sourceY * sprite.width() + sourceX];
                    if (MapSprite.isOpaque(argb)) {
                        plot(x + column, y + row, argb & 0xffffff);
                    }
                }
            }
        }
    }

    /** Copies {@code source}, which must have this raster's size. */
    public void copyFrom(Raster source) {
        if (source.width != width || source.height != height) {
            throw new IllegalArgumentException("size " + source.width + "x" + source.height + " is not " + width + "x"
                    + height);
        }
        System.arraycopy(source.pixels, 0, pixels, 0, width * height);
    }

    static int blend(int rgb, int under, int alpha) {
        int inverse = 256 - alpha;
        int redBlue = ((rgb & 0xff00ff) * alpha + (under & 0xff00ff) * inverse) >>> 8 & 0xff00ff;
        int green = ((rgb & 0xff00) * alpha + (under & 0xff00) * inverse) >>> 8 & 0xff00;
        return redBlue | green;
    }
}
