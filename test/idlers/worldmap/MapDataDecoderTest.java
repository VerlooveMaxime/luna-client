package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MapDataDecoderTest {

    /** Floor 1 underlay weights: hue 22, saturation 71, lightness 20 per tile. */
    private static final int GRASS_HSL = 22 << 20 | 71 << 10 | 20;
    private static final int ROAD_RGB = 0x504746;
    /** Region (1, 1) local tile (3, 5) is absolute (67, 69): column 67, row 192 - 1 - 69. */
    private static final int COLUMN = 67;
    private static final int ROW = 122;

    private static Map<String, byte[]> archive() {
        Map<String, byte[]> entries = new HashMap<>();
        entries.put("size.dat", new Bytes().u16(0).u16(0).u16(192).u16(192).toArray());
        entries.put("floorcol.dat", new Bytes().u16(2).s32(GRASS_HSL).s32(0x123456).s32(0).s32(ROAD_RGB).toArray());
        entries.put("underlay.dat", new Bytes()
                .u8(0, 0).repeat(1, 4096)
                .u8(1, 1).repeat(1, 4096).toArray());
        entries.put("overlay.dat", new Bytes()
                .u8(0, 0).u8(2, 7).repeat(0, 4095)
                .u8(1, 1).repeat(0, 3 * 64 + 5).u8(2, 7).repeat(0, 4096 - 3 * 64 - 6).toArray());
        entries.put("loc.dat", new Bytes()
                .u8(0, 0).u8(2, 0).repeat(0, 4095)
                .u8(1, 1).repeat(0, 3 * 64 + 5).u8(2, 29 + 4, 160 + 6, 0).repeat(0, 4096 - 3 * 64 - 6)
                .u8(2, 0).u8(2, 0).repeat(0, 4095)
                .u8(2, 2).u8(2, 0).repeat(0, 4095)
                .u8(1, 2).u8(2, 0).repeat(0, 4095).toArray());
        entries.put("labels.dat", new Bytes().u16(2)
                .line("Lumbridge").u16(3222).u16(3218).u8(1)
                .line("Kingdom of/Misthalin").u16(3217).u16(3321).u8(2).toArray());
        return entries;
    }

    private static MapData decoded() {
        return MapDataDecoder.decode(archive()::get);
    }

    @Test
    void readsTheMapBounds() {
        MapData data = decoded();

        assertEquals(List.of(0, 0, 192, 192), List.of(data.originX(), data.originY(), data.width(), data.height()));
    }

    @Test
    void placesAnOverlayAtItsTileWithItsColourAndShape() {
        MapData data = decoded();

        assertEquals(ROAD_RGB, data.overlay(COLUMN, ROW));
        assertEquals(7, data.overlayShape(COLUMN, ROW));
    }

    @Test
    void placesWallsMapScenesAndFunctionsAtTheirTile() {
        MapData data = decoded();

        assertEquals(List.of(2, 5, 7), List.of(data.wall(COLUMN, ROW), data.mapscene(COLUMN, ROW),
                data.function(COLUMN, ROW)));
    }

    @Test
    void recordsEachFunctionTileForTheOverview() {
        assertEquals(List.of(new MapData.FunctionPoint(COLUMN, ROW, 6)), decoded().functionPoints());
    }

    @Test
    void skipsRegionsTouchingTheMapEdgeButReadsPastThem() {
        MapData data = decoded();

        assertEquals(0, data.wall(0, 191));
        assertEquals(0, data.overlay(0, 191));
    }

    @Test
    void skipsRegionsTouchingAnyEdge() {
        MapData data = decoded();

        assertEquals(List.of(0, 0, 0), List.of(data.wall(128, 191), data.wall(128, 63), data.wall(64, 63)));
    }

    @Test
    void readsTheLabels() {
        assertEquals(List.of(new MapData.Label("Lumbridge", 3222, 3218, 1),
                new MapData.Label("Kingdom of/Misthalin", 3217, 3321, 2)), decoded().labels());
    }

    @Test
    void blendsTheGroundFromTheTenByTenTilesAround() {
        assertEquals(Hsl.toRgb(2200 / 8533.0, 7100 / 8533.0, 2000 / 8533.0), decoded().ground(96, 95));
    }

    @Test
    void leavesTheGroundBlackNearTheEdge() {
        assertEquals(0, decoded().ground(10, 95));
    }

    @Test
    void leavesTheGroundBlackWhereThereIsNoUnderlay() {
        assertEquals(0, decoded().ground(160, 95));
    }

    @Test
    void failsWithTheNameOfAMissingEntry() {
        Map<String, byte[]> entries = archive();
        entries.remove("loc.dat");

        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> MapDataDecoder.decode(entries::get));
        assertEquals("world map archive has no loc.dat", failure.getMessage());
    }
}
