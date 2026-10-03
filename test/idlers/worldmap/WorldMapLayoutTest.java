package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldMapLayoutTest {

    /** The game view with the 2006 map's overview (1664 x 1408 tiles at 100 pixels high). */
    private final WorldMapLayout layout = WorldMapLayout.of(512, 334, 118, 100);

    @Test
    void closeSitsTopRight() {
        assertEquals(new WorldMapLayout.Rect(458, 4, 50, 18), layout.close());
    }

    @Test
    void theKeyToggleAndPanelSitBottomLeft() {
        assertEquals(new WorldMapLayout.Rect(4, 312, 140, 18), layout.keyToggle());
        assertEquals(new WorldMapLayout.Rect(4, 4, 140, 308), layout.keyPanel());
    }

    @Test
    void thePageButtonsCapTheKeyPanel() {
        assertEquals(List.of(4, 294), List.of(layout.previousPage().y(), layout.nextPage().y()));
    }

    @Test
    void theOverviewSitsAboveItsToggleBottomRight() {
        assertEquals(new WorldMapLayout.Rect(390, 312, 118, 18), layout.overviewToggle());
        assertEquals(new WorldMapLayout.Rect(390, 212, 118, 100), layout.overview());
    }

    @Test
    void theZoomButtonsRunBetweenKeyAndOverview() {
        assertEquals(new WorldMapLayout.Rect(156, 300, 50, 30), layout.zoomButtons().get(0));
        assertEquals(new WorldMapLayout.Rect(318, 300, 50, 30), layout.zoomButtons().get(3));
    }

    @Test
    void fifteenKeyNamesFitBetweenThePageButtons() {
        assertEquals(15, layout.keyRows());
    }

    @Test
    void keyRowsAreSeventeenPixelsApart() {
        assertEquals(List.of(25, 42), List.of(layout.keyRowY(0), layout.keyRowY(1)));
    }

    @Test
    void keyRowAtFindsTheRowUnderThePoint() {
        assertEquals(List.of(0, 1, 14), List.of(layout.keyRowAt(10, 25), layout.keyRowAt(10, 42),
                layout.keyRowAt(143, 25 + 14 * 17)));
    }

    @Test
    void keyRowAtIsMinusOneOutsideTheList() {
        assertEquals(List.of(-1, -1, -1, -1), List.of(layout.keyRowAt(10, 24), layout.keyRowAt(10, 25 + 15 * 17),
                layout.keyRowAt(3, 30), layout.keyRowAt(144, 30)));
    }

    @Test
    void aRectContainsItsTopLeftButNotItsFarEdges() {
        WorldMapLayout.Rect rect = new WorldMapLayout.Rect(2, 3, 4, 5);

        assertTrue(rect.contains(2, 3));
        assertTrue(rect.contains(5, 7));
        assertFalse(rect.contains(6, 7));
        assertFalse(rect.contains(5, 8));
        assertFalse(rect.contains(1, 3));
        assertFalse(rect.contains(2, 2));
    }
}
