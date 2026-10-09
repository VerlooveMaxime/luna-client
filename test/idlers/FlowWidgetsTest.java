package idlers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlowWidgetsTest {

    private static final Map<Integer, WidgetSpec> SPECS = FlowWidgets.specs();

    @Test
    void everyWidgetIdIsInTheFlowWidgetsRange() {
        assertTrue(SPECS.keySet().stream().allMatch(id -> id >= FlowWidgets.FIRST_ID && id < FlowWidgets.ID_LIMIT));
    }

    @Test
    void theBuilderDrawsAnOpaquePanelFirst() {
        WidgetSpec.Layer builder = assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(FlowWidgets.BUILDER));
        WidgetSpec.Box first = assertInstanceOf(WidgetSpec.Box.class, SPECS.get(builder.children().get(0)));

        assertTrue(first.filled());
        assertEquals(builder.width(), first.width());
        assertEquals(builder.height(), first.height());
    }

    @Test
    void theBuilderTitleIsCentredAwayFromTheHoverText() {
        WidgetSpec.Text title = assertInstanceOf(WidgetSpec.Text.class, SPECS.get(FlowWidgets.BUILDER_TITLE));

        assertTrue(title.centred());
        assertEquals(SPECS.get(FlowWidgets.BUILDER).width() / 2, title.x());
    }

    @Test
    void rowWidgetsFollowTheStride() {
        assertEquals(FlowWidgets.ROW_BASE, FlowWidgets.rowText(0));
        assertEquals(FlowWidgets.ROW_BASE + FlowWidgets.ROW_STRIDE + 1, FlowWidgets.rowUp(1));
        assertEquals(FlowWidgets.ROW_BASE + 2 * FlowWidgets.ROW_STRIDE + 2, FlowWidgets.rowDown(2));
        assertEquals(FlowWidgets.ROW_BASE + 3 * FlowWidgets.ROW_STRIDE + 3, FlowWidgets.rowDelete(3));
    }

    @Test
    void theTabAndTheBuilderAreTheRoots() {
        assertEquals(-1, SPECS.get(FlowWidgets.TAB).parent());
        assertEquals(-1, SPECS.get(FlowWidgets.BUILDER).parent());
    }
}
