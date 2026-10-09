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
                IntStream.range(0, WidgetGallery.ICON_KINDS).mapToObj(kind -> Arguments.of(size, kind)));
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
    void aTilesFaceCanBeDragged() {
        assertTrue(assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(WidgetGallery.tileFace(3))).draggable());
    }

    @Test
    void aTileHasAPictureAndTextsTheServerFills() {
        assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(WidgetGallery.tilePicture(2)));
        assertEquals("", assertInstanceOf(WidgetSpec.Text.class, SPECS.get(WidgetGallery.tileLabel(2))).text());
        assertEquals("", assertInstanceOf(WidgetSpec.Text.class, SPECS.get(WidgetGallery.tileKind(2))).text());
    }

    @ParameterizedTest
    @MethodSource("iconsAtEverySize")
    void everyStepIconHasAPictureAtEverySize(int size, int kind) {
        WidgetSpec.Picture icon = assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(WidgetGallery.icon(size, kind)));

        assertEquals(WidgetGallery.ICON_SIZES.get(size), icon.width());
        assertEquals(WidgetGallery.ICON_SIZES.get(size), icon.height());
    }

    @Test
    void itemIconsShowAtTheirOwnSizeAndShrunk() {
        WidgetSpec.Picture small = assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(WidgetGallery.item(1, 1)));

        assertEquals(WidgetGallery.ITEM_SIZES.get(1), small.width());
    }

    @Test
    void eachNpcHasANameTheServerFillsUnderIt() {
        WidgetSpec.Picture npc = (WidgetSpec.Picture) SPECS.get(WidgetGallery.npc(2));
        WidgetSpec.Text name = assertInstanceOf(WidgetSpec.Text.class, SPECS.get(WidgetGallery.npcName(2)));

        assertEquals("", name.text());
        assertEquals(npc.x() + npc.width() / 2, name.x());
        assertTrue(name.y() >= npc.y() + npc.height());
    }

    @Test
    void npcBodiesAreSeenFromAboveAndTurnedTowardsTheCamera() {
        WidgetSpec.Picture npc = assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(WidgetGallery.npc(3)));

        assertEquals(WidgetGallery.NPC_PITCH, npc.pitch());
        assertEquals(WidgetGallery.NPC_YAW, npc.yaw());
        assertEquals(WidgetGallery.NPC_SIZE, npc.height());
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
    void theNameButtonFollowsShow() {
        WidgetSpec show = SPECS.get(WidgetGallery.SHOW);
        WidgetSpec.Button name = assertInstanceOf(WidgetSpec.Button.class, SPECS.get(WidgetGallery.NAME));

        assertEquals(List.of(show.x() + show.width() + 8, show.y(), "Name"), List.of(name.x(), name.y(), name.text()));
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

    static IntStream searches() {
        return IntStream.range(0, WidgetGallery.SEARCHES.size());
    }

    @ParameterizedTest
    @MethodSource("searches")
    void everySearchHasAButtonNamedAfterIt(int search) {
        WidgetSpec.Button button = assertInstanceOf(WidgetSpec.Button.class, SPECS.get(WidgetGallery.search(search)));

        assertEquals(WidgetGallery.SEARCHES.get(search), button.text());
        assertEquals("Search " + WidgetGallery.SEARCHES.get(search).toLowerCase(), button.option());
    }

    @Test
    void theSearchButtonsFollowEachOther() {
        WidgetSpec trees = SPECS.get(WidgetGallery.search(0));
        WidgetSpec fish = SPECS.get(WidgetGallery.search(1));

        assertEquals(trees.x() + trees.width() + 6, fish.x());
    }

    @Test
    void theLastSearchButtonEndsInsideTheGallery() {
        WidgetSpec last = SPECS.get(WidgetGallery.search(WidgetGallery.SEARCHES.size() - 1));

        assertTrue(last.x() + last.width() <= 504);
    }

    @Test
    void theSpecsCannotBeChanged() {
        assertThrows(UnsupportedOperationException.class, () -> SPECS.remove(WidgetGallery.GALLERY));
    }
}
