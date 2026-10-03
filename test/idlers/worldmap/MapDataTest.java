package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapDataTest {

    private final MapData data = TestMaps.map(4, 3).build();

    @Test
    void containsEveryTileInsideTheMap() {
        assertTrue(data.contains(0, 0));
        assertTrue(data.contains(3, 2));
    }

    @Test
    void containsNoTileOutsideAnyEdge() {
        assertFalse(data.contains(-1, 0));
        assertFalse(data.contains(0, -1));
        assertFalse(data.contains(4, 0));
        assertFalse(data.contains(0, 3));
    }

    @Test
    void rowsCountFromTheNorthEdge() {
        assertEquals(List.of(2, 0, 2000, 2002), List.of(data.row(2000), data.row(2002), data.worldY(2), data.worldY(0)));
    }

    @Test
    void columnsCountFromTheWestEdge() {
        assertEquals(List.of(3, 1003), List.of(data.column(1003), data.worldX(3)));
    }
}
