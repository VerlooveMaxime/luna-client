package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The window over a 512 x 334 game view, on a 500 x 500 tile map with origin (1000, 2000) whose overview is 100 x 100
 * at (408, 212). Opened on world (1250, 2250), the centre tile (column 250, row 249), at 50%: tiles 122-378 by
 * 166-332, two pixels per tile.
 */
class WorldMapWindowTest {

    private static final int GROUND = 0x101010;
    private static final int ICON = 0x0000ff;
    private static final int CENTRE_X = 1250;
    private static final int CENTRE_Y = 2250;
    private static final int OFF_MAP = 0;

    private static LoadedMap loadedMap() {
        MapData data = TestMaps.map(500, 500).ground(GROUND).function(100, 100, 3).function(150, 150, 2)
                .label("A", CENTRE_X, CENTRE_Y, LabelFonts.TOWN).build();
        MapSprite icon = TestMaps.solidSprite(2, 2, ICON);
        return LoadedMap.build(data, new WorldMapAssets(new MapSprite[0], new MapSprite[]{icon, null, icon, icon},
                TestMaps.blockLabelFonts()));
    }

    private static WorldMapWindow window() {
        return new WorldMapWindow(512, 334, TestMaps.blockFont());
    }

    private static WorldMapWindow opened() {
        WorldMapWindow window = window();
        window.loaded(loadedMap());
        window.open(CENTRE_X, CENTRE_Y);
        return window;
    }

    private static Raster drawn(WorldMapWindow window, int playerX, int playerY) {
        Raster raster = Raster.blank(512, 334);
        raster.clear(7);
        window.draw(raster, playerX, playerY);
        return raster;
    }

    @Test
    void startsClosedAndTakesNoClicks() {
        WorldMapWindow window = window();

        assertFalse(window.isOpen());
        assertFalse(window.press(100, 100, true));
    }

    @Test
    void openCentresOnTheRequestedTile() {
        WorldMapWindow window = opened();

        assertTrue(window.isOpen() && window.isLoaded());
        assertEquals(List.of(250, 249), List.of(window.view().focusColumn(), window.view().focusRow()));
    }

    @Test
    void aMapThatArrivesAfterOpeningIsCentredOnTheNextTick() {
        WorldMapWindow window = window();
        window.open(CENTRE_X, CENTRE_Y);
        window.loaded(loadedMap());

        window.tick();

        assertEquals(List.of(250, 249), List.of(window.view().focusColumn(), window.view().focusRow()));
    }

    @Test
    void beforeTheMapArrivesTheViewIsBlackWithACloseButton() {
        WorldMapWindow window = window();
        window.open(CENTRE_X, CENTRE_Y);

        Raster raster = drawn(window, CENTRE_X, CENTRE_Y);

        assertFalse(window.isLoaded());
        assertEquals(List.of(0, WorldMapWindow.BUTTON_FILL), List.of(raster.pixel(100, 100), raster.pixel(470, 10)));
    }

    @Test
    void aLoadingFailureIsShownInTheMiddle() {
        WorldMapWindow window = window();
        window.open(CENTRE_X, CENTRE_Y);
        window.failed("A");

        Raster raster = drawn(window, CENTRE_X, CENTRE_Y);

        assertEquals(WorldMapWindow.TEXT_RGB, raster.pixel(255, 164));
    }

    @Test
    void theCloseButtonWorksBeforeTheMapArrives() {
        WorldMapWindow window = window();
        window.open(CENTRE_X, CENTRE_Y);

        window.press(470, 10, true);

        assertFalse(window.isOpen());
    }

    @Test
    void clicksOnTheViewBeforeTheMapArrivesAreTakenAndIgnored() {
        WorldMapWindow window = window();
        window.open(CENTRE_X, CENTRE_Y);

        assertTrue(window.press(200, 100, true));
        assertTrue(window.isOpen());
    }

    @Test
    void theMouseAndTicksBeforeTheMapArrivesDoNothing() {
        WorldMapWindow window = window();
        window.open(CENTRE_X, CENTRE_Y);

        window.mouse(200, 100, true);
        window.tick();

        assertFalse(window.isLoaded());
    }

