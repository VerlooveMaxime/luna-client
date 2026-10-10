package idlers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static idlers.WidgetSpec.FONT_BOLD;
import static idlers.WidgetSpec.FONT_PLAIN;
import static idlers.WidgetSpec.FONT_SMALL;
import static idlers.WidgetSpecs.add;

/**
 * The flow builder's screens (flow builder v2, S06): one root holding a layer per screen, which the server shows one
 * at a time (packet 82), so an open chatbox prompt survives the switch. The overview holds a slot per step slot the
 * player has, as many as the server says (packet 108; Maxime, 2026-10-09: no fixed most), then the padlock, and on its
 * Reflexes tab (S07c) a row per reflex the flow can hold, then the padlock; the kind picker a button per kind of step;
 * the configure screen a header, two columns of setting rows the server fills or hides, a warnings band and its
 * buttons, and for a step (S07c2b) the Settings and Reflexes tabs, the Reflexes page a line per reflex the flow can
 * hold. The server mirrors these ids in {@code game.idle.ui.BuilderWidgets}; text and pictures it changes start empty
 * here.
 */
public final class BuilderWidgets {

    public static final int FIRST_ID = 30700;
    /** The fixed ids end here; the slots follow from {@link #SLOT_BASE}. */
    public static final int ID_LIMIT = 32000;

    public static final int ROOT = 30700;
    public static final int PANEL = 30701;
    public static final int FRAME = 30702;
    public static final int TITLE = 30703;
    public static final int CLOSE = 30704;

    public static final int OVERVIEW = 30710;
    public static final int SLOTS = 30711;
    public static final int STATUS = 30712;
    public static final int LEVELS = 30713;
    public static final int BASE_LEVELS = 30714;
    public static final int BOOSTED_LEVELS = 30715;
    public static final int BASE_LEVELS_FRAME = 30716;
    public static final int BOOSTED_LEVELS_FRAME = 30717;
    public static final int RUN = 30718;
    public static final int STOP = 30719;
    public static final int CLEAR = 30720;
    public static final int SAVE_FLOW = 30721;
    /** The overview's tabs (S07c), each a frame the server lights and a button. */
    public static final int STEPS_TAB = 30722;
    public static final int STEPS_TAB_FRAME = 30723;
    public static final int REFLEXES_TAB = 30724;
    public static final int REFLEXES_TAB_FRAME = 30725;
    /** The Steps tab's button that attaches every reflex to every step (S07c2b), in a layer the Reflexes tab hides. */
    public static final int ATTACH_ALL_REFLEXES_LAYER = 30726;
    public static final int ATTACH_ALL_REFLEXES = 30727;

    public static final int KINDS = 30730;
    public static final int KINDS_TITLE = 30731;
    public static final int KINDS_BACK = 30732;
    public static final int KIND_BUTTONS = 16;
    private static final int KIND_BASE = 30736;
    private static final int KIND_STRIDE = 4;

    public static final int CONFIGURE = 30800;
    public static final int HEADER_PICTURE = 30801;
    public static final int HEADER_CORNER_LAYER = 30802;
    public static final int HEADER_CORNER_BOX = 30803;
    public static final int HEADER_CORNER = 30804;
    public static final int HEADER_NAME = 30805;
    public static final int HEADER_DESCRIPTION = 30806;
    public static final int TOP_DIVIDER = 30807;
    public static final int BOTTOM_DIVIDER = 30808;
    public static final int WARNING_LINES = 3;
    private static final int WARNING_BASE = 30810;
    public static final int DELETE = 30813;
    public static final int BACK = 30814;
    public static final int SAVE = 30815;
    /** A step screen's tabs (S07c2b), in a layer a reflex's screen hides, each a button and a frame the server lights. */
    public static final int STEP_TABS = 30816;
    public static final int SETTINGS_TAB = 30817;
    public static final int SETTINGS_TAB_FRAME = 30818;
    public static final int STEP_REFLEXES_TAB = 30819;
    public static final int STEP_REFLEXES_TAB_FRAME = 30820;
    /** Six rows a column (Maxime, 2026-10-10). */
    public static final int ROWS_PER_COLUMN = 6;
    public static final int ROWS = 2 * ROWS_PER_COLUMN;
    private static final int ROW_BASE = 30830;
    private static final int ROW_STRIDE = 30;
    /** A toggle row's two sets of buttons, the server showing the one of its size. */
    private static final int TWO_BUTTONS = 13;
    private static final int THREE_BUTTONS = 20;

    /** A scrolling list per column (S07a): a line that adds or removes, then a line per item. */
    public static final int LISTS = 2;
    public static final int LIST_LINES = 28;
    private static final int LIST_BASE = 31200;
    private static final int LIST_STRIDE = 400;
    private static final int LINE_BASE = 10;
    private static final int LINE_STRIDE = 12;

    /**
     * A step's Reflexes page (S07c2b): a list the server places as it places the columns' lists, under index
     * {@link #ATTACHED_LIST}: a line that attaches or detaches beside Attach all, then a line per reflex.
     */
    public static final int ATTACHED_LIST = 2;
    /** The lists the server places: the columns' and the Reflexes page's. */
    public static final int PLACED_LISTS = 3;
    public static final int ATTACHED = 40900;
    public static final int ATTACH_LINE = 40901;
    public static final int ATTACH = 40902;
    public static final int ATTACH_FRAME = 40903;
    public static final int ATTACH_TEXT = 40904;
    public static final int ATTACH_ALL = 40905;
    public static final int ATTACH_ALL_FRAME = 40906;
    public static final int ATTACH_ALL_TEXT = 40907;
    private static final int ATTACHED_LINE_BASE = 40910;
    private static final int ATTACHED_LINE_STRIDE = 8;

    public static final int SLOT_BASE = 32000;
    public static final int SLOT_STRIDE = 16;
    public static final int SLOT_LINES = 4;

    /** The Reflexes tab's rows (S07c): a scrolling layer, then a row per reflex slot and the padlock. */
    public static final int REFLEX_ROWS = 40000;
    private static final int REFLEX_ROW_BASE = 40016;
    private static final int REFLEX_ROW_STRIDE = 8;

