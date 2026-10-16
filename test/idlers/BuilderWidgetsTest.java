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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void fiveToggleButtonsMirrorTheServersIds() {
        assertEquals(List.of(42000, 42016, 42029, 42030, 42031, 42176),
                List.of(BuilderWidgets.toggles(0, 5), BuilderWidgets.toggles(1, 5), BuilderWidgets.toggleFace(1, 5, 4),
                        BuilderWidgets.toggleFrame(1, 5, 4), BuilderWidgets.toggleText(1, 5, 4), BuilderWidgets.toggles(BuilderWidgets.ROWS - 1, 5)));
    }

    @Test
    void everyFiveButtonWidgetStaysInsideItsStride() {
        assertTrue(BuilderWidgets.toggleText(0, 5, 4) < BuilderWidgets.toggles(1, 5));
    }

    @Test
    void theLastRowsFiveButtonsEndBeforeTheStopsRow() {
        assertTrue(BuilderWidgets.toggleText(BuilderWidgets.ROWS - 1, 5, 4) < BuilderWidgets.STOPS);
    }

    @Test
    void theFiveButtonSetsLiePastTheReflexesPagesLinesAtTheMostReflexes() {
        assertTrue(BuilderWidgets.detachText(98) < BuilderWidgets.toggles(0, 5));
    }

    @Test
    void aToggleOfAnotherSizeIsAMistake() {
        assertThrows(IllegalArgumentException.class, () -> BuilderWidgets.toggles(0, 6));
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
                BuilderWidgets.toggles(2, 1), BuilderWidgets.toggles(2, 2), BuilderWidgets.toggles(2, 3), BuilderWidgets.toggles(2, 4), BuilderWidgets.toggles(2, 5)), layer(BuilderWidgets.row(2)).children());
    }

    @Test
    void theFiveButtonsAreALayerOfTheirRowInTheFieldAndButtonsPlace() {
        WidgetSpec.Layer toggles = layer(BuilderWidgets.toggles(3, 5));

        assertEquals(List.of(BuilderWidgets.row(3), BuilderWidgets.FIELD_X, BuilderWidgets.TOGGLE_ROOM),
                List.of(toggles.parent(), toggles.x(), toggles.width()));
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
    void fiveToggleButtonsAreNarrowerStill() {
        WidgetSpec fifth = SPECS.get(BuilderWidgets.toggleFace(0, 5, 4));

        assertEquals(List.of(32, 144), List.of(fifth.width(), fifth.x()));
    }

    @Test
    void aFiveButtonSetsButtonIsClickedAndNotDragged() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.toggleFace(7, 5, 2)));

        assertEquals(List.of("Select", false), List.of(face.option(), face.draggable()));
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

        assertEquals(List.of(BuilderWidgets.unplacedLists().get(0), placement, BuilderWidgets.unplacedLists().get(2)),
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
    void theStopsRowMirrorsTheServersIds() {
        assertEquals(List.of(42200, 42201, 42202, 42203, 42208, 42209, 42210, 42211, 42212, 42213),
                List.of(BuilderWidgets.STOPS, BuilderWidgets.STOPS_LABEL, BuilderWidgets.stopFrame(0), BuilderWidgets.stopChoice(0),
                        BuilderWidgets.stopFrame(BuilderWidgets.STOP_CHOICES - 1), BuilderWidgets.stopChoice(BuilderWidgets.STOP_CHOICES - 1),
                        BuilderWidgets.STOPS_AFTER, BuilderWidgets.STOPS_AFTER_FACE, BuilderWidgets.STOPS_AFTER_FRAME,
                        BuilderWidgets.STOPS_AFTER_TEXT));
    }

    @Test
    void theStopsRowIsALayerOfTheOverviewAcrossItsWidth() {
        WidgetSpec.Layer stops = layer(BuilderWidgets.STOPS);

        assertEquals(List.of(BuilderWidgets.OVERVIEW, 4, 260, 492, 20), List.of(stops.parent(), stops.x(), stops.y(), stops.width(), stops.height()));
    }

    @Test
    void theStopsRowSitsBetweenTheStatusLineAndLevels() {
        WidgetSpec status = SPECS.get(BuilderWidgets.STATUS);
        WidgetSpec stops = SPECS.get(BuilderWidgets.STOPS);

        assertTrue(status.y() + status.height() < stops.y());
        assertTrue(stops.y() + stops.height() < SPECS.get(BuilderWidgets.BASE_LEVELS_FRAME).y());
    }

    @Test
    void theOverviewHoldsTheStopsRowAfterTheStatusLineAndBeforeLevels() {
        List<Integer> children = layer(BuilderWidgets.OVERVIEW).children();

        assertEquals(List.of(children.indexOf(BuilderWidgets.STATUS) + 1, children.indexOf(BuilderWidgets.STOPS) + 1),
                List.of(children.indexOf(BuilderWidgets.STOPS), children.indexOf(BuilderWidgets.LEVELS)));
    }

    @Test
    void theStopsRowHoldsItsLabelThenAFrameAndAButtonPerChoiceThenTheAfterField() {
        assertEquals(List.of(BuilderWidgets.STOPS_LABEL, BuilderWidgets.stopFrame(0), BuilderWidgets.stopChoice(0), BuilderWidgets.stopFrame(1),
                BuilderWidgets.stopChoice(1), BuilderWidgets.stopFrame(2), BuilderWidgets.stopChoice(2), BuilderWidgets.stopFrame(3),
                BuilderWidgets.stopChoice(3), BuilderWidgets.STOPS_AFTER), layer(BuilderWidgets.STOPS).children());
    }

    @Test
    void theStopsLabelIsWrittenAsTheLevelsLabel() {
        WidgetSpec.Text label = assertInstanceOf(WidgetSpec.Text.class, SPECS.get(BuilderWidgets.STOPS_LABEL));
        WidgetSpec.Text levels = assertInstanceOf(WidgetSpec.Text.class, SPECS.get(BuilderWidgets.LEVELS));

        assertEquals(List.of("Flow stops:", 0, 2, 60, levels.height(), levels.colour(), levels.font()),
                List.of(label.text(), label.x(), label.y(), label.width(), label.height(), label.colour(), label.font()));
    }

    @Test
    void theStopChoicesFramesGoLeftToRight() {
        List<WidgetSpec> frames = List.of(SPECS.get(BuilderWidgets.stopFrame(0)), SPECS.get(BuilderWidgets.stopFrame(1)),
                SPECS.get(BuilderWidgets.stopFrame(2)), SPECS.get(BuilderWidgets.stopFrame(3)));

        assertEquals(List.of(62, 40, 106, 66, 176, 68, 248, 136),
                List.of(frames.get(0).x(), frames.get(0).width(), frames.get(1).x(), frames.get(1).width(), frames.get(2).x(), frames.get(2).width(),
                        frames.get(3).x(), frames.get(3).width()));
    }

    @Test
    void aStopChoicesFrameIsTheRowsHeightInTheTabsEdgeColour() {
        WidgetSpec.Box frame = assertInstanceOf(WidgetSpec.Box.class, SPECS.get(BuilderWidgets.stopFrame(2)));

        assertEquals(List.of(BuilderWidgets.STOPS, 0, 18, FlowWidgets.EDGE, false),
                List.of(frame.parent(), frame.y(), frame.height(), frame.colour(), frame.filled()));
    }

    @Test
    void aStopChoicesButtonSitsInsideItsFrame() {
        WidgetSpec button = SPECS.get(BuilderWidgets.stopChoice(3));

        assertEquals(List.of(BuilderWidgets.STOPS, 251, 2, 130), List.of(button.parent(), button.x(), button.y(), button.width()));
    }

    @Test
    void theStopsRowsLayerReachesTwoRowsBelowItsFramesSoDescendersStayWhole() {
        WidgetSpec stops = SPECS.get(BuilderWidgets.STOPS);
        WidgetSpec frame = SPECS.get(BuilderWidgets.stopFrame(3));

        assertEquals(frame.y() + frame.height() + 2, stops.height());
    }

    @Test
    void theStopChoicesSayWhatTheyDo() {
        List<WidgetSpec.Button> buttons = List.of(assertInstanceOf(WidgetSpec.Button.class, SPECS.get(BuilderWidgets.stopChoice(0))),
                assertInstanceOf(WidgetSpec.Button.class, SPECS.get(BuilderWidgets.stopChoice(1))),
                assertInstanceOf(WidgetSpec.Button.class, SPECS.get(BuilderWidgets.stopChoice(2))),
                assertInstanceOf(WidgetSpec.Button.class, SPECS.get(BuilderWidgets.stopChoice(3))));

        assertEquals(List.of("Never", "Never stop", "After laps", "Stop after a number of laps", "After time", "Stop after a number of minutes",
                        "When all goals are met", "Stop once every step met its goal"),
                List.of(buttons.get(0).text(), buttons.get(0).option(), buttons.get(1).text(), buttons.get(1).option(), buttons.get(2).text(),
                        buttons.get(2).option(), buttons.get(3).text(), buttons.get(3).option()));
    }

    @Test
    void theAfterFieldIsALayerRightOfTheLastChoiceSoItHides() {
        WidgetSpec.Layer after = layer(BuilderWidgets.STOPS_AFTER);
        WidgetSpec last = SPECS.get(BuilderWidgets.stopFrame(BuilderWidgets.STOP_CHOICES - 1));

        assertEquals(List.of(BuilderWidgets.STOPS, 388, 0, 64, 18,
                        List.of(BuilderWidgets.STOPS_AFTER_FACE, BuilderWidgets.STOPS_AFTER_FRAME, BuilderWidgets.STOPS_AFTER_TEXT)),
                List.of(after.parent(), after.x(), after.y(), after.width(), after.height(), after.children()));
        assertTrue(last.x() + last.width() < after.x());
    }

    @Test
    void theAfterFieldEndsInsideTheStopsRow() {
        WidgetSpec after = SPECS.get(BuilderWidgets.STOPS_AFTER);

        assertTrue(after.x() + after.width() <= SPECS.get(BuilderWidgets.STOPS).width());
    }

    @Test
    void theAfterFieldIsClickedToChangeAndNotDragged() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.STOPS_AFTER_FACE));

        assertEquals(List.of("Change", false, BuilderWidgets.FIELD, BuilderWidgets.FIELD_HOVER, 64, 18),
                List.of(face.option(), face.draggable(), face.colour(), face.hoverColour(), face.width(), face.height()));
    }

    @Test
    void theAfterFieldsFrameIsTheTilesEdge() {
        WidgetSpec.Box frame = assertInstanceOf(WidgetSpec.Box.class, SPECS.get(BuilderWidgets.STOPS_AFTER_FRAME));

        assertEquals(List.of(0, 0, 64, 18, BuilderWidgets.TILE_EDGE, false),
                List.of(frame.x(), frame.y(), frame.width(), frame.height(), frame.colour(), frame.filled()));
    }

    @Test
    void theAfterFieldsTextIsCentredInIt() {
        WidgetSpec.Text text = assertInstanceOf(WidgetSpec.Text.class, SPECS.get(BuilderWidgets.STOPS_AFTER_TEXT));

        assertEquals(List.of(32, 3, "", FlowWidgets.WHITE, WidgetSpec.FONT_SMALL, true),
                List.of(text.x(), text.y(), text.text(), text.colour(), text.font(), text.centred()));
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

    @Test
    void theStepTabsAndTheReflexesPageMirrorTheServersIds() {
        assertEquals(List.of(30726, 30727, 30816, 30817, 30818, 30819, 30820, 40900, 40902, 40904, 40905, 40907, 40919, 40920, 40921, 40922, 40923),
                List.of(BuilderWidgets.ATTACH_ALL_REFLEXES_LAYER, BuilderWidgets.ATTACH_ALL_REFLEXES, BuilderWidgets.STEP_TABS,
                        BuilderWidgets.SETTINGS_TAB, BuilderWidgets.SETTINGS_TAB_FRAME, BuilderWidgets.STEP_REFLEXES_TAB,
                        BuilderWidgets.STEP_REFLEXES_TAB_FRAME, BuilderWidgets.list(BuilderWidgets.ATTACHED_LIST), BuilderWidgets.ATTACH,
                        BuilderWidgets.ATTACH_TEXT, BuilderWidgets.ATTACH_ALL, BuilderWidgets.ATTACH_ALL_TEXT, BuilderWidgets.attachedFace(1),
                        BuilderWidgets.attachedNumber(1), BuilderWidgets.attachedPicture(1), BuilderWidgets.attachedSentence(1),
                        BuilderWidgets.detachFace(1)));
    }

    @Test
    void attachAllReflexesSitsInItsOwnLayerAtTheRightOfTheTabsRow() {
        WidgetSpec.Layer button = layer(BuilderWidgets.ATTACH_ALL_REFLEXES_LAYER);
        WidgetSpec reflexes = SPECS.get(BuilderWidgets.REFLEXES_TAB_FRAME);

        assertEquals(List.of(BuilderWidgets.OVERVIEW, List.of(BuilderWidgets.ATTACH_ALL_REFLEXES), BuilderWidgets.LAYER_WIDTH - 4),
                List.of(button.parent(), button.children(), button.x() + button.width()));
        assertTrue(reflexes.x() + reflexes.width() < button.x());
    }

    @Test
    void theStepTabsSitAtTheRightOfTheNameLineInALayerThatHides() {
        WidgetSpec.Layer tabs = layer(BuilderWidgets.STEP_TABS);
        WidgetSpec name = SPECS.get(BuilderWidgets.HEADER_NAME);

        assertEquals(List.of(BuilderWidgets.CONFIGURE, 0, BuilderWidgets.LAYER_WIDTH - 2), List.of(tabs.parent(), tabs.y(), tabs.x() + tabs.width()));
        assertTrue(name.x() + name.width() < tabs.x());
    }

    @Test
    void eachStepTabHasAFrameTheServerLightsThenItsButton() {
        assertEquals(List.of(BuilderWidgets.SETTINGS_TAB_FRAME, BuilderWidgets.SETTINGS_TAB, BuilderWidgets.STEP_REFLEXES_TAB_FRAME,
                BuilderWidgets.STEP_REFLEXES_TAB), layer(BuilderWidgets.STEP_TABS).children());
    }

    @Test
    void theReflexesPageIsPlacedOverTheRowsAcrossTheScreen() {
        BuilderWidgets.ListPlacement placement = BuilderWidgets.listPlacement(BuilderWidgets.ATTACHED_LIST, 0, BuilderWidgets.ROWS_PER_COLUMN, 3);

        assertEquals(List.of(0, BuilderWidgets.ROW_TOP, 3 * 22 - 2), List.of(placement.x(), placement.y(), placement.height()));
    }

    @Test
    void theReflexesPageComesAfterTheListsSoItDrawsOverTheRows() {
        List<Integer> children = layer(BuilderWidgets.CONFIGURE).children();

        assertEquals(children.indexOf(BuilderWidgets.list(1)) + 1, children.indexOf(BuilderWidgets.ATTACHED));
    }

    @Test
    void theReflexesPageHoldsItsAttachLineThenALinePerReflexSlot() {
        WidgetSpec.Layer page = layer(BuilderWidgets.ATTACHED);

        assertEquals(List.of(BuilderWidgets.ATTACH_LINE, BuilderWidgets.attachedLine(0), BuilderWidgets.attachedLine(1), BuilderWidgets.attachedLine(2)),
                page.children());
        assertEquals(BuilderWidgets.ATTACHED_WIDTH, page.width());
    }

    @Test
    void theAttachLineOpensTheSearchBesideAttachAll() {
        WidgetSpec.Tile search = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.ATTACH));
        WidgetSpec.Tile all = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.ATTACH_ALL));

        assertEquals(List.of("Search", "Attach all", BuilderWidgets.ATTACHED_WIDTH), List.of(search.option(), all.option(), all.x() + all.width()));
        assertTrue(search.x() + search.width() < all.x());
    }

    @Test
    void theAttachLineAndAttachAllAreClickedAndNotDragged() {
        TileDrag drag = new TileDrag(SPECS);

        assertEquals(List.of(Optional.empty(), Optional.empty(), Optional.empty()),
                List.of(drag.tileOf(BuilderWidgets.ATTACH), drag.tileOf(BuilderWidgets.ATTACH_ALL), drag.tileOf(BuilderWidgets.detachFace(0))));
    }

    @Test
    void aReflexLineShowsItsNumberPictureSentenceAndDetachButton() {
        assertEquals(List.of(BuilderWidgets.attachedFace(1), BuilderWidgets.attachedNumber(1), BuilderWidgets.attachedPicture(1),
                BuilderWidgets.attachedSentence(1), BuilderWidgets.detachFace(1), BuilderWidgets.detachText(1)),
                layer(BuilderWidgets.attachedLine(1)).children());
    }

    @Test
    void aReflexLineMovesByDragAndItsXDetaches() {
        WidgetSpec.Tile face = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.attachedFace(0)));
        WidgetSpec.Tile x = assertInstanceOf(WidgetSpec.Tile.class, SPECS.get(BuilderWidgets.detachFace(0)));

        assertEquals(List.of("Move", true, "Detach"), List.of(face.option(), face.draggable(), x.option()));
    }

    @Test
    void reflexLinesFollowTheAttachLineOneUnderTheOther() {
        assertEquals(List.of(22, 44), List.of(SPECS.get(BuilderWidgets.attachedLine(0)).y(), SPECS.get(BuilderWidgets.attachedLine(1)).y()));
    }

    @Test
    void aReflexLineDroppedOnAnotherMovesToItsPlace() {
        assertEquals(Optional.of(new TileDrag.Move(0, 2, BuilderWidgets.ATTACHED)),
                new TileDrag(SPECS).drop(BuilderWidgets.attachedFace(0), BuilderWidgets.attachedSentence(2)));
    }

    @Test
    void aReflexLinesSentenceHasTheRoomTheServerFitsItTo() {
        assertTrue(SPECS.get(BuilderWidgets.attachedSentence(0)).width() >= 420);
    }

    @Test
    void everyReflexLineWidgetStaysInsideItsStride() {
        assertTrue(BuilderWidgets.detachText(0) < BuilderWidgets.attachedLine(1));
    }

    @Test
    void theReflexesPageLiesPastTheMostReflexRows() {
        assertTrue(BuilderWidgets.reflexLockedSprite(99) < BuilderWidgets.ATTACHED);
    }

    @Test
    void oneAndFourButtonSetsMirrorTheServerWithoutOverlapping() {
        assertEquals(List.of(42300, 42344, 42400, 42543, 42555), List.of(BuilderWidgets.toggles(0, 1), BuilderWidgets.toggles(11, 1), BuilderWidgets.toggles(0, 4), BuilderWidgets.toggles(11, 4), BuilderWidgets.toggleText(11, 4, 3)));
        assertTrue(BuilderWidgets.STOPS_AFTER_TEXT < BuilderWidgets.toggles(0, 1));
        assertTrue(BuilderWidgets.toggleText(11, 1, 0) < BuilderWidgets.toggles(0, 4));
    }

    @Test
    void aSingleToggleFillsTheAvailableWidth() {
        WidgetSpec face = SPECS.get(BuilderWidgets.toggleFace(1, 1, 0));

        assertEquals(List.of(0, BuilderWidgets.TOGGLE_ROOM), List.of(face.x(), face.width()));
    }

    @Test
    void fourTogglesDivideTheAvailableWidthWithGaps() {
        WidgetSpec last = SPECS.get(BuilderWidgets.toggleFace(6, 4, 3));

        assertEquals(List.of(42, 138, BuilderWidgets.TOGGLE_ROOM), List.of(last.width(), last.x(), last.x() + last.width()));
    }

}
