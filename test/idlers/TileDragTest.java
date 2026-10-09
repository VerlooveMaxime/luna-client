package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TileDragTest {

    /**
     * Layer 10 holds a heading (11) and tiles 20, 30 and 40, each a layer with a draggable face (+1) and a text (+2);
     * layer 50 holds tile 60, and tile 70 has a face that cannot be dragged.
     */
    private static final Map<Integer, WidgetSpec> SPECS = Map.ofEntries(
            Map.entry(10, WidgetSpec.layer(10, -1, 0, 0, 300, 100, List.of(11, 20, 30, 40, 70))),
            Map.entry(11, WidgetSpec.text(11, 10, 0, 0, 50, 14, "Steps", 0, 0)),
            tile(20, 10), face(21, 20, true), text(22, 20),
            tile(30, 10), face(31, 30, true), text(32, 30),
            tile(40, 10), face(41, 40, true), text(42, 40),
            Map.entry(50, WidgetSpec.layer(50, -1, 0, 0, 100, 100, List.of(60))),
            tile(60, 50), face(61, 60, true), text(62, 60),
            tile(70, 10), face(71, 70, false), text(72, 70));

    private final TileDrag drag = new TileDrag(SPECS);

    private static Map.Entry<Integer, WidgetSpec> tile(int id, int parent) {
        return Map.entry(id, WidgetSpec.layer(id, parent, 0, 0, 50, 50, List.of(id + 1, id + 2)));
    }

    private static Map.Entry<Integer, WidgetSpec> face(int id, int parent, boolean draggable) {
        return Map.entry(id, new WidgetSpec.Tile(id, parent, 0, 0, 50, 50, 1, 2, "Select", draggable));
    }

    private static Map.Entry<Integer, WidgetSpec> text(int id, int parent) {
        return Map.entry(id, WidgetSpec.text(id, parent, 0, 20, 50, 14, "", 0, 0));
    }

    @Test
    void aPressOnADraggableFaceDragsItsTile() {
        assertEquals(Optional.of(30), drag.tileOf(31));
    }

    @Test
    void aFaceThatCannotBeDraggedDragsNothing() {
        assertTrue(drag.tileOf(71).isEmpty());
    }

    @Test
    void aPressOnAnythingElseDragsNothing() {
        assertTrue(drag.tileOf(32).isEmpty());
        assertTrue(drag.tileOf(999).isEmpty());
    }

    @Test
    void droppingOnAnotherTilesFaceMovesToItsPlace() {
        assertEquals(Optional.of(new TileDrag.Move(0, 2, 10)), drag.drop(21, 41));
    }

    @Test
    void droppingOnAWidgetInsideAnotherTileMovesThere() {
        assertEquals(Optional.of(new TileDrag.Move(2, 1, 10)), drag.drop(41, 32));
    }

    @Test
    void tilesAreCountedAmongTheLayersDraggableTilesOnly() {
        assertEquals(Optional.of(new TileDrag.Move(1, 0, 10)), drag.drop(31, 21));
    }

    @Test
    void aTileWhoseFaceCannotBeDraggedIsNoPlaceToDropOn() {
        assertTrue(drag.drop(21, 71).isEmpty());
    }

    @Test
    void droppingOnItselfMovesNothing() {
        assertTrue(drag.drop(21, 22).isEmpty());
    }

    @Test
    void droppingOnATileOfAnotherLayerMovesNothing() {
        assertTrue(drag.drop(21, 61).isEmpty());
    }

    @Test
    void droppingOutsideEveryTileMovesNothing() {
        assertTrue(drag.drop(21, 11).isEmpty());
        assertTrue(drag.drop(21, 0).isEmpty());
    }

    @Test
    void aDropAfterPressingSomethingElseMovesNothing() {
        assertTrue(drag.drop(32, 41).isEmpty());
    }

    @Test
    void theDraggedTileIsDrawnLastAndTheOthersKeepTheirOrder() {
        TileDrag.Children children = new TileDrag.Children(new int[]{11, 20, 30, 40}, new int[]{1, 2, 3, 4}, new int[]{5, 6, 7, 8});

        TileDrag.Children front = TileDrag.toFront(children, 20);

        assertArrayEquals(new int[]{11, 30, 40, 20}, front.ids());
        assertArrayEquals(new int[]{1, 3, 4, 2}, front.xs());
        assertArrayEquals(new int[]{5, 7, 8, 6}, front.ys());
    }
}