    public static final int SCREEN_WIDTH = 512;
    public static final int SCREEN_HEIGHT = 334;
    /** Where each screen's layer sits in the root, under the title. */
    public static final int LAYER_X = 6;
    public static final int LAYER_Y = 24;
    public static final int LAYER_WIDTH = 500;
    public static final int LAYER_HEIGHT = 304;

    /** The mockup's slots: four to a row in an area two rows high that scrolls. */
    public static final int SLOT_WIDTH = 116;
    public static final int SLOT_HEIGHT = 104;
    public static final int SLOT_GAP = 5;
    public static final int COLUMNS = 4;
    public static final int SLOTS_X = 2;
    /** Under the tabs. */
    public static final int SLOTS_Y = 22;
    public static final int SLOTS_HEIGHT = 216;
    public static final int SLOTS_WIDTH = COLUMNS * SLOT_WIDTH + (COLUMNS - 1) * SLOT_GAP;
    public static final int TAB_HEIGHT = 18;

    /** A reflex row: its number, picture and sentence, the full width of the slots. */
    public static final int REFLEX_ROW_HEIGHT = 24;
    public static final int REFLEX_ROW_GAP = 3;
    public static final int REFLEX_PICTURE = 20;
    public static final int SENTENCE_X = 46;
    public static final int BIG_PICTURE = 34;
    public static final int CORNER_PICTURE = 16;
    public static final int KIND_PICTURE = 25;

    /** The mockup's configure rows: a label, a field box, and the amount's button, 28 px apart in two columns. */
    public static final int ROW_TOP = 50;
    public static final int ROW_HEIGHT = 28;
    public static final int RIGHT_COLUMN = 252;
    public static final int LABEL_WIDTH = 62;
    public static final int FIELD_X = 64;
    public static final int FIELD_WIDTH = 128;
    public static final int FIELD_HEIGHT = 21;
    public static final int FIELD_PICTURE = 18;
    public static final int ROW_BUTTON_X = 196;
    public static final int ROW_BUTTON_WIDTH = 48;
    /** A toggle's buttons fill the field's place and the button's: two wide ones, or three. */
    public static final int TOGGLE_ROOM = ROW_BUTTON_X + ROW_BUTTON_WIDTH - FIELD_X;
    public static final int TOGGLE_GAP = 4;
    /** A list's lines, the client's scrollbar right of them within the column. */
    public static final int LIST_WIDTH = TOGGLE_ROOM - 16;
    public static final int LINE_HEIGHT = 20;
    public static final int LINE_GAP = 2;
    public static final int AMOUNT_X = 84;
    public static final int AMOUNT_WIDTH = 34;
    public static final int ALL_X = 120;
    public static final int ALL_WIDTH = 26;
    public static final int REMOVE_X = 148;
    public static final int REMOVE_WIDTH = 14;
    public static final int NAME_X = 21;
    /** The Reflexes page spans the rows' area, the client's scrollbar right of it. */
    public static final int ATTACHED_WIDTH = LAYER_WIDTH - 16;
    public static final int ATTACH_ALL_WIDTH = 56;
    public static final int ATTACHED_PICTURE_X = 18;
    public static final int SENTENCE_LINE_X = 40;
    public static final int DETACH_X = ATTACHED_WIDTH - REMOVE_WIDTH - 4;
    /** The step tabs, at the right of the header's name line (Maxime, 2026-10-10). */
    public static final int SETTINGS_TAB_WIDTH = 58;
    public static final int REFLEXES_TAB_WIDTH = 62;
    public static final int STEP_TABS_WIDTH = SETTINGS_TAB_WIDTH + 6 + REFLEXES_TAB_WIDTH;
    public static final int ATTACH_ALL_REFLEXES_WIDTH = 116;
    public static final int HEADER_PICTURE_SIZE = 36;

    public static final int KIND_WIDTH = 64;
    public static final int KIND_HEIGHT = 52;
    public static final int KIND_GAP = 6;
    public static final int KIND_COLUMNS = 7;

    static final int TILE = 0x3b342a;
    static final int TILE_HOVER = 0x4a4134;
    static final int TILE_EDGE = 0x5c5243;
    static final int CORNER = 0x2a241c;
    static final int LOCKED = 0x2b261f;
    static final int NUMBER = 0xa89a7c;
    static final int FIELD = 0x1e1a14;
    static final int FIELD_HOVER = 0x2a241c;
    static final int BUTTON = 0x4a4031;
    static final int BUTTON_HOVER = 0x5a4e3c;
    static final int MUTED = 0xc8bfa8;

    private static final int LINE = 14;

    private BuilderWidgets() {
    }

    /**
     * The layer of kind button {@code kind}, which the server hides past the last kind: the client hides only layers
     * (packet 82 sets a flag its draw loop reads for layers alone).
     */
    public static int kindButton(int kind) {
        return KIND_BASE + kind * KIND_STRIDE;
    }

    public static int kindFace(int kind) {
        return kindButton(kind) + 1;
    }

    public static int kindPicture(int kind) {
        return kindButton(kind) + 2;
    }

    public static int kindLabel(int kind) {
        return kindButton(kind) + 3;
    }

    /** The layer of slot {@code slot}; slot {@code slots} is the padlock. */
    public static int slot(int slot) {
        return SLOT_BASE + slot * SLOT_STRIDE;
    }

    public static int slotFace(int slot) {
        return slot(slot) + 1;
    }

    /** The slot's frame, which the server colours (packet 218): the tile's edge, green running, red when it cannot work. */
    public static int slotFrame(int slot) {
        return slot(slot) + 2;
    }

    public static int slotPicture(int slot) {
        return slot(slot) + 3;
    }

    /** The layer holding the corner's box and picture, hidden when the slot shows no corner (only layers hide). */
    public static int slotCornerLayer(int slot) {
        return slot(slot) + 14;
    }

    public static int slotCornerBox(int slot) {
        return slot(slot) + 4;
    }

    public static int slotCorner(int slot) {
        return slot(slot) + 5;
    }

    public static int slotNumber(int slot) {
        return slot(slot) + 6;
    }

    public static int slotKind(int slot) {
        return slot(slot) + 7;
    }

    public static int slotLine(int slot, int line) {
        return slot(slot) + 8 + line;
    }

    public static int slotPlus(int slot) {
        return slot(slot) + 12;
    }

    public static int slotAdd(int slot) {
        return slot(slot) + 13;
    }

