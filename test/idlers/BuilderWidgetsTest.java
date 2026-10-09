package idlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderWidgetsTest {

    /** Six step slots: a full row, then two slots and the padlock. */
    private static final Map<Integer, WidgetSpec> SPECS = BuilderWidgets.specs(6);

    private static WidgetSpec.Layer layer(int id) {
        return assertInstanceOf(WidgetSpec.Layer.class, SPECS.get(id));
    }

    @Test
    void theRootIsAWholeMainScreen() {
        WidgetSpec.Layer root = layer(BuilderWidgets.ROOT);

        assertEquals(List.of(-1, 512, 334), List.of(root.parent(), root.width(), root.height()));
    }

    @Test
    void theOverviewAndTheKindPickerAreLayersOfTheRoot() {
        assertEquals(List.of(BuilderWidgets.ROOT, BuilderWidgets.ROOT),
                List.of(layer(BuilderWidgets.OVERVIEW).parent(), layer(BuilderWidgets.KINDS).parent()));
    }

    @Test
    void theFixedIdsStayInTheirRange() {
        assertTrue(SPECS.keySet().stream().filter(id -> id < BuilderWidgets.SLOT_BASE)
                .allMatch(id -> id >= BuilderWidgets.FIRST_ID && id < BuilderWidgets.ID_LIMIT));
    }

    @Test
    void theSlotAreaHoldsEverySlotThenThePadlock() {
        assertEquals(List.of(BuilderWidgets.slot(0), BuilderWidgets.slot(1), BuilderWidgets.slot(2), BuilderWidgets.slot(3),
                BuilderWidgets.slot(4), BuilderWidgets.slot(5), BuilderWidgets.slot(6)), layer(BuilderWidgets.SLOTS).children());
    }

    @Test
    void slotsGoFourToARow() {
        WidgetSpec fifth = SPECS.get(BuilderWidgets.slot(4));

        assertEquals(List.of(0, BuilderWidgets.SLOT_HEIGHT + BuilderWidgets.SLOT_GAP), List.of(fifth.x(), fifth.y()));
    }

    @Test
    void slotsInARowAreAGapApart() {
        WidgetSpec second = SPECS.get(BuilderWidgets.slot(1));

        assertEquals(BuilderWidgets.SLOT_WIDTH + BuilderWidgets.SLOT_GAP, second.x());
    }

    @Test
    void thePadlockFollowsTheLastSlot() {
        WidgetSpec padlock = SPECS.get(BuilderWidgets.slot(6));

        assertEquals(List.of(2 * (BuilderWidgets.SLOT_WIDTH + BuilderWidgets.SLOT_GAP), BuilderWidgets.SLOT_HEIGHT + BuilderWidgets.SLOT_GAP),
                List.of(padlock.x(), padlock.y()));
    }

    @Test
    void thePadlockShowsTheCachesKeys() {
        assertEquals("keys", assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(BuilderWidgets.lockedSprite(6))).name());
    }

    @Test
    void thePadlockCannotBeDragged() {
        assertTrue(new TileDrag(SPECS).tileOf(BuilderWidgets.slot(6) + 1).isEmpty());
    }

    @Test
    void aSlotsFaceDragsItsSlot() {
        assertEquals(Optional.of(BuilderWidgets.slot(2)), new TileDrag(SPECS).tileOf(BuilderWidgets.slotFace(2)));
    }

    @Test
    void aSlotDroppedOnAnotherMovesToItsPlace() {
        assertEquals(Optional.of(new TileDrag.Move(0, 3, BuilderWidgets.SLOTS)),
                new TileDrag(SPECS).drop(BuilderWidgets.slotFace(0), BuilderWidgets.slotLine(3, 1)));
    }

    @Test
    void twoRowsShowWithoutScrolling() {
        assertFalse(layer(BuilderWidgets.SLOTS).scrolls());
    }

    @Test
    void aThirdRowScrolls() {
        WidgetSpec.Layer slots = assertInstanceOf(WidgetSpec.Layer.class, BuilderWidgets.specs(8).get(BuilderWidgets.SLOTS));

        assertEquals(3 * BuilderWidgets.SLOT_HEIGHT + 2 * BuilderWidgets.SLOT_GAP, slots.scrollHeight());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3, 4, 7})
    void slotsAndThePadlockTakeTheirRows(int slots) {
        assertEquals(slots / 4 + 1, BuilderWidgets.slotRows(slots));
    }

    @Test
    void noSlotsLeavesThePadlockAlone() {
        assertEquals(List.of(BuilderWidgets.slot(0)),
                assertInstanceOf(WidgetSpec.Layer.class, BuilderWidgets.specs(0).get(BuilderWidgets.SLOTS)).children());
    }

    @Test
    void everySlotWidgetStaysInsideItsStride() {
        assertTrue(BuilderWidgets.slotCornerLayer(0) < BuilderWidgets.slot(1));
    }

    @Test
    void theCornersBoxAndPictureHideTogetherInTheirOwnLayer() {
        WidgetSpec.Layer corner = layer(BuilderWidgets.slotCornerLayer(0));

        assertEquals(List.of(BuilderWidgets.slotCornerBox(0), BuilderWidgets.slotCorner(0)), corner.children());
    }

    @Test
    void aSlotHasItsLinesOneUnderAnother() {
        WidgetSpec first = SPECS.get(BuilderWidgets.slotLine(0, 0));
        WidgetSpec last = SPECS.get(BuilderWidgets.slotLine(0, BuilderWidgets.SLOT_LINES - 1));

        assertEquals(13 * (BuilderWidgets.SLOT_LINES - 1), last.y() - first.y());
    }

    @Test
    void theKindsCornerSitsOnItsBox() {
        WidgetSpec box = SPECS.get(BuilderWidgets.slotCornerBox(0));
        WidgetSpec corner = SPECS.get(BuilderWidgets.slotCorner(0));

        assertEquals(List.of(box.x() + 1, box.y() + 1), List.of(corner.x(), corner.y()));
    }

    @Test
    void npcPicturesAreSeenAsInTheGallery() {
        WidgetSpec.Picture picture = assertInstanceOf(WidgetSpec.Picture.class, SPECS.get(BuilderWidgets.slotPicture(0)));

        assertEquals(List.of(WidgetGallery.NPC_PITCH, WidgetGallery.NPC_YAW), List.of(picture.pitch(), picture.yaw()));
    }

    @Test
    void theKindPickerHasALayerPerKindButtonSoItCanHide() {
        assertEquals(BuilderWidgets.KIND_BUTTONS, layer(BuilderWidgets.KINDS).children().stream()
                .filter(id -> SPECS.get(id) instanceof WidgetSpec.Layer).count());
    }

    @Test
    void aKindButtonHoldsItsFacePictureAndLabel() {
        assertEquals(List.of(BuilderWidgets.kindFace(3), BuilderWidgets.kindPicture(3), BuilderWidgets.kindLabel(3)),
                layer(BuilderWidgets.kindButton(3)).children());
    }

    @Test
    void kindsGoSevenToARow() {
        WidgetSpec first = SPECS.get(BuilderWidgets.kindButton(0));
        WidgetSpec eighth = SPECS.get(BuilderWidgets.kindButton(7));

        assertEquals(List.of(first.x(), first.y() + BuilderWidgets.KIND_HEIGHT + BuilderWidgets.KIND_GAP), List.of(eighth.x(), eighth.y()));
    }

    @Test
    void aKindsPictureAndLabelAreItsOwn() {
        assertEquals(List.of(BuilderWidgets.kindButton(2) + 1, BuilderWidgets.kindButton(2) + 2, BuilderWidgets.kindButton(2) + 3),
                List.of(BuilderWidgets.kindFace(2), BuilderWidgets.kindPicture(2), BuilderWidgets.kindLabel(2)));
    }

    @Test
    void aKindButtonCannotBeDragged() {
        assertTrue(new TileDrag(SPECS).tileOf(BuilderWidgets.kindFace(0)).isEmpty());
    }

    @Test
    void theLastKindButtonEndsBeforeTheIdsOfTheSlots() {
        assertTrue(BuilderWidgets.kindLabel(BuilderWidgets.KIND_BUTTONS - 1) < BuilderWidgets.ID_LIMIT);
    }

    @Test
    void theConfigureScreenIsALayerOfTheRoot() {
        assertEquals(BuilderWidgets.ROOT, layer(BuilderWidgets.CONFIGURE).parent());
    }

    @Test
    void configureRowsMirrorTheServersIds() {
        assertEquals(List.of(30843, 30846, 30847, 30852, 30854, 30855, 30960),
                List.of(BuilderWidgets.row(1), BuilderWidgets.rowFace(1), BuilderWidgets.rowFrame(1), BuilderWidgets.rowButtonFace(1),
                        BuilderWidgets.rowNote(1), BuilderWidgets.rowButtonFrame(1), BuilderWidgets.row(BuilderWidgets.ROWS)));
    }

    @Test
    void configureRowsFillTheLeftColumnThenTheRight() {
        WidgetSpec fifth = SPECS.get(BuilderWidgets.row(4));
        WidgetSpec sixth = SPECS.get(BuilderWidgets.row(5));

        assertEquals(List.of(0, BuilderWidgets.ROW_TOP + 4 * BuilderWidgets.ROW_HEIGHT, BuilderWidgets.RIGHT_COLUMN, BuilderWidgets.ROW_TOP),
                List.of(fifth.x(), fifth.y(), sixth.x(), sixth.y()));
    }

    @Test
    void aConfigureRowHoldsItsLabelFieldButtonAndNote() {
        assertEquals(List.of(BuilderWidgets.rowLabel(2), BuilderWidgets.rowField(2), BuilderWidgets.rowButton(2), BuilderWidgets.rowNote(2)),
                layer(BuilderWidgets.row(2)).children());
    }

    @Test
    void aFieldDrawsItsFaceFirstThenItsFrameSoTheServerCanColourIt() {
        assertEquals(List.of(BuilderWidgets.rowFace(3), BuilderWidgets.rowFrame(3), BuilderWidgets.rowPicture(3), BuilderWidgets.rowText(3),
                BuilderWidgets.rowPlainText(3)), layer(BuilderWidgets.rowField(3)).children());
    }

    @Test
    void aFieldIsClickedAndNotDragged() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.rowFace(0)));

        assertEquals(List.of("Change", false), List.of(face.option(), face.draggable()));
    }

    @Test
    void aRowButtonIsClickedAndNotDragged() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.rowButtonFace(0)));

        assertEquals(List.of("Select", false), List.of(face.option(), face.draggable()));
    }

    @Test
    void theFieldsTextLeavesRoomForItsPicture() {
        WidgetSpec picture = SPECS.get(BuilderWidgets.rowPicture(0));

        assertTrue(SPECS.get(BuilderWidgets.rowText(0)).x() >= picture.x() + picture.width());
    }

    @Test
    void theHeaderCornerIsALayerSoItHides() {
        assertEquals(List.of(BuilderWidgets.HEADER_CORNER_BOX, BuilderWidgets.HEADER_CORNER), layer(BuilderWidgets.HEADER_CORNER_LAYER).children());
    }

    @Test
    void theConfigureScreenEndsWithItsWarningsAndButtons() {
        List<Integer> children = layer(BuilderWidgets.CONFIGURE).children();

        assertEquals(List.of(BuilderWidgets.warning(0), BuilderWidgets.warning(1), BuilderWidgets.warning(2), BuilderWidgets.DELETE,
                BuilderWidgets.BACK, BuilderWidgets.SAVE), children.subList(children.size() - 6, children.size()));
    }
}
