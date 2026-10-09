package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlowWidgetsTest {

    private static final Map<Integer, WidgetSpec> SPECS = FlowWidgets.specs(2);

    private static String buttonText(Map<Integer, WidgetSpec> specs, int id) {
        return assertInstanceOf(WidgetSpec.Button.class, specs.get(id)).text();
    }

    @Test
    void everyWidgetIdIsInTheFlowWidgetsRangeForTheMostSavedFlows() {
        assertTrue(FlowWidgets.specs(FlowWidgets.MOST_SAVED_SLOTS).keySet().stream()
                .allMatch(id -> id >= FlowWidgets.FIRST_ID && id < FlowWidgets.ID_LIMIT));
    }

    @Test
    void moreSavedFlowsThanTheIdsHaveRoomForFailLoudly() {
        assertThrows(IllegalArgumentException.class, () -> FlowWidgets.specs(FlowWidgets.MOST_SAVED_SLOTS + 1));
    }

    @Test
    void theTabIsTheRoot() {
        assertEquals(-1, SPECS.get(FlowWidgets.TAB).parent());
    }

    @Test
    void theTabRunsAndStops() {
        assertEquals(List.of("Run", "Stop"), Stream.of(FlowWidgets.TAB_RUN, FlowWidgets.TAB_STOP).map(id -> buttonText(SPECS, id)).toList());
    }

    @Test
    void theSavedFlowsSitUnderTheTabsButtons() {
        assertTrue(SPECS.get(FlowWidgets.TAB_SAVED).y() > SPECS.get(FlowWidgets.TAB_RUN).y() + SPECS.get(FlowWidgets.TAB_RUN).height());
    }

    @Test
    void theListEndsInsideTheTab() {
        assertTrue(FlowWidgets.SAVED_Y + FlowWidgets.ROWS_Y + FlowWidgets.ROWS_HEIGHT <= SPECS.get(FlowWidgets.TAB).height());
    }

    @Test
    void theListHoldsARowPerSavedFlowThenThePadlock() {
        WidgetSpec.Layer rows = assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(FlowWidgets.SAVED_ROWS));

        assertEquals(List.of(FlowWidgets.row(0), FlowWidgets.row(1), FlowWidgets.SAVED_LOCKED), rows.children());
    }

    @Test
    void threeSavedFlowsFitWithoutScrolling() {
        assertTrue(FlowWidgets.rowsContent(3) <= FlowWidgets.ROWS_HEIGHT);
    }

    @Test
    void fourSavedFlowsScroll() {
        assertTrue(assertInstanceOf(WidgetSpec.Layer.class, FlowWidgets.specs(4).get(FlowWidgets.SAVED_ROWS)).scrolls());
    }

    /** The server cuts names to the room these widths leave (`FlowWidgets.nameRoom`: 166, or 150 when scrolling). */
    @Test
    void aRowFillsAListThatFits() {
        assertEquals(174, FlowWidgets.rowWidth(3));
    }

    @Test
    void aRowLeavesAScrollingListRoomForItsScrollbar() {
        assertEquals(158, FlowWidgets.rowWidth(4));
    }

    @Test
    void theNameLeavesFourPixelsEachSide() {
        assertEquals(166, SPECS.get(FlowWidgets.rowName(0)).width());
    }

    @Test
    void rowsFollowEachOtherDown() {
        assertEquals(FlowWidgets.ROW_HEIGHT + FlowWidgets.ROW_GAP, SPECS.get(FlowWidgets.row(1)).y());
    }

    @Test
    void thePadlockFollowsTheLastRow() {
        assertEquals(2 * (FlowWidgets.ROW_HEIGHT + FlowWidgets.ROW_GAP), SPECS.get(FlowWidgets.SAVED_LOCKED).y());
    }

    @Test
    void thePadlockIsTheCachesRingOfKeys() {
        assertEquals("keys", assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(FlowWidgets.SAVED_LOCKED_SPRITE)).name());
    }

    @Test
    void aRowLoadsStartsNewAndEmpties() {
        assertEquals(
                List.of("Load", "New", "x"),
                Stream.of(FlowWidgets.rowLoad(1), FlowWidgets.rowNew(1), FlowWidgets.rowDelete(1)).map(id -> buttonText(SPECS, id)).toList());
    }

    @Test
    void loadNewAndXSitInLayersOfTheirOwnSoTheServerCanHideThem() {
        assertEquals(
                List.of(FlowWidgets.rowLoadLayer(0), FlowWidgets.rowNewLayer(0), FlowWidgets.rowDeleteLayer(0)),
                List.of(SPECS.get(FlowWidgets.rowLoad(0)).parent(), SPECS.get(FlowWidgets.rowNew(0)).parent(), SPECS.get(FlowWidgets.rowDelete(0)).parent()));
    }

    @Test
    void newTakesLoadsPlaceOnAnEmptySlot() {
        assertEquals(SPECS.get(FlowWidgets.rowLoadLayer(0)).x(), SPECS.get(FlowWidgets.rowNewLayer(0)).x());
    }

    @Test
    void theRowsFrameIsGreenInALayerOfItsOwn() {
        WidgetSpec.Box frame = assertInstanceOf(WidgetSpec.Box.class, SPECS.get(FlowWidgets.rowFrame(0)));

        assertEquals(List.<Object>of(FlowWidgets.GREEN, false, FlowWidgets.rowFrameLayer(0)), List.of(frame.colour(), frame.filled(), frame.parent()));
    }

    @Test
    void theRowsButtonsEndInsideTheRow() {
        WidgetSpec x = SPECS.get(FlowWidgets.rowDeleteLayer(0));

        assertTrue(x.x() + x.width() <= SPECS.get(FlowWidgets.row(0)).width());
    }
}