    public static int lockedSprite(int slots) {
        return slot(slots) + 3;
    }

    /** The layer of reflex row {@code row}; row {@code reflexSlots} is the padlock. */
    public static int reflexRow(int row) {
        return REFLEX_ROW_BASE + row * REFLEX_ROW_STRIDE;
    }

    /** The row's face, which opens its reflex and drags it onto another row (packet 123 names {@link #REFLEX_ROWS}). */
    public static int reflexRowFace(int row) {
        return reflexRow(row) + 1;
    }

    /** The row's frame, which the server colours: the tile's edge, red when the reflex cannot work. */
    public static int reflexRowFrame(int row) {
        return reflexRow(row) + 2;
    }

    public static int reflexRowNumber(int row) {
        return reflexRow(row) + 3;
    }

    public static int reflexRowPicture(int row) {
        return reflexRow(row) + 4;
    }

    public static int reflexRowText(int row) {
        return reflexRow(row) + 5;
    }

    public static int reflexLockedSprite(int reflexSlots) {
        return reflexRow(reflexSlots) + 3;
    }

    /** The layer of the Reflexes page's line {@code line}; lines past the last lie outside the placed list. */
    public static int attachedLine(int line) {
        return ATTACHED_LINE_BASE + line * ATTACHED_LINE_STRIDE;
    }

    /** The line's face, which drags it onto another line (packet 123 names {@link #ATTACHED}); a click does nothing. */
    public static int attachedFace(int line) {
        return attachedLine(line) + 1;
    }

    public static int attachedNumber(int line) {
        return attachedLine(line) + 2;
    }

    public static int attachedPicture(int line) {
        return attachedLine(line) + 3;
    }

    public static int attachedSentence(int line) {
        return attachedLine(line) + 4;
    }

    /** The x that detaches the line's reflex from the step. */
    public static int detachFace(int line) {
        return attachedLine(line) + 5;
    }

    public static int detachText(int line) {
        return attachedLine(line) + 6;
    }

    public static int warning(int line) {
        return WARNING_BASE + line;
    }

    /** The layer of configure row {@code row}: the left column's rows first, then the right's. */
    public static int row(int row) {
        return ROW_BASE + row * ROW_STRIDE;
    }

    public static int rowLabel(int row) {
        return row(row) + 1;
    }

    /** The layer of the row's field box, which the server hides on a note row. */
    public static int rowField(int row) {
        return row(row) + 2;
    }

    public static int rowFace(int row) {
        return row(row) + 3;
    }

    /** The field's frame, which the server colours yellow while the field is typed (packet 218). */
    public static int rowFrame(int row) {
        return row(row) + 4;
    }

    public static int rowPicture(int row) {
        return row(row) + 5;
    }

    /** The field's text right of its picture. */
    public static int rowText(int row) {
        return row(row) + 6;
    }

    /** The field's text where a field shows no picture. */
    public static int rowPlainText(int row) {
        return row(row) + 7;
    }

    /** The layer of the button right of the field, hidden on rows without one. */
    public static int rowButton(int row) {
        return row(row) + 8;
    }

    public static int rowButtonFace(int row) {
        return row(row) + 9;
    }

    public static int rowButtonText(int row) {
        return row(row) + 10;
    }

    public static int rowNote(int row) {
        return row(row) + 11;
    }

    public static int rowButtonFrame(int row) {
        return row(row) + 12;
    }

    /** The layer of row {@code row}'s toggle buttons when it has {@code count} of them, two or three. */
    public static int toggles(int row, int count) {
        return row(row) + (count == 2 ? TWO_BUTTONS : THREE_BUTTONS);
    }

    public static int toggleFace(int row, int count, int button) {
        return toggles(row, count) + 1 + 3 * button;
    }

    /** The button's frame, which the server lights (packet 218). */
    public static int toggleFrame(int row, int count, int button) {
        return toggleFace(row, count, button) + 1;
    }

    public static int toggleText(int row, int count, int button) {
        return toggleFace(row, count, button) + 2;
    }

    /** The scrolling layer of list {@code list}: 0 the left column's, 1 the right's, 2 the Reflexes page. */
    public static int list(int list) {
        return list == ATTACHED_LIST ? ATTACHED : LIST_BASE + list * LIST_STRIDE;
    }

    /** The layer of the list's first line, which opens the search that adds or removes items. */
    public static int listAddLine(int list) {
        return list(list) + 1;
    }

    public static int listAdd(int list) {
        return list(list) + 2;
    }

    public static int listAddFrame(int list) {
        return list(list) + 3;
    }

    public static int listAddText(int list) {
        return list(list) + 4;
    }

    /** The layer of the list's line {@code line}, an item, which the server hides past the last. */
    public static int line(int list, int line) {
        return list(list) + LINE_BASE + line * LINE_STRIDE;
    }

    public static int lineBox(int list, int line) {
        return line(list, line) + 1;
    }

    public static int linePicture(int list, int line) {
        return line(list, line) + 2;
    }

    public static int lineName(int list, int line) {
        return line(list, line) + 3;
    }

    /** The layer of a withdrawal's amount box and All button, hidden in other lists. */
    public static int lineAmount(int list, int line) {
        return line(list, line) + 4;
    }

    public static int lineAmountFace(int list, int line) {
        return line(list, line) + 5;
    }

    public static int lineAmountFrame(int list, int line) {
        return line(list, line) + 6;
    }

    public static int lineAmountText(int list, int line) {
        return line(list, line) + 7;
    }

    public static int lineAllFace(int list, int line) {
        return line(list, line) + 8;
    }

    public static int lineAllText(int list, int line) {
        return line(list, line) + 9;
    }

    public static int lineRemoveFace(int list, int line) {
        return line(list, line) + 10;
    }

    public static int lineRemoveText(int list, int line) {
        return line(list, line) + 11;
    }

    /**
     * Where list {@code list} sits once the server placed it (packet 108 sub-opcode 2): on its column's field area from
     * row {@code firstRow}, as tall as its {@code lines} lines up to {@code rows} rows, scrolling past that. Lines past
     * the last lie outside it, so the server never hides them: the client neither draws nor clicks outside a layer.
     */
    public record ListPlacement(int x, int y, int height, int scrollHeight) {

