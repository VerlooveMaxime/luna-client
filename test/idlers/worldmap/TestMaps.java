package idlers.worldmap;

import java.util.ArrayList;
import java.util.List;

/** Small hand-made maps, sprites and fonts for the world map tests. */
final class TestMaps {

    private TestMaps() {
    }

    /** A map whose tiles are all set later by the test, with origin (1000, 2000). */
    static final class Builder {

        final int width;
        final int height;
        final int[] ground;
        final int[] overlay;
        final byte[] overlayShape;
        final byte[] wall;
        final byte[] mapscene;
        final byte[] function;
        final List<MapData.FunctionPoint> points = new ArrayList<>();
        final List<MapData.Label> labels = new ArrayList<>();

        Builder(int width, int height) {
            this.width = width;
            this.height = height;
            ground = new int[width * height];
            overlay = new int[width * height];
            overlayShape = new byte[width * height];
            wall = new byte[width * height];
            mapscene = new byte[width * height];
            function = new byte[width * height];
        }

        Builder ground(int rgb) {
            java.util.Arrays.fill(ground, rgb);
            return this;
        }

        Builder overlay(int column, int row, int rgb, int shape, int rotation) {
            overlay[row * width + column] = rgb;
            overlayShape[row * width + column] = (byte) (shape << 2 | rotation);
            return this;
        }

        Builder wall(int column, int row, int kind) {
            wall[row * width + column] = (byte) kind;
            return this;
        }

        Builder mapscene(int column, int row, int id) {
            mapscene[row * width + column] = (byte) (id + 1);
            return this;
        }

        Builder function(int column, int row, int id) {
            function[row * width + column] = (byte) (id + 1);
            points.add(new MapData.FunctionPoint(column, row, id));
            return this;
        }

        Builder label(String text, int worldX, int worldY, int size) {
            labels.add(new MapData.Label(text, worldX, worldY, size));
            return this;
        }

        MapData build() {
            return new MapData(1000, 2000, width, height, ground, overlay, overlayShape, wall, mapscene, function,
                    points, labels);
        }
    }

    static Builder map(int width, int height) {
        return new Builder(width, height);
    }

    /** A sprite filled with one opaque colour. */
    static MapSprite solidSprite(int width, int height, int rgb) {
        int[] pixels = new int[width * height];
        java.util.Arrays.fill(pixels, MapSprite.OPAQUE | rgb);
        return new MapSprite(pixels, width, height, 0, 0, width, height);
    }

    /** A font whose only glyph, 'A', is a 2 x 3 block one pixel above the baseline, advancing 3. */
    static BitmapFont blockFont() {
        BitmapFont.Glyph[] glyphs = new BitmapFont.Glyph[128];
        glyphs['A'] = new BitmapFont.Glyph(new byte[]{1, 1, 1, 1, 1, 1}, 2, 3, 0, -3, 3);
        glyphs[' '] = new BitmapFont.Glyph(new byte[0], 0, 0, 0, 0, 2);
        return new BitmapFont(glyphs, 3, 4);
    }

    static LabelFonts blockLabelFonts() {
        return new LabelFonts(size -> blockFont());
    }
}
