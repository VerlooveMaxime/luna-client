package idlers.worldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Decodes the applet's {@code worldmap.jag} entries into {@link MapData}. Format notes and sources:
 * {@code .memory/codebase/world-map-data.md}.
 */
public final class MapDataDecoder {

    static final int REGION_SIZE = 64;
    static final int TILES_PER_REGION = REGION_SIZE * REGION_SIZE;
    static final int FIRST_MAPSCENE_CODE = 29;
    static final int FIRST_FUNCTION_CODE = 160;

    /** Divides the summed packed HSL of the 10x10 tiles around a tile (the applet's constant). */
    private static final double BLEND_SCALE = 8533.0;
    private static final int BLEND_RADIUS = 5;

    private MapDataDecoder() {
    }

    /** {@code entries} returns an entry's bytes by file name, or null when the archive has no such entry. */
    public static MapData decode(Function<String, byte[]> entries) {
        ByteReader size = reader(entries, "size.dat");
        int originX = size.u16();
        int originY = size.u16();
        int width = size.u16();
        int height = size.u16();
        Grid grid = new Grid(originX, originY, width, height);

        ByteReader floors = reader(entries, "floorcol.dat");
        int floorCount = floors.u16();
        int[] underlayHsl = new int[floorCount + 1];
        int[] overlayRgb = new int[floorCount + 1];
        for (int i = 1; i <= floorCount; i++) {
            underlayHsl[i] = floors.s32();
            overlayRgb[i] = floors.s32();
        }

        byte[] underlay = new byte[width * height];
        readUnderlay(reader(entries, "underlay.dat"), grid, underlay);
        int[] overlay = new int[width * height];
        byte[] overlayShape = new byte[width * height];
        readOverlay(reader(entries, "overlay.dat"), grid, overlayRgb, overlay, overlayShape);
        byte[] wall = new byte[width * height];
        byte[] mapscene = new byte[width * height];
        byte[] function = new byte[width * height];
        List<MapData.FunctionPoint> points = new ArrayList<>();
        readLocs(reader(entries, "loc.dat"), grid, wall, mapscene, function, points);

        return new MapData(originX, originY, width, height, blendGround(underlay, underlayHsl, width, height), overlay,
                overlayShape, wall, mapscene, function, points, readLabels(reader(entries, "labels.dat")));
    }

    private static ByteReader reader(Function<String, byte[]> entries, String name) {
        byte[] data = entries.apply(name);
        if (data == null) {
            throw new IllegalArgumentException("world map archive has no " + name);
        }
        return new ByteReader(data);
    }

    /**
     * Where a region's tiles go: the applet keeps only regions strictly inside the map, and its bytes run column by
     * column, south to north.
     */
    private record Grid(int originX, int originY, int width, int height) {

        int left(int regionX) {
            return regionX * REGION_SIZE - originX;
        }

        int bottom(int regionY) {
            return regionY * REGION_SIZE - originY;
        }

        boolean inside(int left, int bottom) {
            return left > 0 && bottom > 0 && left + REGION_SIZE < width && bottom + REGION_SIZE < height;
        }

        int index(int left, int bottom, int x, int z) {
            return (height - 1 - bottom - z) * width + left + x;
        }
    }

    private static void readUnderlay(ByteReader data, Grid grid, byte[] underlay) {
        while (data.remaining() > 0) {
            int left = grid.left(data.u8());
            int bottom = grid.bottom(data.u8());
            boolean inside = grid.inside(left, bottom);
            for (int x = 0; x < REGION_SIZE; x++) {
                for (int z = 0; z < REGION_SIZE; z++) {
                    byte floor = (byte) data.u8();
                    if (inside) {
                        underlay[grid.index(left, bottom, x, z)] = floor;
                    }
                }
            }
        }
    }

    private static void readOverlay(ByteReader data, Grid grid, int[] overlayRgb, int[] overlay, byte[] shapes) {
        while (data.remaining() > 0) {
            int left = grid.left(data.u8());
            int bottom = grid.bottom(data.u8());
            boolean inside = grid.inside(left, bottom);
            for (int x = 0; x < REGION_SIZE; x++) {
                for (int z = 0; z < REGION_SIZE; z++) {
                    int floor = data.u8();
                    if (floor != 0) {
                        int shape = data.u8();
                        if (inside) {
                            int index = grid.index(left, bottom, x, z);
                            overlay[index] = overlayRgb[floor];
                            shapes[index] = (byte) shape;
                        }
                    }
                }
            }
        }
    }

    private static void readLocs(ByteReader data, Grid grid, byte[] wall, byte[] mapscene, byte[] function,
                                 List<MapData.FunctionPoint> points) {
        while (data.remaining() > 0) {
            int left = grid.left(data.u8());
            int bottom = grid.bottom(data.u8());
            boolean inside = grid.inside(left, bottom);
            for (int x = 0; x < REGION_SIZE; x++) {
                for (int z = 0; z < REGION_SIZE; z++) {
                    for (int code = data.u8(); code != 0; code = data.u8()) {
                        if (inside) {
                            placeLoc(code, grid.index(left, bottom, x, z), grid.width(), wall, mapscene, function, points);
                        }
                    }
                }
            }
        }
    }

    private static void placeLoc(int code, int index, int width, byte[] wall, byte[] mapscene, byte[] function,
                                 List<MapData.FunctionPoint> points) {
        if (code < FIRST_MAPSCENE_CODE) {
            wall[index] = (byte) code;
        } else if (code < FIRST_FUNCTION_CODE) {
            mapscene[index] = (byte) (code - FIRST_MAPSCENE_CODE + 1);
        } else {
            function[index] = (byte) (code - FIRST_FUNCTION_CODE + 1);
            points.add(new MapData.FunctionPoint(index % width, index / width, code - FIRST_FUNCTION_CODE));
        }
    }

    private static List<MapData.Label> readLabels(ByteReader data) {
        int count = data.u16();
        List<MapData.Label> labels = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            labels.add(new MapData.Label(data.line(), data.u16(), data.u16(), data.u8()));
        }
        return labels;
    }

    /**
     * The applet's {@code GetBlendedGroundColour}: each underlay colour is the average of the packed HSL weights of
     * the tiles around it, built from running sums over columns, then rows. Tiles near the edges stay 0.
     */
    static int[] blendGround(byte[] underlay, int[] underlayHsl, int width, int height) {
        int[] ground = new int[width * height];
        int[] columnSums = new int[height];
        for (int x = BLEND_RADIUS; x < width - BLEND_RADIUS; x++) {
            for (int row = 0; row < height; row++) {
                columnSums[row] += underlayHsl[underlay[row * width + x + BLEND_RADIUS] & 0xff]
                        - underlayHsl[underlay[row * width + x - BLEND_RADIUS] & 0xff];
            }
            if (x > 2 * BLEND_RADIUS && x < width - 2 * BLEND_RADIUS) {
                int hue = 0;
                int saturation = 0;
                int lightness = 0;
                for (int row = BLEND_RADIUS; row < height - BLEND_RADIUS; row++) {
                    int next = columnSums[row + BLEND_RADIUS];
                    int previous = columnSums[row - BLEND_RADIUS];
                    hue += (next >> 20) - (previous >> 20);
                    saturation += ((next >> 10) & 0x3ff) - ((previous >> 10) & 0x3ff);
                    lightness += (next & 0x3ff) - (previous & 0x3ff);
                    if (lightness > 0) {
                        ground[row * width + x] = Hsl.toRgb(hue / BLEND_SCALE, saturation / BLEND_SCALE,
                                lightness / BLEND_SCALE);
                    }
                }
            }
        }
        return ground;
    }
}