        /** A scroll position kept as far as the new content allows. */
        public int clampedScroll(int scroll) {
            return Math.max(0, Math.min(scroll, scrollHeight - height));
        }
    }

    /** {@code lists} with list {@code list} at {@code placement}. */
    public static List<ListPlacement> placed(List<ListPlacement> lists, int list, ListPlacement placement) {
        List<ListPlacement> placed = new ArrayList<>(lists);
        placed.set(list, placement);
        return List.copyOf(placed);
    }

    /** Where the lists sit before the server places them: their whole area, room for every line. */
    public static List<ListPlacement> unplacedLists() {
        List<ListPlacement> placements = new ArrayList<>();
        for (int list = 0; list < PLACED_LISTS; list++) {
            placements.add(listPlacement(list, 0, ROWS_PER_COLUMN, LIST_LINES + 1));
        }
        return List.copyOf(placements);
    }

    public static ListPlacement listPlacement(int list, int firstRow, int rows, int lines) {
        int room = rows * ROW_HEIGHT - (ROW_HEIGHT - FIELD_HEIGHT);
        int content = lines * (LINE_HEIGHT + LINE_GAP) - LINE_GAP;
        return new ListPlacement(listX(list), ROW_TOP + firstRow * ROW_HEIGHT, Math.min(room, content), content);
    }

    private static int listX(int list) {
        return list == ATTACHED_LIST ? 0 : list * RIGHT_COLUMN + FIELD_X;
    }

    /**
     * The widgets for a player with {@code slots} step slots and {@code reflexSlots} reflex slots, the lists where the
     * server last placed them.
     */
    public static Map<Integer, WidgetSpec> specs(int slots, int reflexSlots, List<ListPlacement> lists) {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        // The panel comes first so the rest draws over it.
        add(specs, children, WidgetSpec.box(PANEL, ROOT, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, FlowWidgets.PANEL));
        add(specs, children, WidgetSpec.frame(FRAME, ROOT, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, FlowWidgets.EDGE));
        add(specs, children, WidgetSpec.centredText(TITLE, ROOT, SCREEN_WIDTH / 2, 6, LINE, "Flow builder", FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, button(CLOSE, ROOT, 460, 6, 42, "Close", "Close"));
        add(specs, children, overview(specs, slots, reflexSlots));
        add(specs, children, kinds(specs));
        add(specs, children, configure(specs, reflexSlots, lists));
        specs.put(ROOT, WidgetSpec.layer(ROOT, -1, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, children));
        return specs;
    }

    /** The rows of slots {@code slots} step slots and the padlock take. */
    public static int slotRows(int slots) {
        return slots / COLUMNS + 1;
    }

    private static WidgetSpec overview(Map<Integer, WidgetSpec> specs, int slots, int reflexSlots) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.frame(STEPS_TAB_FRAME, OVERVIEW, 2, 0, 44, TAB_HEIGHT, FlowWidgets.EDGE));
        add(specs, children, button(STEPS_TAB, OVERVIEW, 5, 2, 38, "Steps", "Show the steps"));
        add(specs, children, WidgetSpec.frame(REFLEXES_TAB_FRAME, OVERVIEW, 50, 0, 62, TAB_HEIGHT, FlowWidgets.EDGE));
        add(specs, children, button(REFLEXES_TAB, OVERVIEW, 53, 2, 56, "Reflexes", "Show the reflexes"));
        add(specs, children, attachAllReflexes(specs));
        add(specs, children, slotArea(specs, slots));
        add(specs, children, reflexArea(specs, reflexSlots));
        add(specs, children, WidgetSpec.text(STATUS, OVERVIEW, 4, 242, 492, LINE, "", FlowWidgets.YELLOW, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(LEVELS, OVERVIEW, 4, 284, 44, LINE, "Levels:", FlowWidgets.ORANGE, FONT_PLAIN));
        add(specs, children, WidgetSpec.frame(BASE_LEVELS_FRAME, OVERVIEW, 48, 282, 34, 18, FlowWidgets.EDGE));
        add(specs, children, button(BASE_LEVELS, OVERVIEW, 51, 284, 28, "Base", "Grey options on base levels"));
        add(specs, children, WidgetSpec.frame(BOOSTED_LEVELS_FRAME, OVERVIEW, 86, 282, 54, 18, FlowWidgets.EDGE));
        add(specs, children, button(BOOSTED_LEVELS, OVERVIEW, 89, 284, 48, "Boosted", "Grey options on boosted levels"));
        add(specs, children, button(SAVE_FLOW, OVERVIEW, 336, 284, 34, "Save", "Save the flow"));
        add(specs, children, button(RUN, OVERVIEW, 378, 284, 30, "Run", "Run the flow"));
        add(specs, children, button(STOP, OVERVIEW, 416, 284, 34, "Stop", "Stop the flow"));
        add(specs, children, button(CLEAR, OVERVIEW, 458, 284, 38, "Clear", "Clear the flow"));
        return WidgetSpec.layer(OVERVIEW, ROOT, LAYER_X, LAYER_Y, LAYER_WIDTH, LAYER_HEIGHT, children);
    }

    /** The Steps tab's "Attach all reflexes", at the right of the tabs row (Maxime, 2026-10-10). */
    private static WidgetSpec attachAllReflexes(Map<Integer, WidgetSpec> specs) {
        int x = LAYER_WIDTH - 4 - ATTACH_ALL_REFLEXES_WIDTH;
        List<Integer> children = new ArrayList<>();
        add(specs, children, button(ATTACH_ALL_REFLEXES, ATTACH_ALL_REFLEXES_LAYER, 0, 0, ATTACH_ALL_REFLEXES_WIDTH, "",
                "Attach all reflexes to every step"));
        return WidgetSpec.layer(ATTACH_ALL_REFLEXES_LAYER, OVERVIEW, x, 2, ATTACH_ALL_REFLEXES_WIDTH, LINE, children);
    }

