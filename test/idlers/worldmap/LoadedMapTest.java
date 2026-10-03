package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoadedMapTest {

    @Test
    void theOverviewIsAHundredPixelsHighWithTheMapsProportions() {
        assertEquals(118, LoadedMap.overviewWidth(TestMaps.map(1664, 1408).build()));
    }

    @Test
    void buildRendersTheWholeMapIntoTheOverview() {
        MapData data = TestMaps.map(400, 200).ground(0x123456).build();

        LoadedMap map = LoadedMap.build(data, new WorldMapAssets(new MapSprite[0], new MapSprite[0],
                TestMaps.blockLabelFonts()));

        assertEquals(List.of(200, 100, 0x123456), List.of(map.overview().width(), map.overview().height(),
                map.overview().pixel(199, 99)));
    }
}
