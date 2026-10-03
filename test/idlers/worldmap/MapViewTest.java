package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapViewTest {

    /** A 1000 x 800 tile map seen through 400 x 200 pixels. */
    private static MapView view(ZoomLevel level) {
        return new MapView(1000, 800, 400, 200, level);
    }

    @Test
    void startsCentredOnTheMapAtTheChosenZoom() {
        MapView view = view(ZoomLevel.P50);

        assertEquals(List.of(500, 400, 4.0), List.of(view.focusColumn(), view.focusRow(), view.zoom()));
    }

    @Test
    void at100PercentATileIsFourPixelsWide() {
        MapView view = view(ZoomLevel.P100);

        assertEquals(List.of(450, 550, 375, 425), List.of(view.left(), view.right(), view.top(), view.bottom()));
    }

    @Test
    void centreOnMovesTheFocus() {
        MapView view = view(ZoomLevel.P100);

        view.centreOn(300, 200);

        assertEquals(List.of(300, 200), List.of(view.focusColumn(), view.focusRow()));
    }

    @Test
    void centreOnKeepsTheMarginFromTheTopLeftEdges() {
        MapView view = view(ZoomLevel.P100);

        view.centreOn(0, 0);

        assertEquals(List.of(MapView.MARGIN, MapView.MARGIN), List.of(view.left(), view.top()));
    }

    @Test
    void centreOnKeepsTheMarginFromTheBottomRightEdges() {
        MapView view = view(ZoomLevel.P100);

        view.centreOn(5000, 5000);

        assertEquals(List.of(1000 - MapView.MARGIN, 800 - MapView.MARGIN), List.of(view.right(), view.bottom()));
    }

    @Test
    void draggingMovesTheMapWithTheMouse() {
        MapView view = view(ZoomLevel.P100);
        view.startDrag(200, 100);

        view.dragTo(160, 120);

        assertEquals(List.of(510, 395), List.of(view.focusColumn(), view.focusRow()));
    }

    @Test
    void dragToWithoutADragDoesNothing() {
        MapView view = view(ZoomLevel.P100);

        view.dragTo(0, 0);

        assertEquals(500, view.focusColumn());
    }

    @Test
    void endDragStopsFollowingTheMouse() {
        MapView view = view(ZoomLevel.P100);
        view.startDrag(200, 100);
        view.endDrag();

        view.dragTo(0, 0);

        assertFalse(view.dragging());
        assertEquals(500, view.focusColumn());
    }

    @Test
    void zoomingInEasesAThirtiethPerTick() {
        MapView view = view(ZoomLevel.P50);
        view.zoomTo(ZoomLevel.P100);

        view.tick();

        assertEquals(4.0 + 4.0 / 30.0, view.zoom());
        assertFalse(view.settled());
    }

    @Test
    void zoomingOutEasesAThirtiethPerTick() {
        MapView view = view(ZoomLevel.P100);
        view.zoomTo(ZoomLevel.P75);

        view.tick();

        assertEquals(8.0 - 8.0 / 30.0, view.zoom());
    }

    @Test
    void zoomingStopsExactlyAtTheTarget() {
        MapView view = view(ZoomLevel.P75);
        view.zoomTo(ZoomLevel.P100);

        tick(view, 30);

        assertEquals(8.0, view.zoom());
        assertTrue(view.settled());
    }

    @Test
    void zoomingOutStopsExactlyAtTheTarget() {
        MapView view = view(ZoomLevel.P50);
        view.zoomTo(ZoomLevel.P37);

        tick(view, 30);

        assertEquals(ZoomLevel.P37.factor(), view.zoom());
    }

    @Test
    void aSettledViewStaysPut() {
        MapView view = view(ZoomLevel.P50);

        view.tick();

        assertEquals(List.of(4.0, ZoomLevel.P50), List.of(view.zoom(), view.target()));
    }

    @Test
    void pixelsMapToTilesAndBack() {
        MapView view = view(ZoomLevel.P100);

        assertEquals(List.of(452, 380), List.of(view.columnAt(9), view.rowAt(20)));
        assertEquals(List.of(10, 22), List.of(view.pixelX(452.5), view.pixelY(380.5)));
    }

    private static void tick(MapView view, int ticks) {
        for (int i = 0; i < ticks; i++) {
            view.tick();
        }
    }
}