    /** The slots, then the padlock, four to a row; the client's scrollbar sits right of them. */
    private static WidgetSpec slotArea(Map<Integer, WidgetSpec> specs, int slots) {
        List<Integer> children = new ArrayList<>();
        for (int slot = 0; slot < slots; slot++) {
            add(specs, children, slot(specs, slot));
        }
        add(specs, children, locked(specs, slots));
        int content = slotRows(slots) * (SLOT_HEIGHT + SLOT_GAP) - SLOT_GAP;
        return WidgetSpec.scrollLayer(SLOTS, OVERVIEW, SLOTS_X, SLOTS_Y, SLOTS_WIDTH, SLOTS_HEIGHT, Math.max(content, SLOTS_HEIGHT), children);
    }

    /** The Reflexes tab's area, where the slots are: a row per reflex slot, then the padlock, scrolling past its height. */
    private static WidgetSpec reflexArea(Map<Integer, WidgetSpec> specs, int reflexSlots) {
        List<Integer> children = new ArrayList<>();
        for (int row = 0; row < reflexSlots; row++) {
            add(specs, children, reflexRow(specs, row));
        }
        add(specs, children, reflexLocked(specs, reflexSlots));
        int content = (reflexSlots + 1) * (REFLEX_ROW_HEIGHT + REFLEX_ROW_GAP) - REFLEX_ROW_GAP;
        return WidgetSpec.scrollLayer(REFLEX_ROWS, OVERVIEW, SLOTS_X, SLOTS_Y, SLOTS_WIDTH, SLOTS_HEIGHT, Math.max(content, SLOTS_HEIGHT), children);
    }

    private static int reflexRowY(int row) {
        return row * (REFLEX_ROW_HEIGHT + REFLEX_ROW_GAP);
    }

    private static WidgetSpec reflexRow(Map<Integer, WidgetSpec> specs, int row) {
        int id = reflexRow(row);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(reflexRowFace(row), id, 0, 0, SLOTS_WIDTH, REFLEX_ROW_HEIGHT, TILE, TILE_HOVER, "Configure", true));
        add(specs, children, WidgetSpec.frame(reflexRowFrame(row), id, 0, 0, SLOTS_WIDTH, REFLEX_ROW_HEIGHT, TILE_EDGE));
        add(specs, children, WidgetSpec.centredText(reflexRowNumber(row), id, 10, 5, LINE, "", NUMBER, FONT_SMALL));
        add(specs, children, picture(reflexRowPicture(row), id, 20, 2, REFLEX_PICTURE));
        add(specs, children, WidgetSpec.text(reflexRowText(row), id, SENTENCE_X, 5, SLOTS_WIDTH - SENTENCE_X - 4, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        return WidgetSpec.layer(id, REFLEX_ROWS, 0, reflexRowY(row), SLOTS_WIDTH, REFLEX_ROW_HEIGHT, children);
    }

    /** The padlock after the reflex rows, as after the step slots. */
    private static WidgetSpec reflexLocked(Map<Integer, WidgetSpec> specs, int reflexSlots) {
        int id = reflexRow(reflexSlots);
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(id + 1, id, 0, 0, SLOTS_WIDTH, REFLEX_ROW_HEIGHT, LOCKED));
        add(specs, children, WidgetSpec.frame(id + 2, id, 0, 0, SLOTS_WIDTH, REFLEX_ROW_HEIGHT, CORNER));
        add(specs, children, new WidgetSpec.Sprite(reflexLockedSprite(reflexSlots), id, 20, 2, REFLEX_PICTURE, REFLEX_PICTURE, "keys", 0));
        return WidgetSpec.layer(id, REFLEX_ROWS, 0, reflexRowY(reflexSlots), SLOTS_WIDTH, REFLEX_ROW_HEIGHT, children);
    }

    private static int slotX(int slot) {
        return slot % COLUMNS * (SLOT_WIDTH + SLOT_GAP);
    }

    private static int slotY(int slot) {
        return slot / COLUMNS * (SLOT_HEIGHT + SLOT_GAP);
    }

    private static WidgetSpec slot(Map<Integer, WidgetSpec> specs, int slot) {
        int id = slot(slot);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(slotFace(slot), id, 0, 0, SLOT_WIDTH, SLOT_HEIGHT, TILE, TILE_HOVER, "Configure", true));
        add(specs, children, WidgetSpec.frame(slotFrame(slot), id, 0, 0, SLOT_WIDTH, SLOT_HEIGHT, TILE_EDGE));
        add(specs, children, picture(slotPicture(slot), id, 6, 5, BIG_PICTURE));
        add(specs, children, corner(specs, slot));
        add(specs, children, WidgetSpec.centredText(slotNumber(slot), id, 108, 3, LINE, "", NUMBER, FONT_SMALL));
        add(specs, children, WidgetSpec.text(slotKind(slot), id, 47, 5, 60, LINE, "", FlowWidgets.ORANGE, FONT_BOLD));
        for (int line = 0; line < SLOT_LINES; line++) {
            add(specs, children, WidgetSpec.text(slotLine(slot, line), id, 5, 42 + 13 * line, 108, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        }
        add(specs, children, WidgetSpec.centredText(slotPlus(slot), id, SLOT_WIDTH / 2, 34, LINE, "", FlowWidgets.GREY, FONT_BOLD));
        add(specs, children, WidgetSpec.centredText(slotAdd(slot), id, SLOT_WIDTH / 2, 54, LINE, "", FlowWidgets.GREY, FONT_SMALL));
        return WidgetSpec.layer(id, SLOTS, slotX(slot), slotY(slot), SLOT_WIDTH, SLOT_HEIGHT, children);
    }

    /** The mockup's corner: the kind's icon on a dark box over the target's bottom right. */
    private static WidgetSpec corner(Map<Integer, WidgetSpec> specs, int slot) {
        int id = slotCornerLayer(slot);
        int size = CORNER_PICTURE + 2;
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(slotCornerBox(slot), id, 0, 0, size, size, CORNER));
        add(specs, children, picture(slotCorner(slot), id, 1, 1, CORNER_PICTURE));
        return WidgetSpec.layer(id, slot(slot), 28, 26, size, size, children);
    }

