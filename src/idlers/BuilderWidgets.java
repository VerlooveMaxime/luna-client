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
 * player has, as many as the server says (packet 108; Maxime, 2026-10-09: no fixed most), then the padlock; the kind
 * picker a button per kind of step. The server mirrors these ids in {@code game.idle.ui.BuilderWidgets}; text and
 * pictures it changes start empty here.
 */
public final class BuilderWidgets {

    public static final int FIRST_ID = 30700;
    /** The fixed ids end here; the slots follow from {@link #SLOT_BASE}. */
    public static final int ID_LIMIT = 30800;

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

    public static final int KINDS = 30730;
    public static final int KINDS_TITLE = 30731;
    public static final int KINDS_BACK = 30732;
    public static final int KIND_BUTTONS = 16;
    private static final int KIND_BASE = 30736;
    private static final int KIND_STRIDE = 4;

    public static final int SLOT_BASE = 31000;
    public static final int SLOT_STRIDE = 16;
    public static final int SLOT_LINES = 4;

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
    public static final int SLOTS_Y = 2;
    public static final int SLOTS_HEIGHT = 216;
    public static final int BIG_PICTURE = 34;
    public static final int CORNER_PICTURE = 16;
    public static final int KIND_PICTURE = 25;

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

    /** The widgets for a player with {@code slots} step slots. */
    public static Map<Integer, WidgetSpec> specs(int slots) {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        // The panel comes first so the rest draws over it.
        add(specs, children, WidgetSpec.box(PANEL, ROOT, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, FlowWidgets.PANEL));
        add(specs, children, WidgetSpec.frame(FRAME, ROOT, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, FlowWidgets.EDGE));
        add(specs, children, WidgetSpec.centredText(TITLE, ROOT, SCREEN_WIDTH / 2, 6, LINE, "Flow builder", FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, button(CLOSE, ROOT, 460, 6, 42, "Close", "Close"));
        add(specs, children, overview(specs, slots));
        add(specs, children, kinds(specs));
        specs.put(ROOT, WidgetSpec.layer(ROOT, -1, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, children));
        return specs;
    }

    /** The rows of slots {@code slots} step slots and the padlock take. */
    public static int slotRows(int slots) {
        return slots / COLUMNS + 1;
    }

    private static WidgetSpec overview(Map<Integer, WidgetSpec> specs, int slots) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, slotArea(specs, slots));
        add(specs, children, WidgetSpec.text(STATUS, OVERVIEW, 4, 224, 492, LINE, "", FlowWidgets.YELLOW, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(LEVELS, OVERVIEW, 4, 284, 44, LINE, "Levels:", FlowWidgets.ORANGE, FONT_PLAIN));
        add(specs, children, WidgetSpec.frame(BASE_LEVELS_FRAME, OVERVIEW, 48, 282, 34, 18, FlowWidgets.EDGE));
        add(specs, children, button(BASE_LEVELS, OVERVIEW, 51, 284, 28, "Base", "Grey options on base levels"));
        add(specs, children, WidgetSpec.frame(BOOSTED_LEVELS_FRAME, OVERVIEW, 86, 282, 54, 18, FlowWidgets.EDGE));
        add(specs, children, button(BOOSTED_LEVELS, OVERVIEW, 89, 284, 48, "Boosted", "Grey options on boosted levels"));
        add(specs, children, button(RUN, OVERVIEW, 378, 284, 30, "Run", "Run the flow"));
        add(specs, children, button(STOP, OVERVIEW, 416, 284, 34, "Stop", "Stop the flow"));
        add(specs, children, button(CLEAR, OVERVIEW, 458, 284, 38, "Clear", "Clear the flow"));
        return WidgetSpec.layer(OVERVIEW, ROOT, LAYER_X, LAYER_Y, LAYER_WIDTH, LAYER_HEIGHT, children);
    }

    /** The slots, then the padlock, four to a row; the client's scrollbar sits right of them. */
    private static WidgetSpec slotArea(Map<Integer, WidgetSpec> specs, int slots) {
        List<Integer> children = new ArrayList<>();
        for (int slot = 0; slot < slots; slot++) {
            add(specs, children, slot(specs, slot));
        }
        add(specs, children, locked(specs, slots));
        int width = COLUMNS * SLOT_WIDTH + (COLUMNS - 1) * SLOT_GAP;
        int content = slotRows(slots) * (SLOT_HEIGHT + SLOT_GAP) - SLOT_GAP;
        return WidgetSpec.scrollLayer(SLOTS, OVERVIEW, SLOTS_X, SLOTS_Y, width, SLOTS_HEIGHT, Math.max(content, SLOTS_HEIGHT), children);
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