    @Test
    void clicksOutsideTheViewAreLeftToTheClient() {
        assertFalse(opened().press(600, 10, true));
        assertFalse(opened().press(10, -1, true));
        assertFalse(opened().press(-1, 10, true));
        assertFalse(opened().press(10, 334, true));
    }

    @Test
    void aRightClickIsTakenButDoesNothing() {
        WorldMapWindow window = opened();

        assertTrue(window.press(200, 100, false));
        assertFalse(window.view().dragging());
    }

    @Test
    void theCloseButtonCloses() {
        WorldMapWindow window = opened();

        window.press(470, 10, true);

        assertFalse(window.isOpen());
    }

    @Test
    void closingEndsADrag() {
        WorldMapWindow window = opened();
        window.press(200, 100, true);

        window.close();

        assertFalse(window.view().dragging());
    }

    @Test
    void aZoomButtonSetsTheZoom() {
        WorldMapWindow window = opened();

        window.press(160, 310, true);

        assertEquals(ZoomLevel.P37, window.view().target());
    }

    @Test
    void ticksEaseTheZoomTowardsTheButtonsLevel() {
        WorldMapWindow window = opened();
        window.press(320, 310, true);

        window.tick();

        assertTrue(window.view().zoom() > ZoomLevel.P50.factor());
    }

    @Test
    void theKeyButtonTogglesTheKey() {
        WorldMapWindow window = opened();

        window.press(10, 320, true);
        boolean shownAfterFirstClick = window.keyShown();
        window.press(10, 320, true);

        assertTrue(shownAfterFirstClick);
        assertFalse(window.keyShown());
    }

    @Test
    void theOverviewButtonTogglesTheOverview() {
        WorldMapWindow window = opened();

        window.press(450, 320, true);

        assertTrue(window.overviewShown());
    }