    /** The padlock after the slots (S01: only the next locked slot shows), the cache's ring of keys (S04). */
    private static WidgetSpec locked(Map<Integer, WidgetSpec> specs, int slots) {
        int id = slot(slots);
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(id + 1, id, 0, 0, SLOT_WIDTH, SLOT_HEIGHT, LOCKED));
        add(specs, children, WidgetSpec.frame(id + 2, id, 0, 0, SLOT_WIDTH, SLOT_HEIGHT, CORNER));
        add(specs, children, new WidgetSpec.Sprite(lockedSprite(slots), id, (SLOT_WIDTH - 32) / 2, (SLOT_HEIGHT - 32) / 2, 32, 32, "keys", 0));
        return WidgetSpec.layer(id, SLOTS, slotX(slots), slotY(slots), SLOT_WIDTH, SLOT_HEIGHT, children);
    }

    private static WidgetSpec kinds(Map<Integer, WidgetSpec> specs) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.centredText(KINDS_TITLE, KINDS, LAYER_WIDTH / 2, 8, LINE, "", FlowWidgets.GREY, FONT_PLAIN));
        int width = KIND_COLUMNS * KIND_WIDTH + (KIND_COLUMNS - 1) * KIND_GAP;
        int left = (LAYER_WIDTH - width) / 2;
        for (int kind = 0; kind < KIND_BUTTONS; kind++) {
            int x = left + kind % KIND_COLUMNS * (KIND_WIDTH + KIND_GAP);
            int y = 30 + kind / KIND_COLUMNS * (KIND_HEIGHT + KIND_GAP);
            add(specs, children, kindButton(specs, kind, x, y));
        }
        add(specs, children, button(KINDS_BACK, KINDS, (LAYER_WIDTH - 34) / 2, 284, 34, "Back", "Back"));
        return WidgetSpec.layer(KINDS, ROOT, LAYER_X, LAYER_Y, LAYER_WIDTH, LAYER_HEIGHT, children);
    }

    /**
     * The mockup's configure screen: header and a step's tabs, two columns of rows, a step's Reflexes page over them,
     * warnings, then Delete, Back and Save.
     */
    private static WidgetSpec configure(Map<Integer, WidgetSpec> specs, int reflexSlots, List<ListPlacement> lists) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, picture(HEADER_PICTURE, CONFIGURE, 4, 2, HEADER_PICTURE_SIZE));
        add(specs, children, headerCorner(specs));
        add(specs, children, WidgetSpec.text(HEADER_NAME, CONFIGURE, 46, 4, LAYER_WIDTH - STEP_TABS_WIDTH - 52, LINE, "", FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, stepTabs(specs));
        add(specs, children, WidgetSpec.text(HEADER_DESCRIPTION, CONFIGURE, 46, 22, 450, LINE, "", MUTED, FONT_SMALL));
        add(specs, children, WidgetSpec.box(TOP_DIVIDER, CONFIGURE, 0, 42, LAYER_WIDTH, 1, FlowWidgets.EDGE));
        for (int row = 0; row < ROWS; row++) {
            add(specs, children, row(specs, row));
        }
        for (int list = 0; list < LISTS; list++) {
            add(specs, children, list(specs, list, lists.get(list)));
        }
        add(specs, children, attached(specs, reflexSlots, lists.get(ATTACHED_LIST)));
        add(specs, children, WidgetSpec.box(BOTTOM_DIVIDER, CONFIGURE, 0, 236, LAYER_WIDTH, 1, FlowWidgets.EDGE));
        for (int line = 0; line < WARNING_LINES; line++) {
            add(specs, children, WidgetSpec.text(warning(line), CONFIGURE, 4, 241 + 13 * line, 492, LINE, "", FlowWidgets.YELLOW, FONT_SMALL));
        }
        add(specs, children, button(DELETE, CONFIGURE, 366, 284, 40, "Delete", "Delete"));
        add(specs, children, button(BACK, CONFIGURE, 418, 284, 30, "Back", "Back without saving"));
        add(specs, children, button(SAVE, CONFIGURE, 460, 284, 34, "Save", "Save"));
        return WidgetSpec.layer(CONFIGURE, ROOT, LAYER_X, LAYER_Y, LAYER_WIDTH, LAYER_HEIGHT, children);
    }

    /** A step's Settings and Reflexes tabs, drawn as the overview's, at the right of the name line. */
    private static WidgetSpec stepTabs(Map<Integer, WidgetSpec> specs) {
        int reflexesX = SETTINGS_TAB_WIDTH + 6;
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.frame(SETTINGS_TAB_FRAME, STEP_TABS, 0, 0, SETTINGS_TAB_WIDTH, TAB_HEIGHT, FlowWidgets.EDGE));
        add(specs, children, button(SETTINGS_TAB, STEP_TABS, 3, 2, SETTINGS_TAB_WIDTH - 6, "Settings", "Show the step's settings"));
        add(specs, children, WidgetSpec.frame(STEP_REFLEXES_TAB_FRAME, STEP_TABS, reflexesX, 0, REFLEXES_TAB_WIDTH, TAB_HEIGHT, FlowWidgets.EDGE));
        add(specs, children, button(STEP_REFLEXES_TAB, STEP_TABS, reflexesX + 3, 2, REFLEXES_TAB_WIDTH - 6, "Reflexes", "Show the step's reflexes"));
        return WidgetSpec.layer(STEP_TABS, CONFIGURE, LAYER_WIDTH - 2 - STEP_TABS_WIDTH, 0, STEP_TABS_WIDTH, TAB_HEIGHT, children);
    }

    /**
     * The Reflexes page where the server placed it: the line that opens the attach search, Attach all beside it, then a
     * line per reflex slot, each dragging onto another.
     */
    private static WidgetSpec attached(Map<Integer, WidgetSpec> specs, int reflexSlots, ListPlacement placement) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, attachLine(specs));
        for (int line = 0; line < reflexSlots; line++) {
            add(specs, children, attachedLine(specs, line));
        }
        return WidgetSpec.scrollLayer(ATTACHED, CONFIGURE, placement.x(), placement.y(), ATTACHED_WIDTH, placement.height(), placement.scrollHeight(),
                children);
    }

    private static WidgetSpec attachLine(Map<Integer, WidgetSpec> specs) {
        int width = ATTACHED_WIDTH - ATTACH_ALL_WIDTH - TOGGLE_GAP;
        int height = LINE_HEIGHT - 2;
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(ATTACH, ATTACH_LINE, 0, 0, width, LINE_HEIGHT, FIELD, FIELD_HOVER, "Search", false));
        add(specs, children, WidgetSpec.frame(ATTACH_FRAME, ATTACH_LINE, 0, 0, width, LINE_HEIGHT, TILE_EDGE));
        add(specs, children, WidgetSpec.text(ATTACH_TEXT, ATTACH_LINE, 4, 3, width - 8, LINE, "", FlowWidgets.GREY, FONT_SMALL));
        int all = width + TOGGLE_GAP;
        add(specs, children, new WidgetSpec.Tile(ATTACH_ALL, ATTACH_LINE, all, 1, ATTACH_ALL_WIDTH, height, BUTTON, BUTTON_HOVER, "Attach all", false));
        add(specs, children, WidgetSpec.frame(ATTACH_ALL_FRAME, ATTACH_LINE, all, 1, ATTACH_ALL_WIDTH, height, FlowWidgets.EDGE));
        add(specs, children, WidgetSpec.centredText(ATTACH_ALL_TEXT, ATTACH_LINE, all + ATTACH_ALL_WIDTH / 2, 3, LINE, "", FlowWidgets.ORANGE, FONT_SMALL));
        return WidgetSpec.layer(ATTACH_LINE, ATTACHED, 0, 0, ATTACHED_WIDTH, LINE_HEIGHT, children);
    }

    /** A reflex's line: its number in the flow, picture and sentence, and the x that detaches it (Maxime: "Move" drags it). */
    private static WidgetSpec attachedLine(Map<Integer, WidgetSpec> specs, int line) {
        int id = attachedLine(line);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(attachedFace(line), id, 0, 0, ATTACHED_WIDTH, LINE_HEIGHT, FIELD, FIELD_HOVER, "Move", true));
        add(specs, children, WidgetSpec.centredText(attachedNumber(line), id, 9, 3, LINE, "", NUMBER, FONT_SMALL));
        add(specs, children, picture(attachedPicture(line), id, ATTACHED_PICTURE_X, 1, FIELD_PICTURE));
        add(specs, children, WidgetSpec.text(attachedSentence(line), id, SENTENCE_LINE_X, 3, DETACH_X - SENTENCE_LINE_X - 4, LINE, "",
                FlowWidgets.WHITE, FONT_SMALL));
        add(specs, children, new WidgetSpec.Tile(detachFace(line), id, DETACH_X, 3, REMOVE_WIDTH, LINE_HEIGHT - 6, BUTTON, BUTTON_HOVER, "Detach", false));
        add(specs, children, WidgetSpec.centredText(detachText(line), id, DETACH_X + REMOVE_WIDTH / 2, 3, LINE, "x", FlowWidgets.ORANGE, FONT_SMALL));
        return WidgetSpec.layer(id, ATTACHED, 0, (line + 1) * (LINE_HEIGHT + LINE_GAP), ATTACHED_WIDTH, LINE_HEIGHT, children);
    }

    /** The header's corner, as a slot's: the kind's icon on a dark box over the picture's bottom right. */
    private static WidgetSpec headerCorner(Map<Integer, WidgetSpec> specs) {
        int size = CORNER_PICTURE + 2;
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(HEADER_CORNER_BOX, HEADER_CORNER_LAYER, 0, 0, size, size, CORNER));
        add(specs, children, picture(HEADER_CORNER, HEADER_CORNER_LAYER, 1, 1, CORNER_PICTURE));
        return WidgetSpec.layer(HEADER_CORNER_LAYER, CONFIGURE, 24, 22, size, size, children);
    }

    /** Configure row {@code row}: its label, then a field box or a note in its place, and the amount's button. */
    private static WidgetSpec row(Map<Integer, WidgetSpec> specs, int row) {
        int id = row(row);
        int x = row < ROWS_PER_COLUMN ? 0 : RIGHT_COLUMN;
        int y = ROW_TOP + row % ROWS_PER_COLUMN * ROW_HEIGHT;
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.text(rowLabel(row), id, 0, 4, LABEL_WIDTH, LINE, "", FlowWidgets.ORANGE, FONT_SMALL));
        add(specs, children, field(specs, row));
        add(specs, children, rowButton(specs, row));
        add(specs, children, WidgetSpec.text(rowNote(row), id, FIELD_X, 4, RIGHT_COLUMN - FIELD_X - 4, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        add(specs, children, toggles(specs, row, 2));
        add(specs, children, toggles(specs, row, 3));
        return WidgetSpec.layer(id, CONFIGURE, x, y, RIGHT_COLUMN - 4, FIELD_HEIGHT + 1, children);
    }

    /** A toggle's {@code count} buttons side by side in the field's place, each a face, a frame the server lights and a word. */
    private static WidgetSpec toggles(Map<Integer, WidgetSpec> specs, int row, int count) {
        int id = toggles(row, count);
        int width = toggleWidth(count);
        int height = FIELD_HEIGHT - 2;
        List<Integer> children = new ArrayList<>();
        for (int button = 0; button < count; button++) {
            int x = button * (width + TOGGLE_GAP);
            add(specs, children, new WidgetSpec.Tile(toggleFace(row, count, button), id, x, 0, width, height, BUTTON, BUTTON_HOVER, "Select", false));
            add(specs, children, WidgetSpec.frame(toggleFrame(row, count, button), id, x, 0, width, height, TILE_EDGE));
            add(specs, children, WidgetSpec.centredText(toggleText(row, count, button), id, x + width / 2, 3, LINE, "", FlowWidgets.ORANGE, FONT_SMALL));
        }
        return WidgetSpec.layer(id, row(row), FIELD_X, 1, TOGGLE_ROOM, height, children);
    }

    public static int toggleWidth(int count) {
        return (TOGGLE_ROOM - (count - 1) * TOGGLE_GAP) / count;
    }

    private static WidgetSpec list(Map<Integer, WidgetSpec> specs, int list, ListPlacement placement) {
        int id = list(list);
        List<Integer> children = new ArrayList<>();
        add(specs, children, addLine(specs, list));
        for (int line = 0; line < LIST_LINES; line++) {
            add(specs, children, line(specs, list, line));
        }
        return WidgetSpec.scrollLayer(id, CONFIGURE, placement.x(), placement.y(), LIST_WIDTH, placement.height(), placement.scrollHeight(), children);
    }

    private static WidgetSpec addLine(Map<Integer, WidgetSpec> specs, int list) {
        int id = listAddLine(list);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(listAdd(list), id, 0, 0, LIST_WIDTH, LINE_HEIGHT, FIELD, FIELD_HOVER, "Search", false));
        add(specs, children, WidgetSpec.frame(listAddFrame(list), id, 0, 0, LIST_WIDTH, LINE_HEIGHT, TILE_EDGE));
        add(specs, children, WidgetSpec.text(listAddText(list), id, 4, 3, LIST_WIDTH - 8, LINE, "", FlowWidgets.GREY, FONT_SMALL));
        return WidgetSpec.layer(id, list(list), 0, 0, LIST_WIDTH, LINE_HEIGHT, children);
    }

    /** An item's line: its picture and name, a withdrawal's amount box and All button, and the x that takes it out. */
    private static WidgetSpec line(Map<Integer, WidgetSpec> specs, int list, int line) {
        int id = line(list, line);
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(lineBox(list, line), id, 0, 0, LIST_WIDTH, LINE_HEIGHT, FIELD));
        add(specs, children, picture(linePicture(list, line), id, 1, 1, FIELD_PICTURE));
        add(specs, children, WidgetSpec.text(lineName(list, line), id, NAME_X, 3, REMOVE_X - NAME_X - 2, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        add(specs, children, amount(specs, list, line));
        add(specs, children, new WidgetSpec.Tile(lineRemoveFace(list, line), id, REMOVE_X, 3, REMOVE_WIDTH, LINE_HEIGHT - 6, BUTTON, BUTTON_HOVER, "Remove", false));
        add(specs, children, WidgetSpec.centredText(lineRemoveText(list, line), id, REMOVE_X + REMOVE_WIDTH / 2, 3, LINE, "x", FlowWidgets.ORANGE, FONT_SMALL));
        return WidgetSpec.layer(id, list(list), 0, (line + 1) * (LINE_HEIGHT + LINE_GAP), LIST_WIDTH, LINE_HEIGHT, children);
    }

    private static WidgetSpec amount(Map<Integer, WidgetSpec> specs, int list, int line) {
        int id = lineAmount(list, line);
        int height = LINE_HEIGHT - 4;
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(lineAmountFace(list, line), id, 0, 0, AMOUNT_WIDTH, height, FIELD_HOVER, TILE_HOVER, "Change", false));
        add(specs, children, WidgetSpec.frame(lineAmountFrame(list, line), id, 0, 0, AMOUNT_WIDTH, height, TILE_EDGE));
        add(specs, children, WidgetSpec.centredText(lineAmountText(list, line), id, AMOUNT_WIDTH / 2, 2, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        int all = ALL_X - AMOUNT_X;
        add(specs, children, new WidgetSpec.Tile(lineAllFace(list, line), id, all, 0, ALL_WIDTH, height, BUTTON, BUTTON_HOVER, "Select", false));
        add(specs, children, WidgetSpec.centredText(lineAllText(list, line), id, all + ALL_WIDTH / 2, 2, LINE, "All", FlowWidgets.ORANGE, FONT_SMALL));
        return WidgetSpec.layer(id, line(list, line), AMOUNT_X, 2, ALL_X + ALL_WIDTH - AMOUNT_X, height, children);
    }

    private static WidgetSpec field(Map<Integer, WidgetSpec> specs, int row) {
        int id = rowField(row);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(rowFace(row), id, 0, 0, FIELD_WIDTH, FIELD_HEIGHT, FIELD, FIELD_HOVER, "Change", false));
        add(specs, children, WidgetSpec.frame(rowFrame(row), id, 0, 0, FIELD_WIDTH, FIELD_HEIGHT, TILE_EDGE));
        add(specs, children, picture(rowPicture(row), id, 2, 1, FIELD_PICTURE));
        add(specs, children, WidgetSpec.text(rowText(row), id, 22, 4, FIELD_WIDTH - 24, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        add(specs, children, WidgetSpec.text(rowPlainText(row), id, 4, 4, FIELD_WIDTH - 8, LINE, "", FlowWidgets.WHITE, FONT_SMALL));
        return WidgetSpec.layer(id, row(row), FIELD_X, 0, FIELD_WIDTH, FIELD_HEIGHT, children);
    }

    private static WidgetSpec rowButton(Map<Integer, WidgetSpec> specs, int row) {
        int id = rowButton(row);
        int height = FIELD_HEIGHT - 2;
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(rowButtonFace(row), id, 0, 0, ROW_BUTTON_WIDTH, height, BUTTON, BUTTON_HOVER, "Select", false));
        add(specs, children, WidgetSpec.frame(rowButtonFrame(row), id, 0, 0, ROW_BUTTON_WIDTH, height, FlowWidgets.EDGE));
        add(specs, children, WidgetSpec.centredText(rowButtonText(row), id, ROW_BUTTON_WIDTH / 2, 3, LINE, "", FlowWidgets.ORANGE, FONT_SMALL));
        return WidgetSpec.layer(id, row(row), ROW_BUTTON_X, 1, ROW_BUTTON_WIDTH, height, children);
    }

    private static WidgetSpec kindButton(Map<Integer, WidgetSpec> specs, int kind, int x, int y) {
        int id = kindButton(kind);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(kindFace(kind), id, 0, 0, KIND_WIDTH, KIND_HEIGHT, TILE, TILE_HOVER, "Select", false));
        add(specs, children, picture(kindPicture(kind), id, (KIND_WIDTH - KIND_PICTURE) / 2, 5, KIND_PICTURE));
        add(specs, children, WidgetSpec.centredText(kindLabel(kind), id, KIND_WIDTH / 2, 34, LINE, "", FlowWidgets.ORANGE, FONT_SMALL));
        return WidgetSpec.layer(id, KINDS, x, y, KIND_WIDTH, KIND_HEIGHT, children);
    }

    private static WidgetSpec picture(int id, int parent, int x, int y, int size) {
        return new WidgetSpec.Picture(id, parent, x, y, size, size, WidgetGallery.NPC_PITCH, WidgetGallery.NPC_YAW);
    }

    private static WidgetSpec button(int id, int parent, int x, int y, int width, String text, String option) {
        return WidgetSpec.button(id, parent, x, y, width, LINE, text, FlowWidgets.ORANGE, FlowWidgets.YELLOW, FONT_PLAIN, option);
    }
}
