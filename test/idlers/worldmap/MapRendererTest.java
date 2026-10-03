package idlers.worldmap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static idlers.worldmap.MapRenderer.CORNER_RGB;
import static idlers.worldmap.MapRenderer.DOOR_RGB;
import static idlers.worldmap.MapRenderer.WALL_RGB;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapRendererTest {

    private static final int GROUND = 0x101010;
    private static final int GREEN = 0x00ff00;
    private static final int RED = 0xff0000;
    private static final int ICON = 0x0000ff;
    private static final MapSprite[] NO_SPRITES = new MapSprite[0];

    /** Renders a 4 x 4 tile map into 8 x 8 pixels: 2 pixels per tile. */
    private static Raster render(MapData data, MapSprite[] mapscenes) {
        Raster raster = Raster.blank(8, 8);
        MapRenderer.renderTerrain(raster, data, 0, 0, 4, 4, mapscenes);
        return raster;
    }

    @Test
    void fillsATileWithItsGroundColour() {
        Raster raster = render(TestMaps.map(4, 4).ground(GROUND).build(), NO_SPRITES);

        assertEquals(GROUND, raster.pixel(7, 7));
    }

    @Test
    void aPlainOverlayCoversTheWholeTile() {
        Raster raster = render(TestMaps.map(4, 4).ground(GROUND).overlay(1, 0, GREEN, 0, 0).build(), NO_SPRITES);

        assertEquals(List.of(GREEN, GREEN, GROUND), List.of(raster.pixel(2, 0), raster.pixel(3, 1), raster.pixel(4, 0)));
    }

    @Test
    void aShapedOverlaySharesTheTileWithTheGround() {
        Raster raster = render(TestMaps.map(4, 4).ground(GROUND).overlay(0, 1, RED, 1, 0).build(), NO_SPRITES);

        assertEquals(List.of(RED, GROUND), List.of(raster.pixel(0, 2), raster.pixel(1, 2)));
    }

    @Test
    void aShapedOverlayOnAOnePixelTileIsDrawnWhole() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).overlay(1, 0, RED, 1, 2).build();
        Raster raster = Raster.blank(4, 4);

        MapRenderer.renderTerrain(raster, data, 0, 0, 4, 4, NO_SPRITES);

        assertEquals(RED, raster.pixel(1, 0));
    }

    @Test
    void tilesOutsideTheMapAreLeftAlone() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).build();
        Raster raster = Raster.blank(8, 8);
        raster.clear(7);

        MapRenderer.renderTerrain(raster, data, -1, 0, 3, 4, NO_SPRITES);

        assertEquals(List.of(7, GROUND), List.of(raster.pixel(1, 0), raster.pixel(2, 0)));
    }

    @Test
    void tilesNarrowerThanAPixelAreSampled() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).overlay(1, 1, GREEN, 0, 0).build();
        Raster raster = Raster.blank(2, 2);

        MapRenderer.renderTerrain(raster, data, 0, 0, 4, 4, NO_SPRITES);

        assertEquals(List.of(GREEN, GROUND), List.of(raster.pixel(0, 0), raster.pixel(1, 1)));
    }

    @Test
    void zoomedOutBelowAPixelPerTileThereAreNoWallsOrIcons() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).wall(0, 0, 1).function(0, 0, 3).build();
        Raster raster = Raster.blank(2, 2);

        List<MapRenderer.VisibleFunction> functions = MapRenderer.renderTerrain(raster, data, 0, 0, 4, 4, NO_SPRITES);

        assertEquals(List.of(), functions);
        assertEquals(GROUND, raster.pixel(0, 0));
    }

    @Test
    void wallsStillShowWhereRowsAreThinnerThanAPixel() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).wall(0, 1, 1).build();
        Raster raster = Raster.blank(8, 2);

        MapRenderer.renderTerrain(raster, data, 0, 0, 4, 4, NO_SPRITES);

        assertEquals(WALL_RGB, raster.pixel(0, 0));
    }

    @Test
    void wallsOfTilesOutsideTheMapAreSkipped() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).wall(0, 0, 2).build();
        Raster raster = Raster.blank(8, 8);
        raster.clear(7);

        MapRenderer.renderTerrain(raster, data, -1, 0, 3, 4, NO_SPRITES);

        assertEquals(List.of(7, WALL_RGB), List.of(raster.pixel(0, 0), raster.pixel(2, 0)));
    }

    @Test
    void aShapedOverlayOnAOnePixelHighTileIsDrawnWhole() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).overlay(0, 0, RED, 1, 0).build();
        Raster raster = Raster.blank(8, 4);

        MapRenderer.renderTerrain(raster, data, 0, 0, 4, 4, NO_SPRITES);

        assertEquals(RED, raster.pixel(1, 0));
    }

    @Test
    void drawsTheWallsOfATile() {
        Raster raster = render(TestMaps.map(4, 4).ground(GROUND).wall(1, 1, 1).build(), NO_SPRITES);

        assertEquals(List.of(WALL_RGB, GROUND), List.of(raster.pixel(2, 3), raster.pixel(3, 3)));
    }

    @Test
    void drawsAMapSceneTwiceTheTileSizeCentredOnTheTileCorner() {
        MapSprite[] scenes = {null, TestMaps.solidSprite(4, 4, GREEN)};
        Raster raster = render(TestMaps.map(4, 4).ground(GROUND).mapscene(1, 1, 1).build(), scenes);

        assertEquals(List.of(GREEN, GREEN, GROUND), List.of(raster.pixel(1, 1), raster.pixel(4, 4), raster.pixel(5, 5)));
    }

    @Test
    void aMapSceneWithoutASpriteIsSkipped() {
        MapSprite[] scenes = {null};
        Raster raster = render(TestMaps.map(4, 4).ground(GROUND).mapscene(1, 1, 0).mapscene(2, 2, 5).build(), scenes);

        assertEquals(GROUND, raster.pixel(2, 2));
    }

    @Test
    void reportsEachIconAtTheCentreOfItsTile() {
        MapData data = TestMaps.map(4, 4).ground(GROUND).function(2, 1, 3).build();

        List<MapRenderer.VisibleFunction> functions = MapRenderer.renderTerrain(Raster.blank(8, 8), data, 0, 0, 4, 4,
                NO_SPRITES);

        assertEquals(List.of(new MapRenderer.VisibleFunction(3, 5, 3)), functions);
    }

    @ParameterizedTest(name = "wall {0} paints ({1}, {2})")
    @CsvSource(textBlock = """
            1, 0, 2, 0xcccccc
            2, 2, 0, 0xcccccc
            3, 3, 2, 0xcccccc
            4, 2, 3, 0xcccccc
            5, 0, 2, 0xcc0000
            6, 2, 0, 0xcc0000
            9, 0, 3, 0xffffff
            9, 3, 0, 0xcccccc
            10, 3, 3, 0xffffff
            10, 0, 0, 0xcccccc
            11, 3, 0, 0xffffff
            11, 0, 3, 0xcccccc
            12, 0, 0, 0xffffff
            12, 3, 3, 0xcccccc
            13, 3, 0, 0xcc0000
            17, 0, 0, 0xcccccc
            18, 3, 0, 0xcccccc
            19, 3, 3, 0xcccccc
            20, 0, 3, 0xcccccc
            21, 0, 0, 0xcc0000
            25, 1, 2, 0xcccccc
            26, 2, 2, 0xcccccc
            27, 1, 2, 0xcc0000
            28, 2, 2, 0xcc0000
            """)
    void drawWallPaintsTheEdgeOfItsKind(int kind, int x, int y, String rgb) {
        Raster raster = Raster.blank(4, 4);

        MapRenderer.drawWall(raster, kind, 0, 0, 4, 4);

        assertEquals(Integer.decode(rgb), raster.pixel(x, y));
    }

    @Test
    void anUnknownWallKindDrawsNothing() {
        Raster raster = Raster.blank(4, 4);

        MapRenderer.drawWall(raster, 0, 0, 0, 4, 4);

        assertEquals(0, raster.pixel(0, 0));
    }

    @Test
    void aStraightWallLeavesTheRestOfTheTile() {
        Raster raster = Raster.blank(4, 4);

        MapRenderer.drawWall(raster, 1, 0, 0, 4, 4);

        assertEquals(0, raster.pixel(1, 1));
    }

    @Test
    void onAOnePixelTileTheFarEdgeIsTheTileItself() {
        Raster raster = Raster.blank(2, 2);

        MapRenderer.drawWall(raster, 19, 1, 1, 1, 1);

        assertEquals(WALL_RGB, raster.pixel(1, 1));
    }

    @Test
    void drawFunctionsPutsEachIconCentredOnItsPoint() {
        MapSprite[] icons = {TestMaps.solidSprite(2, 2, ICON)};
        Raster raster = Raster.blank(12, 12);

        MapRenderer.drawFunctions(raster, List.of(new MapRenderer.VisibleFunction(0, 10, 10)), icons);

        assertEquals(List.of(ICON, 0), List.of(raster.pixel(3, 3), raster.pixel(5, 5)));
    }

    @Test
    void drawFunctionsSkipsIconsWithoutASprite() {
        MapSprite[] icons = {null};
        Raster raster = Raster.blank(12, 12);

        MapRenderer.drawFunctions(raster, List.of(new MapRenderer.VisibleFunction(0, 10, 10),
                new MapRenderer.VisibleFunction(4, 10, 10)), icons);

        assertEquals(0, raster.pixel(3, 3));
    }

    @Test
    void aLitFlashPutsAWhiteDotOnTheFlashedKind() {
        MapSprite[] icons = {TestMaps.solidSprite(2, 2, ICON)};
        Raster raster = Raster.blank(40, 40);

        MapRenderer.drawFlash(raster, List.of(new MapRenderer.VisibleFunction(0, 20, 20)), icons, 0, true);

        assertEquals(List.of(CORNER_RGB, Raster.blend(0xffff00, 0, 128)),
                List.of(raster.pixel(20, 20), raster.pixel(20, 7)));
    }

    @Test
    void anUnlitFlashShowsOnlyTheIcon() {
        MapSprite[] icons = {TestMaps.solidSprite(2, 2, ICON)};
        Raster raster = Raster.blank(40, 40);

        MapRenderer.drawFlash(raster, List.of(new MapRenderer.VisibleFunction(0, 20, 20)), icons, 0, false);

        assertEquals(List.of(ICON, 0), List.of(raster.pixel(13, 13), raster.pixel(20, 20)));
    }

    @Test
    void theFlashLeavesOtherKindsAlone() {
        MapSprite[] icons = {TestMaps.solidSprite(2, 2, ICON), TestMaps.solidSprite(2, 2, ICON)};
        Raster raster = Raster.blank(40, 40);

        MapRenderer.drawFlash(raster, List.of(new MapRenderer.VisibleFunction(1, 20, 20)), icons, 0, true);

        assertEquals(0, raster.pixel(20, 20));
    }

    /** A view of tiles 98-102 at 5 pixels per tile; a label at world (1100, 2100) lands on pixel (10, 10). */
    private static Raster labelled(String text, int size) {
        MapData data = TestMaps.map(200, 200).label(text, 1100, 2100, size).build();
        MapView view = new MapView(200, 200, 20, 20, ZoomLevel.P100);
        Raster raster = Raster.blank(20, 20);
        MapRenderer.drawLabels(raster, data, view, TestMaps.blockLabelFonts());
        return raster;
    }

    @Test
    void aLabelIsCentredOnItsPoint() {
        Raster raster = labelled("A", LabelFonts.TOWN);

        assertEquals(List.of(MapRenderer.LABEL_RGB, MapRenderer.LABEL_RGB, 0),
                List.of(raster.pixel(9, 8), raster.pixel(10, 10), raster.pixel(8, 8)));
    }

    @Test
    void kingdomLabelsAreOrange() {
        Raster raster = labelled("A", LabelFonts.KINGDOM);

        assertEquals(MapRenderer.KINGDOM_RGB, raster.pixel(9, 8));
    }

    @Test
    void aSlashBreaksTheLabelOverLinesAroundItsPoint() {
        Raster raster = labelled("A/A", LabelFonts.SMALL);

        assertEquals(List.of(MapRenderer.LABEL_RGB, MapRenderer.LABEL_RGB),
                List.of(raster.pixel(9, 6), raster.pixel(9, 12)));
    }
}
