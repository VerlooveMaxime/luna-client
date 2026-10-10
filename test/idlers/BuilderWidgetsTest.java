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
    private static final Map<Integer, WidgetSpec> SPECS = BuilderWidgets.specs(6, 3, BuilderWidgets.unplacedLists());

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
    void theOverviewSavesTheFlowLeftOfRun() {
        WidgetSpec.Button save = assertInstanceOf(WidgetSpec.Button.class, SPECS.get(BuilderWidgets.SAVE_FLOW));

        assertEquals(List.of("Save", BuilderWidgets.OVERVIEW), List.of(save.text(), save.parent()));
        assertTrue(save.x() + save.width() < SPECS.get(BuilderWidgets.RUN).x());
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
        WidgetSpec.Layer slots = assertInstanceOf(WidgetSpec.Layer.class, BuilderWidgets.specs(8, 3, BuilderWidgets.unplacedLists()).get(BuilderWidgets.SLOTS));

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
                assertInstanceOf(WidgetSpec.Layer.class, BuilderWidgets.specs(0, 3, BuilderWidgets.unplacedLists()).get(BuilderWidgets.SLOTS)).children());
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
        assertEquals(List.of(30860, 30863, 30864, 30869, 30871, 30872, 31190),
                List.of(BuilderWidgets.row(1), BuilderWidgets.rowFace(1), BuilderWidgets.rowFrame(1), BuilderWidgets.rowButtonFace(1),
                        BuilderWidgets.rowNote(1), BuilderWidgets.rowButtonFrame(1), BuilderWidgets.row(BuilderWidgets.ROWS)));
    }

    @Test
    void toggleButtonsMirrorTheServersIds() {
        assertEquals(List.of(30873, 30877, 30880, 30888, 30853),
                List.of(BuilderWidgets.toggles(1, 2), BuilderWidgets.toggleFace(1, 2, 1), BuilderWidgets.toggles(1, 3),
                        BuilderWidgets.toggleFrame(1, 3, 2), BuilderWidgets.toggleText(0, 3, 0)));
    }

    @Test
    void listsMirrorTheServersIds() {
        assertEquals(List.of(31600, 31202, 31604, 31634, 31212, 31537, 31614, 31232),
                List.of(BuilderWidgets.list(1), BuilderWidgets.listAdd(0), BuilderWidgets.listAddText(1), BuilderWidgets.line(1, 2),
                        BuilderWidgets.linePicture(0, 0), BuilderWidgets.lineName(0, 27), BuilderWidgets.lineAmount(1, 0),
                        BuilderWidgets.lineRemoveFace(0, 1)));
    }

    @Test
    void configureRowsFillTheLeftColumnThenTheRight() {
        WidgetSpec sixth = SPECS.get(BuilderWidgets.row(5));
        WidgetSpec seventh = SPECS.get(BuilderWidgets.row(6));

        assertEquals(List.of(0, BuilderWidgets.ROW_TOP + 5 * BuilderWidgets.ROW_HEIGHT, BuilderWidgets.RIGHT_COLUMN, BuilderWidgets.ROW_TOP),
                List.of(sixth.x(), sixth.y(), seventh.x(), seventh.y()));
    }

    @Test
    void theSixthRowEndsAboveTheWarnings() {
        WidgetSpec sixth = SPECS.get(BuilderWidgets.row(5));

        assertTrue(sixth.y() + sixth.height() < SPECS.get(BuilderWidgets.BOTTOM_DIVIDER).y());
    }

    @Test
    void aConfigureRowHoldsItsLabelFieldButtonNoteAndToggles() {
        assertEquals(List.of(BuilderWidgets.rowLabel(2), BuilderWidgets.rowField(2), BuilderWidgets.rowButton(2), BuilderWidgets.rowNote(2),
                BuilderWidgets.toggles(2, 2), BuilderWidgets.toggles(2, 3)), layer(BuilderWidgets.row(2)).children());
    }

    @Test
    void twoToggleButtonsSplitTheFieldAndButtonsPlace() {
        WidgetSpec second = SPECS.get(BuilderWidgets.toggleFace(0, 2, 1));

        assertEquals(List.of(88, 92, BuilderWidgets.TOGGLE_ROOM), List.of(second.width(), second.x(), second.x() + second.width()));
    }

    @Test
    void threeToggleButtonsAreNarrower() {
        WidgetSpec third = SPECS.get(BuilderWidgets.toggleFace(0, 3, 2));

        assertEquals(List.of(57, 122), List.of(third.width(), third.x()));
    }

    @Test
    void aToggleButtonDrawsItsFaceThenItsFrameThenItsWord() {
        assertEquals(List.of(BuilderWidgets.toggleFace(4, 2, 0), BuilderWidgets.toggleFrame(4, 2, 0), BuilderWidgets.toggleText(4, 2, 0),
                BuilderWidgets.toggleFace(4, 2, 1), BuilderWidgets.toggleFrame(4, 2, 1), BuilderWidgets.toggleText(4, 2, 1)),
                layer(BuilderWidgets.toggles(4, 2)).children());
    }

    @Test
    void aToggleButtonIsClickedAndNotDragged() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.toggleFace(0, 3, 1)));

        assertEquals(List.of("Select", false), List.of(face.option(), face.draggable()));
    }

    @Test
    void aListScrollsOnTheConfigureScreenOverItsAddLineAndItsLines() {
        WidgetSpec.Layer list = layer(BuilderWidgets.list(0));

        assertEquals(List.of(BuilderWidgets.CONFIGURE, BuilderWidgets.LIST_LINES + 1, BuilderWidgets.listAddLine(0), BuilderWidgets.line(0, 27)),
                List.of(list.parent(), list.children().size(), list.children().get(0), list.children().get(BuilderWidgets.LIST_LINES)));
    }

    @Test
    void theListsComeAfterTheRowsSoTheyDrawOverThem() {
        List<Integer> children = layer(BuilderWidgets.CONFIGURE).children();

        assertTrue(children.indexOf(BuilderWidgets.list(0)) > children.indexOf(BuilderWidgets.row(BuilderWidgets.ROWS - 1)));
    }

    @Test
    void theAddLineOpensTheSearch() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.listAdd(1)));

        assertEquals(List.of("Search", BuilderWidgets.LIST_WIDTH), List.of(face.option(), face.width()));
    }

    @Test
    void aLineHoldsItsBoxPictureNameAmountAndRemoveButton() {
        assertEquals(List.of(BuilderWidgets.lineBox(0, 3), BuilderWidgets.linePicture(0, 3), BuilderWidgets.lineName(0, 3),
                BuilderWidgets.lineAmount(0, 3), BuilderWidgets.lineRemoveFace(0, 3), BuilderWidgets.lineRemoveText(0, 3)),
                layer(BuilderWidgets.line(0, 3)).children());
    }

    @Test
    void linesFollowTheAddLineOneUnderTheOther() {
        assertEquals(List.of(22, 44), List.of(SPECS.get(BuilderWidgets.line(1, 0)).y(), SPECS.get(BuilderWidgets.line(1, 1)).y()));
    }

    @Test
    void aWithdrawalsAmountHoldsItsBoxAndAllButton() {
        assertEquals(List.of(BuilderWidgets.lineAmountFace(0, 0), BuilderWidgets.lineAmountFrame(0, 0), BuilderWidgets.lineAmountText(0, 0),
                BuilderWidgets.lineAllFace(0, 0), BuilderWidgets.lineAllText(0, 0)), layer(BuilderWidgets.lineAmount(0, 0)).children());
    }

    @Test
    void theRemoveButtonTakesTheItemOut() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.lineRemoveFace(0, 0)));

        assertEquals("Remove", face.option());
    }

    @Test
    void aPlacedListSitsOnItsRowsInTheFieldsPlaceAsTallAsItsLines() {
        BuilderWidgets.ListPlacement placement = BuilderWidgets.listPlacement(0, 4, 2, 2);

        assertEquals(new BuilderWidgets.ListPlacement(BuilderWidgets.FIELD_X, 162, 42, 42), placement);
    }

    @Test
    void aListTallerThanItsRowsStopsAtTheirBottom() {
        assertEquals(49, BuilderWidgets.listPlacement(0, 4, 2, 9).height());
    }

    @Test
    void theRightListSitsInTheRightColumn() {
        assertEquals(BuilderWidgets.RIGHT_COLUMN + BuilderWidgets.FIELD_X, BuilderWidgets.listPlacement(1, 0, 1, 1).x());
    }

    @Test
    void aListOfManyLinesScrollsOverThemAll() {
        assertEquals(10 * 22 - 2, BuilderWidgets.listPlacement(0, 1, 5, 10).scrollHeight());
    }

    @Test
    void aScrollPositionStaysWhileTheContentAllowsIt() {
        assertEquals(30, BuilderWidgets.listPlacement(0, 1, 5, 10).clampedScroll(30));
    }

    @Test
    void aScrollPositionPastTheContentComesBack() {
        BuilderWidgets.ListPlacement placement = BuilderWidgets.listPlacement(0, 1, 5, 10);

        assertEquals(placement.scrollHeight() - placement.height(), placement.clampedScroll(500));
    }

    @Test
    void aListThatFitsDoesNotScroll() {
        assertEquals(0, BuilderWidgets.listPlacement(0, 1, 5, 2).clampedScroll(40));
    }

    @Test
    void placingAListKeepsTheOther() {
        BuilderWidgets.ListPlacement placement = BuilderWidgets.listPlacement(1, 2, 4, 3);

        assertEquals(List.of(BuilderWidgets.unplacedLists().get(0), placement),
                BuilderWidgets.placed(BuilderWidgets.unplacedLists(), 1, placement));
    }

    @Test
    void theSpecsPutAListWhereItWasPlaced() {
        BuilderWidgets.ListPlacement placement = BuilderWidgets.listPlacement(0, 3, 3, 9);
        Map<Integer, WidgetSpec> specs = BuilderWidgets.specs(4, 3, BuilderWidgets.placed(BuilderWidgets.unplacedLists(), 0, placement));
        WidgetSpec.Layer list = assertInstanceOf(WidgetSpec.Layer.class, specs.get(BuilderWidgets.list(0)));

        assertEquals(List.of(placement.y(), placement.height(), placement.scrollHeight()), List.of(list.y(), list.height(), list.scrollHeight()));
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

    @Test
    void theOverviewsTabsSitAboveTheSlotsStepsFirst() {
        WidgetSpec steps = SPECS.get(BuilderWidgets.STEPS_TAB);
        WidgetSpec reflexes = SPECS.get(BuilderWidgets.REFLEXES_TAB);

        assertTrue(steps.x() + steps.width() < reflexes.x());
        assertTrue(reflexes.y() + reflexes.height() <= SPECS.get(BuilderWidgets.SLOTS).y());
    }

    @Test
    void eachTabHasAFrameTheServerLights() {
        assertEquals(List.of(BuilderWidgets.OVERVIEW, BuilderWidgets.OVERVIEW),
                List.of(SPECS.get(BuilderWidgets.STEPS_TAB_FRAME).parent(), SPECS.get(BuilderWidgets.REFLEXES_TAB_FRAME).parent()));
    }

    @Test
    void theStatusLineSitsUnderTheSlotArea() {
        WidgetSpec slots = SPECS.get(BuilderWidgets.SLOTS);

        assertTrue(SPECS.get(BuilderWidgets.STATUS).y() >= slots.y() + slots.height());
    }

    @Test
    void theReflexRowsTakeTheSlotAreasPlace() {
        WidgetSpec slots = SPECS.get(BuilderWidgets.SLOTS);
        WidgetSpec.Layer rows = layer(BuilderWidgets.REFLEX_ROWS);

        assertEquals(List.of(slots.x(), slots.y(), slots.width(), slots.height(), BuilderWidgets.OVERVIEW),
                List.of(rows.x(), rows.y(), rows.width(), rows.height(), rows.parent()));
    }

    @Test
    void theReflexAreaHoldsEveryRowThenThePadlock() {
        assertEquals(List.of(BuilderWidgets.reflexRow(0), BuilderWidgets.reflexRow(1), BuilderWidgets.reflexRow(2), BuilderWidgets.reflexRow(3)),
                layer(BuilderWidgets.REFLEX_ROWS).children());
    }

    @Test
    void reflexRowsAreARowAndAGapApart() {
        assertEquals(BuilderWidgets.REFLEX_ROW_HEIGHT + BuilderWidgets.REFLEX_ROW_GAP, SPECS.get(BuilderWidgets.reflexRow(1)).y());
    }

    @Test
    void aReflexRowShowsItsNumberPictureAndSentence() {
        assertEquals(List.of(BuilderWidgets.reflexRowFace(0), BuilderWidgets.reflexRowFrame(0), BuilderWidgets.reflexRowNumber(0),
                BuilderWidgets.reflexRowPicture(0), BuilderWidgets.reflexRowText(0)), layer(BuilderWidgets.reflexRow(0)).children());
    }

    @Test
    void aReflexRowDroppedOnAnotherMovesToItsPlace() {
        assertEquals(Optional.of(new TileDrag.Move(0, 2, BuilderWidgets.REFLEX_ROWS)),
                new TileDrag(SPECS).drop(BuilderWidgets.reflexRowFace(0), BuilderWidgets.reflexRowText(2)));
    }

    @Test
    void theReflexPadlockShowsTheKeysAndCannotBeDragged() {
        assertEquals("keys", assertInstanceOf(WidgetSpec.Sprite.class, SPECS.get(BuilderWidgets.reflexLockedSprite(3))).name());
        assertTrue(new TileDrag(SPECS).tileOf(BuilderWidgets.reflexRow(3) + 1).isEmpty());
    }

    @Test
    void aFewReflexRowsShowWithoutScrolling() {
        assertFalse(layer(BuilderWidgets.REFLEX_ROWS).scrolls());
    }

    @Test
    void moreReflexRowsThanTheAreaHoldsScroll() {
        WidgetSpec.Layer rows = assertInstanceOf(WidgetSpec.Layer.class, BuilderWidgets.specs(6, 9, BuilderWidgets.unplacedLists()).get(BuilderWidgets.REFLEX_ROWS));

        assertEquals(10 * (BuilderWidgets.REFLEX_ROW_HEIGHT + BuilderWidgets.REFLEX_ROW_GAP) - BuilderWidgets.REFLEX_ROW_GAP, rows.scrollHeight());
    }

    @Test
    void everyReflexRowWidgetStaysInsideItsStride() {
        assertTrue(BuilderWidgets.reflexRowText(0) < BuilderWidgets.reflexRow(1));
    }
}
