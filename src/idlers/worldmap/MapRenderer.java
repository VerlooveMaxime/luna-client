package idlers.worldmap;

import java.util.ArrayList;
import java.util.List;

/**
 * The applet's {@code RenderWorldmap}: draws the tiles from {@code left, top} (inclusive) to {@code right, bottom}
 * (exclusive) stretched over a whole raster. Walls, map scenes and icons only appear once a tile is at least a pixel
 * wide.
 */
public final class MapRenderer {

    /** A map function icon that landed on screen, centred on (x, y). */
    public record VisibleFunction(int function, int x, int y) {
    }

    static final int WALL_RGB = 0xcccccc;
    static final int DOOR_RGB = 0xcc0000;
    static final int CORNER_RGB = 0xffffff;
    static final int ICON_HALF = 7;
    static final int KINGDOM_RGB = 0xffaa00;
    static final int LABEL_RGB = 0xffffff;
    private static final int FLASH_HALO_RGB = 0xffff00;
    private static final int FLASH_HALO_RADIUS = 15;
    private static final int FLASH_DOT_RADIUS = 7;
    private static final int HALF_ALPHA = 128;
    private static final int SOLID_ALPHA = 256;

    private MapRenderer() {
    }

    public static List<VisibleFunction> renderTerrain(Raster raster, MapData data, int left, int top, int right,
                                                      int bottom, MapSprite[] mapscenes) {
        Spans columns = Spans.of(raster.width(), right - left);
        Spans rows = Spans.of(raster.height(), bottom - top);
        for (int x = 0; x < columns.count(); x++) {
            for (int y = 0; y < rows.count(); y++) {
                if (columns.length(x) > 0 && rows.length(y) > 0 && data.contains(x + left, y + top)) {
                    drawFloor(raster, data, x + left, y + top, columns.start(x), rows.start(y), columns.length(x),
                            rows.length(y));
                }
            }
        }
        List<VisibleFunction> functions = new ArrayList<>();
        if (right - left > raster.width()) {
            return functions;
        }
        // From here every column is at least a pixel wide; rows may still be thinner if the raster is very flat.
        for (int x = 0; x < columns.count(); x++) {
            for (int y = 0; y < rows.count(); y++) {
                if (rows.length(y) > 0 && data.contains(x + left, y + top)) {
                    drawLocs(raster, data, x + left, y + top, columns.start(x), rows.start(y), columns.length(x),
                            rows.length(y), mapscenes, functions);
                }
            }
        }
        return functions;
    }

    /** The pixel span each tile gets, in the applet's 16.16 fixed point. */
    private record Spans(int count, int ratio) {

        static Spans of(int pixels, int tiles) {
            return new Spans(tiles, (pixels << 16) / tiles);
        }

        int start(int tile) {
            return ratio * tile >> 16;
        }

        int length(int tile) {
            return (ratio * (tile + 1) >> 16) - start(tile);
        }
    }

    private static void drawFloor(Raster raster, MapData data, int column, int row, int x, int y, int width,
                                  int height) {
        int overlay = data.overlay(column, row);
        if (overlay == 0) {
            raster.fillRect(x, y, width, height, data.ground(column, row));
            return;
        }
        int info = data.overlayShape(column, row);
        int shape = info >> 2;
        if (shape == 0 || width <= 1 || height <= 1) {
            raster.fillRect(x, y, width, height, overlay);
        } else {
            OverlayShapes.fill(raster, x, y, width, height, data.ground(column, row), overlay, shape, info & 3);
        }
    }

    private static void drawLocs(Raster raster, MapData data, int column, int row, int x, int y, int width,
                                 int height, MapSprite[] mapscenes, List<VisibleFunction> functions) {
        int wall = data.wall(column, row);
        if (wall != 0) {
            drawWall(raster, wall, x, y, width, height);
        }
        int mapscene = data.mapscene(column, row);
        if (mapscene != 0 && mapscene <= mapscenes.length && mapscenes[mapscene - 1] != null) {
            raster.drawSpriteScaled(mapscenes[mapscene - 1], x - width / 2, y - height / 2, width * 2, height * 2);
        }
        int function = data.function(column, row);
        if (function != 0) {
            functions.add(new VisibleFunction(function - 1, x + width / 2, y + height / 2));
        }
    }

