package idlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WidgetGalleryTest {

    private static final Map<Integer, WidgetSpec> SPECS = WidgetGallery.specs();

    static Stream<Arguments> iconsAtEverySize() {
        return IntStream.range(0, WidgetGallery.ICON_SIZES.size()).boxed().flatMap(size ->
                IntStream.range(0, WidgetGallery.STEP_ICONS.size()).mapToObj(kind -> Arguments.of(size, kind)));
    }

    @Test
    void everyWidgetIdIsInTheGallerysRange() {
        assertTrue(SPECS.keySet().stream().allMatch(id -> id >= WidgetGallery.FIRST_ID && id < WidgetGallery.ID_LIMIT));
    }

    @Test
    void theGalleryIsARootFillingTheMainScreen() {
        WidgetSpec.Layer gallery = assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(WidgetGallery.GALLERY));

        assertEquals(-1, gallery.parent());
        assertEquals(512, gallery.width());
        assertEquals(334, gallery.height());
    }

    @Test
    void theTilesScroll() {
        assertTrue(assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(WidgetGallery.SCROLL)).scrolls());
    }

    @Test
    void aTileIsALayerInsideTheScrollingLayer() {
        WidgetSpec.Layer tile = assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(WidgetGallery.tile(3)));

        assertEquals(WidgetGallery.SCROLL, tile.parent());
    }

    @Test
    void aTilesFaceLightsUpAndTakesTheClick() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(WidgetGallery.tileFace(3)));

        assertEquals(WidgetGallery.tile(3), face.parent());
        assertEquals(WidgetGallery.TILE_HOVER, face.hoverColour());
    }

    @Test
    void aTileShowsTheIconOfItsKindOfStep() {
        WidgetSpec.Sprite icon = assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(WidgetGallery.tile(2) + 3));

        assertEquals(WidgetGallery.STEP_ICONS.get(2).index(), icon.index());
    }

    @ParameterizedTest
    @MethodSource("iconsAtEverySize")
    void everyStepIconShowsAtEverySize(int size, int kind) {
        WidgetSpec.Sprite icon = assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(WidgetGallery.icon(size, kind)));

        assertEquals(WidgetGallery.STEP_ICONS.get(kind).name(), icon.name());
        assertEquals(WidgetGallery.ICON_SIZES.get(size), icon.width());
        assertEquals(WidgetGallery.ICON_SIZES.get(size), icon.height());
    }

    @Test
    void theTooltipIsTheLastWidgetSoItsBoxCoversTheOthers() {
        List<Integer> children = ((WidgetSpec.Layer) SPECS.get(WidgetGallery.GALLERY)).children();

        assertEquals(WidgetGallery.TOOLTIP, children.get(children.size() - 1));
    }

    @Test
    void theTooltipBreaksItsLineTheWayTheClientReadsIt() {
        WidgetSpec.Tooltip tooltip = assertInstanceOf(WidgetSpec.Tooltip.class, SPECS.get(WidgetGallery.TOOLTIP));

        assertTrue(tooltip.text().contains("\\n"));
    }

    @Test
    void theServerCanHideTheNestedLayer() {
        WidgetSpec.Layer hideable = assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(WidgetGallery.HIDEABLE));

        assertEquals(WidgetGallery.GALLERY, hideable.parent());
    }

    @Test
    void theLockedSlotsKeysAreShrunkToTwoSizes() {
        WidgetSpec.Sprite small = assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(WidgetGallery.KEYS_SMALL));
        WidgetSpec.Sprite big = assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(WidgetGallery.KEYS_BIG));

        assertEquals("keys", small.name());
        assertEquals(32, small.height());
        assertEquals(48, big.height());
    }

    @Test
    void theOpenedLineStartsBlankForTheServerToFill() {
        assertEquals("", assertInstanceOf(WidgetSpec.Text.class, SPECS.get(WidgetGallery.OPENED_LINE)).text());
    }

    @Test
    void theSpecsCannotBeChanged() {
        assertThrows(UnsupportedOperationException.class, () -> SPECS.remove(WidgetGallery.GALLERY));
    }
}