    @Test
    void theOverviewButtonHidesTheOverviewAgain() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);

        window.press(450, 320, true);

        assertFalse(window.overviewShown());
    }

    @Test
    void anOpenKeyLeavesClicksElsewhereToTheMap() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);

        window.press(300, 100, true);

        assertTrue(window.view().dragging());
    }

    @Test
    void anOpenOverviewLeavesClicksElsewhereToTheMap() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);

        window.press(300, 100, true);

        assertTrue(window.view().dragging());
    }

    @Test
    void anOverviewDragStopsJumpingOutsideTheOverview() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);
        window.press(458, 262, true);

        window.mouse(300, 100, true);

        assertEquals(List.of(250, 250), List.of(window.view().focusColumn(), window.view().focusRow()));
    }

    @Test
    void theTilesAreRenderedAgainOnlyWhenTheViewMoves() {
        WorldMapWindow window = opened();

        boolean first = window.renderTerrainIfMoved();
        boolean unchanged = window.renderTerrainIfMoved();
        window.press(200, 100, true);
        window.mouse(160, 120, true);

        assertEquals(List.of(true, false, true), List.of(first, unchanged, window.renderTerrainIfMoved()));
    }

    @Test
    void theLastKeyPageLeavesRowsPastTheLastNameEmpty() {
        WorldMapWindow window = new WorldMapWindow(512, 300, TestMaps.blockFont());
        window.loaded(loadedMap());
        window.open(CENTRE_X, CENTRE_Y);
        window.press(10, 285, true);
        turnToLastPage(window);

        Raster raster = Raster.blank(512, 300);
        window.draw(raster, OFF_MAP, OFF_MAP);

        assertEquals(List.of(52, WorldMapWindow.PANEL_FILL), List.of(window.key().firstRow(), raster.pixel(7, 161)));
    }

    @Test
    void theKeysPageButtonsTurnThePages() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);

        window.press(10, 300, true);
        window.press(10, 300, true);
        window.press(10, 10, true);

        assertEquals(15, window.key().firstRow());
    }

    @Test
    void clickingAKeyNameFlashesItsKind() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);

        window.press(10, 25 + 3 * 17 + 5, true);

        assertEquals(3, window.key().flashing());
    }

    @Test
    void clickingTheKeyBetweenRowsFlashesNothing() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);

        window.press(10, 23, true);

        assertEquals(-1, window.key().flashing());
    }

    @Test
    void aHiddenKeyDoesNotCatchClicks() {
        WorldMapWindow window = opened();

        window.press(10, 30, true);

        assertTrue(window.view().dragging());
    }

    @Test
    void clickingTheOverviewJumpsThere() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);

        window.press(458, 262, true);

        assertEquals(List.of(250, 250), List.of(window.view().focusColumn(), window.view().focusRow()));
    }

    @Test
    void draggingOnTheOverviewKeepsJumping() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);
        window.press(458, 262, true);

        window.mouse(418, 222, true);

        assertEquals(List.of(128 + 48, 83 + 48), List.of(window.view().focusColumn(), window.view().focusRow()));
    }

    @Test
    void releasingTheButtonEndsTheOverviewDrag() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);
        window.press(458, 262, true);
        window.mouse(458, 262, false);

        window.mouse(418, 222, true);

        assertEquals(250, window.view().focusColumn());
    }

    @Test
    void aHiddenOverviewDoesNotCatchClicks() {
        WorldMapWindow window = opened();

        window.press(458, 262, true);

        assertTrue(window.view().dragging());
    }

    @Test
    void draggingTheMapPansIt() {
        WorldMapWindow window = opened();
        window.press(200, 100, true);

        window.mouse(160, 120, true);

        assertEquals(List.of(270, 239), List.of(window.view().focusColumn(), window.view().focusRow()));
    }

    @Test
    void releasingTheButtonEndsTheDrag() {
        WorldMapWindow window = opened();
        window.press(200, 100, true);
        window.mouse(200, 100, false);

        window.mouse(160, 120, true);

        assertEquals(250, window.view().focusColumn());
    }

    @Test
    void drawsThePlayerAsAWhiteSquareWithABlackBorder() {
        Raster raster = drawn(opened(), CENTRE_X, CENTRE_Y);

        assertEquals(List.of(WorldMapWindow.PLAYER_RGB, 0), List.of(raster.pixel(257, 168), raster.pixel(254, 168)));
    }

    @Test
    void aPlayerOffTheMapIsNotDrawn() {
        Raster raster = drawn(opened(), OFF_MAP, OFF_MAP);

        assertEquals(GROUND, raster.pixel(260, 172));
    }

    @Test
    void theChosenZoomButtonIsRed() {
        Raster raster = drawn(opened(), OFF_MAP, OFF_MAP);

        assertEquals(List.of(WorldMapWindow.ACTIVE_FILL, WorldMapWindow.BUTTON_FILL),
                List.of(raster.pixel(215, 305), raster.pixel(160, 305)));
    }

    @Test
    void labelsAreDrawnOnceTheZoomHasSettled() {
        Raster raster = drawn(opened(), OFF_MAP, OFF_MAP);

        assertEquals(MapRenderer.LABEL_RGB, raster.pixel(255, 167));
    }

    @Test
    void labelsWaitWhileTheZoomMoves() {
        WorldMapWindow window = opened();
        window.press(320, 310, true);
        window.tick();

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertNotEquals(MapRenderer.LABEL_RGB, raster.pixel(255, 167));
    }

    @Test
    void theKeyShowsEachKindsIcon() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertEquals(List.of(ICON, WorldMapWindow.PANEL_FILL), List.of(raster.pixel(7, 25), raster.pixel(100, 280)));
    }

    @Test
    void theKeyNameUnderTheMouseIsHighlighted() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);
        window.mouse(30, 25 + 3 * 17 + 5, false);

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertEquals(WorldMapWindow.KEY_HOVER_RGB, raster.pixel(24, 86));
    }

    @Test
    void theFlashingKeyNameIsYellowWhileLit() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);
        window.press(30, 25 + 3 * 17 + 5, true);
        window.mouse(300, 100, false);

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertEquals(WorldMapWindow.KEY_FLASH_RGB, raster.pixel(24, 86));
    }

    @Test
    void theOverviewShowsTheWholeMapWithTheViewBoxed() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertEquals(List.of(GROUND, 0, WorldMapWindow.VIEW_BOX_RGB),
                List.of(raster.pixel(410, 214), raster.pixel(408, 212), raster.pixel(432, 250)));
    }

    @Test
    void theOverviewMarksTheFlashingKind() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);
        window.press(10, 320, true);
        window.press(30, 25 + 3 * 17 + 5, true);

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertEquals(WorldMapWindow.KEY_FLASH_RGB, raster.pixel(428, 232));
    }

    @Test
    void theOverviewMarksNothingWhenTheFlashIsDark() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);
        window.press(10, 320, true);
        window.press(30, 25 + 3 * 17 + 5, true);
        tick(window, 5);

        Raster raster = drawn(window, OFF_MAP, OFF_MAP);

        assertEquals(GROUND, raster.pixel(428, 232));
    }

    @Test
    void theHoveredTileIsNamedInWorldCoordinates() {
        WorldMapWindow window = opened();

        window.mouse(256, 167, false);

        assertEquals("1250, 2250", window.hoveredTileText());
    }

    @Test
    void noTileIsNamedOutsideTheView() {
        WorldMapWindow window = opened();

        window.mouse(-1, 100, false);

        assertNull(window.hoveredTileText());
    }

    @Test
    void noTileIsNamedOverAButton() {
        WorldMapWindow window = opened();

        window.mouse(470, 10, false);

        assertNull(window.hoveredTileText());
    }

    @Test
    void noTileIsNamedOverTheToggles() {
        WorldMapWindow window = opened();

        assertNull(hovered(window, 10, 320));
        assertNull(hovered(window, 450, 320));
        assertNull(hovered(window, 160, 310));
    }

    @Test
    void anOpenOverviewStillLetsTheRestOfTheMapBeNamed() {
        WorldMapWindow window = opened();
        window.press(450, 320, true);

        assertEquals("1250, 2250", hovered(window, 256, 167));
    }

    @Test
    void noTileIsNamedOverAnOpenPanel() {
        WorldMapWindow window = opened();
        window.press(10, 320, true);
        window.press(450, 320, true);

        window.mouse(50, 100, false);
        String overKey = window.hoveredTileText();
        window.mouse(450, 250, false);

        assertNull(overKey);
        assertNull(window.hoveredTileText());
    }

    @Test
    void noTileIsNamedOffTheEdgeOfAMapSmallerThanTheView() {
        WorldMapWindow window = window();
        MapData small = TestMaps.map(100, 100).build();
        window.loaded(LoadedMap.build(small, new WorldMapAssets(new MapSprite[0], new MapSprite[0],
                TestMaps.blockLabelFonts())));
        window.open(1050, 2050);

        window.mouse(200, 30, false);

        assertNull(window.hoveredTileText());
    }

    private static WorldMapWindow openedToPick() {
        WorldMapWindow window = window();
        window.loaded(loadedMap());
        window.openToPick(CENTRE_X, CENTRE_Y);
        return window;
    }

    private static void click(WorldMapWindow window, int pressX, int pressY, int releaseX, int releaseY) {
        window.press(pressX, pressY, true);
        window.mouse(releaseX, releaseY, false);
    }

    @Test
    void aStillClickWhilePickingPicksTheTileAndCloses() {
        WorldMapWindow window = openedToPick();

        click(window, 256, 167, 256, 167);

        assertEquals(new PickedTile(CENTRE_X, CENTRE_Y), window.takePicked());
        assertFalse(window.isOpen());
    }

    @Test
    void aClickThatWobblesAFewPixelsPicksTheTileItWasPressedOn() {
        WorldMapWindow window = openedToPick();

        click(window, 256, 167, 259, 164);

        assertEquals(new PickedTile(CENTRE_X, CENTRE_Y), window.takePicked());
    }

    @Test
    void movingFourPixelsAcrossIsADragAndPicksNothing() {
        WorldMapWindow window = openedToPick();

        click(window, 256, 167, 260, 167);

        assertNull(window.takePicked());
        assertTrue(window.isOpen());
    }

    @Test
    void movingFourPixelsDownIsADragAndPicksNothing() {
        WorldMapWindow window = openedToPick();

        click(window, 256, 167, 256, 171);

        assertNull(window.takePicked());
    }

    @Test
    void aPickIsHandedOverOnce() {
        WorldMapWindow window = openedToPick();
        click(window, 256, 167, 256, 167);
        window.takePicked();

        assertNull(window.takePicked());
    }

    @Test
    void aClickOnAButtonWhilePickingPicksNothing() {
        WorldMapWindow window = openedToPick();

        click(window, 470, 10, 470, 10);

        assertNull(window.takePicked());
    }

    @Test
    void openedToLookAClickPicksNothing() {
        WorldMapWindow window = opened();

        click(window, 256, 167, 256, 167);

        assertNull(window.takePicked());
        assertTrue(window.isOpen());
    }

    @Test
    void reopenedToLookAfterBeingOpenedToPickAClickPicksNothing() {
        WorldMapWindow window = openedToPick();
        window.open(CENTRE_X, CENTRE_Y);

        click(window, 256, 167, 256, 167);

        assertNull(window.takePicked());
    }

    @Test
    void closingBetweenPressAndReleaseCancelsThePick() {
        WorldMapWindow window = openedToPick();
        window.press(256, 167, true);
        window.close();

        window.mouse(256, 167, false);

        assertNull(window.takePicked());
    }

    @Test
    void aClickOffTheEdgeOfASmallMapPicksNothing() {
        WorldMapWindow window = window();
        window.loaded(LoadedMap.build(TestMaps.map(100, 100).build(), new WorldMapAssets(new MapSprite[0],
                new MapSprite[0], TestMaps.blockLabelFonts())));
        window.openToPick(1050, 2050);

        click(window, 200, 30, 200, 30);

        assertNull(window.takePicked());
        assertTrue(window.isOpen());
    }

    @Test
    void whilePickingTheHintTakesTheTopLine() {
        WorldMapWindow window = dotFontWindow();
        window.openToPick(CENTRE_X, CENTRE_Y);
        window.mouse(-1, -1, false);

        Raster raster = drawn(window, CENTRE_X, CENTRE_Y);

        assertEquals(List.of(true, false), List.of(textOnRow(raster, 15), textOnRow(raster, 19)));
    }

    @Test
    void whilePickingTheHoveredTileMovesBelowTheHint() {
        WorldMapWindow window = dotFontWindow();
        window.openToPick(CENTRE_X, CENTRE_Y);
        window.mouse(256, 167, false);

        assertTrue(textOnRow(drawn(window, CENTRE_X, CENTRE_Y), 19));
    }

    @Test
    void openedToLookNoHintIsShown() {
        WorldMapWindow window = dotFontWindow();
        window.open(CENTRE_X, CENTRE_Y);
        window.mouse(-1, -1, false);

        assertFalse(textOnRow(drawn(window, CENTRE_X, CENTRE_Y), 15));
    }

    /** A loaded window whose font draws every character as a one-pixel dot just above the baseline. */
    private static WorldMapWindow dotFontWindow() {
        BitmapFont.Glyph[] glyphs = new BitmapFont.Glyph[128];
        java.util.Arrays.fill(glyphs, new BitmapFont.Glyph(new byte[]{1}, 1, 1, 0, -1, 2));
        WorldMapWindow window = new WorldMapWindow(512, 334, new BitmapFont(glyphs, 3, 4));
        window.loaded(loadedMap());
        return window;
    }

    /** Whether text is drawn on a row of the middle of the view, clear of the buttons. */
    private static boolean textOnRow(Raster raster, int row) {
        for (int x = 150; x < 362; x++) {
            if (raster.pixel(x, row) == WorldMapWindow.TEXT_RGB) {
                return true;
            }
        }
        return false;
    }

    private static String hovered(WorldMapWindow window, int x, int y) {
        window.mouse(x, y, false);
        return window.hoveredTileText();
    }

    /** Turns the 13-row key of a 300-pixel-high window to its last page, 52-59; Next page is at (10, 265). */
    private static void turnToLastPage(WorldMapWindow window) {
        for (int page = 0; page < 4; page++) {
            window.press(10, 265, true);
        }
    }

    private static void tick(WorldMapWindow window, int ticks) {
        for (int i = 0; i < ticks; i++) {
            window.tick();
        }
    }
}
