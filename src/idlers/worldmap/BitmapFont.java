package idlers.worldmap;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * One-colour glyphs drawn onto a {@link Raster}: the client's own fonts copied in by the glue, or an AWT font
 * rasterised once without antialiasing, the way the 2006 applet made its label fonts from "Helvetica".
 */
public final class BitmapFont {

    /** One glyph; {@code top} is relative to the baseline (negative above it), {@code left} to the pen. */
    public record Glyph(byte[] mask, int width, int height, int left, int top, int advance) {

        public Glyph {
            if (mask.length != width * height) {
                throw new IllegalArgumentException("mask holds " + mask.length + ", need " + width * height);
            }
        }
    }

    private static final int FIRST_AWT_CHAR = 32;
    private static final int LAST_AWT_CHAR = 126;

    private final Glyph[] glyphs;
    private final int ascent;
    private final int lineHeight;

    /** {@code glyphs} is indexed by character; a missing glyph draws nothing and takes no space. */
    public BitmapFont(Glyph[] glyphs, int ascent, int lineHeight) {
        this.glyphs = glyphs.clone();
        this.ascent = ascent;
        this.lineHeight = lineHeight;
    }

    public static BitmapFont fromAwt(Font font) {
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D probeGraphics = probe.createGraphics();
        FontMetrics metrics = probeGraphics.getFontMetrics(font);
        probeGraphics.dispose();
        int cell = metrics.getMaxAscent() + metrics.getMaxDescent() + 2;
        Glyph[] glyphs = new Glyph[LAST_AWT_CHAR + 1];
        for (int c = FIRST_AWT_CHAR; c <= LAST_AWT_CHAR; c++) {
            glyphs[c] = rasterise(font, (char) c, metrics.charWidth(c), metrics.getMaxAscent(), cell);
        }
        return new BitmapFont(glyphs, metrics.getAscent(), metrics.getHeight());
    }

    private static Glyph rasterise(Font font, char c, int advance, int maxAscent, int cell) {
        int canvasWidth = advance + cell;
        BufferedImage image = new BufferedImage(canvasWidth, cell, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setFont(font);
        graphics.setColor(java.awt.Color.WHITE);
        graphics.drawString(String.valueOf(c), 0, maxAscent);
        graphics.dispose();

        int left = canvasWidth;
        int right = 0;
        int top = cell;
        int bottom = 0;
        for (int y = 0; y < cell; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if ((image.getRGB(x, y) & 0xffffff) != 0) {
                    left = Math.min(left, x);
                    right = Math.max(right, x + 1);
                    top = Math.min(top, y);
                    bottom = Math.max(bottom, y + 1);
                }
            }
        }
        if (right <= left) {
            return new Glyph(new byte[0], 0, 0, 0, 0, advance);
        }
        int width = right - left;
        int height = bottom - top;
        byte[] mask = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mask[y * width + x] = (byte) ((image.getRGB(left + x, top + y) & 0xffffff) != 0 ? 1 : 0);
            }
        }
        return new Glyph(mask, width, height, left, top - maxAscent, advance);
    }

    public int ascent() {
        return ascent;
    }

    public int lineHeight() {
        return lineHeight;
    }

    public int width(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            Glyph glyph = glyph(text.charAt(i));
            width += glyph == null ? 0 : glyph.advance();
        }
        return width;
    }

    public void draw(Raster raster, String text, int x, int baseline, int rgb) {
        int pen = x;
        for (int i = 0; i < text.length(); i++) {
            Glyph glyph = glyph(text.charAt(i));
            if (glyph != null) {
                plot(raster, glyph, pen, baseline, rgb);
                pen += glyph.advance();
            }
        }
    }

    /** Draws a black copy one pixel right and one pixel down first, as the applet's labels and buttons do. */
    public void drawShadowed(Raster raster, String text, int x, int baseline, int rgb) {
        draw(raster, text, x + 1, baseline, 0);
        draw(raster, text, x, baseline + 1, 0);
        draw(raster, text, x, baseline, rgb);
    }

    public void drawCentred(Raster raster, String text, int centreX, int baseline, int rgb, boolean shadowed) {
        int x = centreX - width(text) / 2;
        if (shadowed) {
            drawShadowed(raster, text, x, baseline, rgb);
        } else {
            draw(raster, text, x, baseline, rgb);
        }
    }

    private Glyph glyph(char c) {
        return c < glyphs.length ? glyphs[c] : null;
    }

    private static void plot(Raster raster, Glyph glyph, int pen, int baseline, int rgb) {
        int left = pen + glyph.left();
        int top = baseline + glyph.top();
        for (int y = 0; y < glyph.height(); y++) {
            for (int x = 0; x < glyph.width(); x++) {
                if (glyph.mask()[y * glyph.width() + x] != 0) {
                    raster.plot(left + x, top + y, rgb);
                }
            }
        }
    }
}