    /**
     * Wall kinds: 1-4 west, north, east, south edge; 9-12 square corners (NW, NE, SE, SW); 17-20 a corner pixel for
     * wall decoration; 25-26 diagonals. Adding 4 (or 2 for diagonals) marks an interactive one, drawn red. The
     * original applet drew nothing for 27-28 (interactive diagonals); LostCity's fix draws them, kept here.
     */
    static void drawWall(Raster raster, int kind, int x, int y, int width, int height) {
        int rgb = WALL_RGB;
        int wall = kind;
        if ((wall >= 5 && wall <= 8) || (wall >= 13 && wall <= 16) || (wall >= 21 && wall <= 24)) {
            rgb = DOOR_RGB;
            wall -= 4;
        } else if (wall == 27 || wall == 28) {
            rgb = DOOR_RGB;
            wall -= 2;
        }
        int edgeX = width == 1 ? x : x + width - 1;
        int edgeY = height == 1 ? y : y + height - 1;
        switch (wall) {
            case 1 -> raster.vline(x, y, height, rgb);
            case 2 -> raster.hline(x, y, width, rgb);
            case 3 -> raster.vline(edgeX, y, height, rgb);
            case 4 -> raster.hline(x, edgeY, width, rgb);
            case 9 -> corner(raster, x, y, x, y, width, height, rgb);
            case 10 -> corner(raster, edgeX, y, x, y, width, height, rgb);
            case 11 -> corner(raster, edgeX, y, x, edgeY, width, height, rgb);
            case 12 -> corner(raster, x, y, x, edgeY, width, height, rgb);
            case 17 -> raster.plot(x, y, rgb);
            case 18 -> raster.plot(edgeX, y, rgb);
            case 19 -> raster.plot(edgeX, edgeY, rgb);
            case 20 -> raster.plot(x, edgeY, rgb);
            case 25 -> diagonal(raster, x, edgeY, -1, height, rgb);
            case 26 -> diagonal(raster, x, y, 1, height, rgb);
        }
    }

    private static void corner(Raster raster, int verticalX, int verticalY, int horizontalX, int horizontalY,
                               int width, int height, int rgb) {
        raster.vline(verticalX, verticalY, height, CORNER_RGB);
        raster.hline(horizontalX, horizontalY, width, rgb);
    }

    private static void diagonal(Raster raster, int x, int y, int stepY, int length, int rgb) {
        for (int i = 0; i < length; i++) {
            raster.plot(x + i, y + i * stepY, rgb);
        }
    }

    public static void drawFunctions(Raster raster, List<VisibleFunction> functions, MapSprite[] sprites) {
        for (VisibleFunction function : functions) {
            drawIcon(raster, function, sprites);
        }
    }

    /** The key's flash: every icon of {@code function} under a yellow halo and a white dot, while {@code lit}. */
    public static void drawFlash(Raster raster, List<VisibleFunction> functions, MapSprite[] sprites, int function,
                                 boolean lit) {
        for (VisibleFunction visible : functions) {
            if (visible.function() == function) {
                drawIcon(raster, visible, sprites);
                if (lit) {
                    raster.fillCircle(visible.x(), visible.y(), FLASH_HALO_RADIUS, FLASH_HALO_RGB, HALF_ALPHA);
                    raster.fillCircle(visible.x(), visible.y(), FLASH_DOT_RADIUS, CORNER_RGB, SOLID_ALPHA);
                }
            }
        }
    }

    private static void drawIcon(Raster raster, VisibleFunction function, MapSprite[] sprites) {
        if (function.function() < sprites.length && sprites[function.function()] != null) {
            raster.drawSprite(sprites[function.function()], function.x() - ICON_HALF, function.y() - ICON_HALF);
        }
    }

    /** Place names, centred on their point, in the font the label's size and the zoom call for. */
    public static void drawLabels(Raster raster, MapData data, MapView view, LabelFonts fonts) {
        for (MapData.Label label : data.labels()) {
            BitmapFont font = fonts.font(label.size(), view.target());
            String[] lines = label.text().split("/", -1);
            int x = view.pixelX(data.column(label.worldX()));
            int baseline = view.pixelY(data.row(label.worldY()) + 1) - font.lineHeight() * (lines.length - 1) / 2
                    + font.ascent() / 2;
            int rgb = label.size() == LabelFonts.KINGDOM ? KINGDOM_RGB : LABEL_RGB;
            for (String line : lines) {
                font.drawCentred(raster, line, x, baseline, rgb, true);
                baseline += font.lineHeight();
            }
        }
    }
}
