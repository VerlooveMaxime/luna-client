package idlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Rules every code-defined widget follows, whichever screen defines it. */
class WidgetSpecsTest {

    /** Six step slots: two rows of slots, the padlock on the second. */
    private static final WidgetSpecs SPECS = WidgetSpecs.of(6);
    private static final Map<Integer, WidgetSpec> ALL = SPECS.all();

    static Stream<WidgetSpec> specs() {
        return ALL.values().stream();
    }

    static Stream<Arguments> layersAndChildren() {
        return specs().filter(WidgetSpec.Layer.class::isInstance).map(WidgetSpec.Layer.class::cast)
                .flatMap(layer -> layer.children().stream().map(child -> Arguments.of(layer, ALL.get(child))));
    }

    static Stream<WidgetSpec> underCodeDefinedLayers() {
        return specs().filter(spec -> ALL.containsKey(spec.parent()));
    }

    static Stream<WidgetSpec> roots() {
        return specs().filter(spec -> spec.parent() == -1);
    }

    static Stream<Arguments> clickOptions() {
        return Stream.concat(
                specs().filter(WidgetSpec.Button.class::isInstance).map(WidgetSpec.Button.class::cast)
                        .map(button -> Arguments.of(button.id(), button.option())),
                specs().filter(WidgetSpec.Tile.class::isInstance).map(WidgetSpec.Tile.class::cast)
                        .map(tile -> Arguments.of(tile.id(), tile.option())));
    }

    @ParameterizedTest
    @MethodSource("specs")
    void everyIdIsAboveTheCacheWidgetsAndBelowTheClientsCapacity(WidgetSpec spec) {
        assertTrue(spec.id() >= FlowWidgets.FIRST_ID && spec.id() < SPECS.capacity());
    }

    @ParameterizedTest
    @MethodSource("layersAndChildren")
    void everyChildOfALayerNamesTheLayerAsItsParent(WidgetSpec.Layer layer, WidgetSpec child) {
        assertEquals(layer.id(), child.parent());
    }

    @ParameterizedTest
    @MethodSource("underCodeDefinedLayers")
    void everyWidgetUnderACodeDefinedLayerIsAmongItsChildren(WidgetSpec spec) {
        WidgetSpec.Layer layer = assertInstanceOf(WidgetSpec.Layer.class, ALL.get(spec.parent()));

        assertTrue(layer.children().contains(spec.id()));
    }

    @ParameterizedTest
    @MethodSource("layersAndChildren")
    void childrenFitInsideTheirLayersScrollingArea(WidgetSpec.Layer layer, WidgetSpec child) {
        assertTrue(child.x() + child.width() <= layer.width(), "width");
        assertTrue(child.y() + child.height() <= layer.scrollHeight(), "height");
    }

    @ParameterizedTest
    @MethodSource("roots")
    void everyRootIsALayer(WidgetSpec root) {
        assertInstanceOf(WidgetSpec.Layer.class, root);
    }

    @ParameterizedTest
    @MethodSource("clickOptions")
    void everyClickableWidgetHasAClickOption(int id, String option) {
        assertFalse(option.isEmpty());
    }

    @Test
    void anUnknownIdHasNoSpec() {
        assertTrue(SPECS.spec(FlowWidgets.FIRST_ID - 1).isEmpty());
    }

    @Test
    void aKnownIdHasItsSpec() {
        assertEquals(FlowWidgets.TAB, SPECS.spec(FlowWidgets.TAB).orElseThrow().id());
    }

    @Test
    void aRootIsItsOwnGroupAsInTheCache() {
        assertEquals(WidgetGallery.GALLERY, SPECS.root(WidgetGallery.GALLERY));
    }

    @Test
    void aNestedWidgetBelongsToTheGroupOfItsRootNotOfItsDirectParent() {
        assertEquals(WidgetGallery.GALLERY, SPECS.root(WidgetGallery.tileFace(0)));
    }

    @Test
    void aWidgetUnderACacheLayerBelongsToThatLayersGroup() {
        assertEquals(QuestJournal.TAB, SPECS.root(QuestJournal.TUTORIAL_LINE));
    }

    @Test
    void definingAnIdTwiceFailsLoudly() {
        WidgetSpec spec = WidgetSpec.text(30001, 30000, 0, 0, 10, 10, "", 0, 0);

        assertThrows(IllegalStateException.class, () -> WidgetSpecs.merge(List.of(Map.of(30001, spec), Map.of(30001, spec))));
    }

    @Test
    void addingAWidgetMakesItTheLayersNextChild() {
        Map<Integer, WidgetSpec> specs = new HashMap<>();
        List<Integer> children = new ArrayList<>(List.of(30001));

        WidgetSpecs.add(specs, children, WidgetSpec.text(30002, 30000, 0, 0, 10, 10, "", 0, 0));

        assertEquals(List.of(30001, 30002), children);
    }

    @Test
    void theCapacityIsOnePastTheHighestId() {
        assertEquals(BuilderWidgets.lockedSprite(6) + 1, SPECS.capacity());
    }

    @Test
    void moreStepSlotsNeedMoreRoom() {
        assertTrue(WidgetSpecs.of(40).capacity() > SPECS.capacity());
    }

    @Test
    void theSpecsCannotBeChanged() {
        assertThrows(UnsupportedOperationException.class, () -> ALL.remove(FlowWidgets.TAB));
    }
}
