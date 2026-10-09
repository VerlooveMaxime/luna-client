package idlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchWidgetsTest {

    private static final Map<Integer, WidgetSpec> SPECS = SearchWidgets.specs();

    static IntStream places() {
        return IntStream.range(0, SearchGrid.MOST_ICONS);
    }

    @Test
    void everyWidgetIdIsInTheSearchsRange() {
        assertTrue(SPECS.keySet().stream().allMatch(id -> id >= SearchWidgets.FIRST_ID && id < SearchWidgets.ID_LIMIT));
    }

    @Test
    void theIconsLayerIsARootOverTheRowsArea() {
        WidgetSpec.Layer layer = assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(SearchWidgets.ICONS));

        assertEquals(-1, layer.parent());
        assertEquals(SearchGrid.TOP, layer.y());
        assertEquals(SearchGrid.WIDTH, layer.width());
        assertEquals(SearchGrid.HEIGHT, layer.height());
    }

    @Test
    void theLayerHoldsAPictureForEveryIconShown() {
        assertEquals(SearchGrid.MOST_ICONS, assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(SearchWidgets.ICONS)).children().size());
    }

    @ParameterizedTest
    @MethodSource("places")
    void everyIconIsAPictureOfTheGridsIconSize(int place) {
        WidgetSpec.Picture icon = assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(SearchWidgets.icon(place)));

        assertEquals(SearchGrid.ICON, icon.width());
        assertEquals(SearchGrid.ICON, icon.height());
    }

    @Test
    void npcBodiesAreSeenAsInTheGallery() {
        WidgetSpec.Picture icon = assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(SearchWidgets.icon(0)));

        assertEquals(WidgetGallery.NPC_PITCH, icon.pitch());
        assertEquals(WidgetGallery.NPC_YAW, icon.yaw());
    }

    @Test
    void theClientMakesRoomForTheSearchsIds() {
        assertTrue(WidgetSpecs.of(0).capacity() >= SearchWidgets.ID_LIMIT);
    }
}
