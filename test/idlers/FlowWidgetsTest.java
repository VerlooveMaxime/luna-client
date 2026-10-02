package idlers;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlowWidgetsTest {

    @Test
    void everyWidgetIdIsInTheReservedRange() {
        for (int id : FlowWidgets.specs().keySet()) {
            assertTrue(id >= FlowWidgets.FIRST_ID && id < FlowWidgets.ID_LIMIT, "id " + id);
        }
    }

    @Test
    void everyChildBelongsToItsLayerAndExists() {
        for (WidgetSpec layer : FlowWidgets.specs().values()) {
            for (int childId : layer.children()) {
                WidgetSpec child = FlowWidgets.spec(childId);
                assertNotNull(child, "child " + childId);
                assertEquals(layer.id(), child.parent(), "parent of " + childId);
            }
        }
    }

    @Test
    void everyNonLayerWidgetIsSomeLayersChild() {
        Set<Integer> placed = new HashSet<>();
        FlowWidgets.specs().values().forEach(spec -> placed.addAll(spec.children()));
        for (WidgetSpec spec : FlowWidgets.specs().values()) {
            if (spec.kind() != WidgetSpec.Kind.LAYER) {
                assertTrue(placed.contains(spec.id()), "orphan " + spec.id());
            }
        }
    }

    @Test
    void childrenFitInsideTheirLayer() {
        for (WidgetSpec layer : FlowWidgets.specs().values()) {
            for (int childId : layer.children()) {
                WidgetSpec child = FlowWidgets.spec(childId);
                assertTrue(child.x() + child.width() <= layer.width(), "width of " + childId);
                assertTrue(child.y() + child.height() <= layer.height(), "height of " + childId);
            }
        }
    }

    @Test
    void theBuilderDrawsAnOpaquePanelFirst() {
        WidgetSpec builder = FlowWidgets.spec(FlowWidgets.BUILDER);
        WidgetSpec first = FlowWidgets.spec(builder.children().get(0));

        assertEquals(WidgetSpec.Kind.BOX, first.kind());
        assertEquals(builder.width(), first.width());
        assertEquals(builder.height(), first.height());
    }

    @Test
    void theBuilderTitleIsCentredAwayFromTheHoverText() {
        WidgetSpec title = FlowWidgets.spec(FlowWidgets.BUILDER_TITLE);

        assertTrue(title.centred());
        assertEquals(FlowWidgets.spec(FlowWidgets.BUILDER).width() / 2, title.x());
    }

    @Test
    void buttonsHaveAClickOption() {
        for (WidgetSpec spec : FlowWidgets.specs().values()) {
            if (spec.kind() == WidgetSpec.Kind.BUTTON) {
                assertFalse(spec.tooltip().isEmpty(), "tooltip of " + spec.id());
            }
        }
    }

    @Test
    void rowWidgetsFollowTheStride() {
        assertEquals(FlowWidgets.ROW_BASE, FlowWidgets.rowText(0));
        assertEquals(FlowWidgets.ROW_BASE + FlowWidgets.ROW_STRIDE + 1, FlowWidgets.rowUp(1));
        assertEquals(FlowWidgets.ROW_BASE + 2 * FlowWidgets.ROW_STRIDE + 2, FlowWidgets.rowDown(2));
        assertEquals(FlowWidgets.ROW_BASE + 3 * FlowWidgets.ROW_STRIDE + 3, FlowWidgets.rowDelete(3));
    }

    @Test
    void unknownIdsHaveNoSpec() {
        assertNull(FlowWidgets.spec(FlowWidgets.FIRST_ID - 1));
        assertNull(FlowWidgets.spec(FlowWidgets.ID_LIMIT));
    }

    @Test
    void theTwoRootsAreLayersWithoutAParent() {
        Map<Integer, WidgetSpec> specs = FlowWidgets.specs();
        assertEquals(WidgetSpec.Kind.LAYER, specs.get(FlowWidgets.TAB).kind());
        assertEquals(WidgetSpec.Kind.LAYER, specs.get(FlowWidgets.BUILDER).kind());
        assertEquals(-1, specs.get(FlowWidgets.TAB).parent());
        assertEquals(-1, specs.get(FlowWidgets.BUILDER).parent());
    }
}
