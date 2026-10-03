package idlers.worldmap;

import java.util.List;

/**
 * The decoded surface map: one entry per tile in row-major arrays, row 0 at the north edge. Colours are RGB with 0
 * meaning nothing there; map scene and map function arrays hold the id plus one, 0 for none.
 */
public final class MapData {

    /** A place name at an absolute tile; "/" in the text breaks the line. {@code size} 0 small, 1 town, 2 kingdom. */
    public record Label(String text, int worldX, int worldY, int size) {
    }

    /** A map function icon's tile, kept apart for the overview's key flash. */
    public record FunctionPoint(int column, int row, int function) {
    }

    private final int originX;
    private final int originY;
    private final int width;
    private final int height;
    private final int[] ground;
    private final int[] overlay;
    private final byte[] overlayShape;
    private final byte[] wall;
    private final byte[] mapscene;
    private final byte[] function;
    private final List<FunctionPoint> functionPoints;
    private final List<Label> labels;

    MapData(int originX, int originY, int width, int height, int[] ground, int[] overlay, byte[] overlayShape,
            byte[] wall, byte[] mapscene, byte[] function, List<FunctionPoint> functionPoints, List<Label> labels) {
        this.originX = originX;
        this.originY = originY;
        this.width = width;
        this.height = height;
        this.ground = ground;
        this.overlay = overlay;
        this.overlayShape = overlayShape;
        this.wall = wall;
        this.mapscene = mapscene;
        this.function = function;
        this.functionPoints = List.copyOf(functionPoints);
        this.labels = List.copyOf(labels);
    }

    public int originX() {
        return originX;
    }

    public int originY() {
        return originY;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int column(int worldX) {
        return worldX - originX;
    }

    public int row(int worldY) {
        return originY + height - 1 - worldY;
    }

    public int worldX(int column) {
        return originX + column;
    }

    public int worldY(int row) {
        return originY + height - 1 - row;
    }

    public boolean contains(int column, int row) {
        return column >= 0 && row >= 0 && column < width && row < height;
    }

    public int ground(int column, int row) {
        return ground[row * width + column];
    }

    public int overlay(int column, int row) {
        return overlay[row * width + column];
    }

    /** {@code shape << 2 | rotation} of the tile's overlay. */
    public int overlayShape(int column, int row) {
        return overlayShape[row * width + column] & 0xff;
    }

    /** Wall kind 1-28 as the applet numbers them (see {@code MapRenderer}), 0 for none. */
    public int wall(int column, int row) {
        return wall[row * width + column] & 0xff;
    }

    public int mapscene(int column, int row) {
        return mapscene[row * width + column] & 0xff;
    }

    public int function(int column, int row) {
        return function[row * width + column] & 0xff;
    }

    public List<FunctionPoint> functionPoints() {
        return functionPoints;
    }

    public List<Label> labels() {
        return labels;
    }
}
