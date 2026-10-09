package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    void theTabIsTheRoot() {
        assertEquals(-1, SPECS.get(FlowWidgets.TAB).parent());
    }

    @Test
    void theTabOpensTheBuilderRunsAndStops() {
        assertEquals(
                List.of("Flow builder", "Run", "Stop"),
                Stream.of(FlowWidgets.TAB_OPEN_BUILDER, FlowWidgets.TAB_RUN, FlowWidgets.TAB_STOP)
                        .map(id -> assertInstanceOf(WidgetSpec.Button.class, SPECS.get(id)).text())
                        .toList());
    }
}
