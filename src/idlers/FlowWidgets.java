package idlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static idlers.WidgetSpec.FONT_BOLD;
import static idlers.WidgetSpec.FONT_PLAIN;
import static idlers.WidgetSpec.FONT_SMALL;
import static idlers.WidgetSpecs.add;

/**
 * The Idle tab (tab slot 7, unused by the 377 client): status lines, Run and Stop, then the saved flows, the way into
 * the flow builder, a row per saved-flow slot the player has (packet 108) and the padlock (flow builder v2, S06c); and
 * the colours the builder's screens share. Ids start at {@link #FIRST_ID}, above every cache widget. The server mirrors
 * these ids in {@code game.idle.ui.FlowWidgets}; text the server changes at runtime starts empty here.
 */
public final class FlowWidgets {

    public static final int FIRST_ID = 30000;
    public static final int ID_LIMIT = 30300;

    public static final int TAB = 30000;
    public static final int TAB_TITLE = 30001;
    public static final int TAB_STATUS_1 = 30002;
    public static final int TAB_STATUS_2 = 30003;
    public static final int TAB_STATUS_3 = 30004;
    public static final int TAB_RUN = 30011;
    public static final int TAB_STOP = 30012;
    public static final int TAB_DIVIDER = 30013;

    public static final int TAB_SAVED = 30020;
    public static final int SAVED_TITLE = 30021;
    public static final int SAVED_ROWS = 30022;
    public static final int SAVED_LOCKED = 30023;
    public static final int SAVED_LOCKED_SPRITE = 30026;

    private static final int ROW_BASE = 30100;
    private static final int ROW_STRIDE = 12;
    /** The most saved-flow slots the tab's ids have room for. */
    public static final int MOST_SAVED_SLOTS = (ID_LIMIT - ROW_BASE) / ROW_STRIDE;

    static final int ORANGE = 0xff981f;
    static final int WHITE = 0xffffff;
    static final int YELLOW = 0xffff00;
    static final int GREY = 0x9f9f9f;
    static final int GREEN = 0x00ff00;
    /** The client's own interface background colour and a lighter edge for frames and dividers. */
    static final int PANEL = 0x332d25;
    static final int EDGE = 0x8d7f63;

    private static final int TAB_WIDTH = 190;
    private static final int TAB_HEIGHT = 261;
    private static final int LINE = 14;

    /** The saved flows' list: rows of two lines, 3 shown before it scrolls, the padlock under them. */
    public static final int SAVED_Y = 98;
    public static final int ROWS_X = 8;
    public static final int ROWS_Y = 20;
    public static final int ROWS_WIDTH = 174;
    public static final int ROWS_HEIGHT = 140;
    public static final int ROW_HEIGHT = 30;
    public static final int ROW_GAP = 3;
    public static final int LOCKED_HEIGHT = 22;
    public static final int LOCKED_SPRITE = 18;
    /** The client draws a scrolling layer's scrollbar over its right edge. */
    public static final int SCROLLBAR = 16;

    private FlowWidgets() {
    }

    /** The widgets of the tab for a player with {@code savedSlots} saved-flow slots. */
    public static Map<Integer, WidgetSpec> specs(int savedSlots) {
        if (savedSlots > MOST_SAVED_SLOTS) {
            throw new IllegalArgumentException("The Idle tab has ids for " + MOST_SAVED_SLOTS + " saved flows, not " + savedSlots);
        }
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.text(TAB_TITLE, TAB, 10, 6, 170, LINE, "IdleRS", ORANGE, FONT_BOLD));
        add(specs, children, WidgetSpec.text(TAB_STATUS_1, TAB, 10, 26, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(TAB_STATUS_2, TAB, 10, 40, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(TAB_STATUS_3, TAB, 10, 54, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, button(TAB_RUN, TAB, 10, 74, 28, "Run", "Run the flow", FONT_PLAIN));
        add(specs, children, button(TAB_STOP, TAB, 46, 74, 36, "Stop", "Stop the flow", FONT_PLAIN));
        add(specs, children, WidgetSpec.box(TAB_DIVIDER, TAB, 8, 94, ROWS_WIDTH, 1, EDGE));
        add(specs, children, saved(specs, savedSlots));
        specs.put(TAB, WidgetSpec.layer(TAB, -1, 0, 0, TAB_WIDTH, TAB_HEIGHT, children));
        return Collections.unmodifiableMap(specs);
    }

    /** The height the rows and the padlock of {@code savedSlots} saved flows take. */
    public static int rowsContent(int savedSlots) {
        return savedSlots * (ROW_HEIGHT + ROW_GAP) + LOCKED_HEIGHT;
    }

    /** A row's width: the list's, less the scrollbar when it scrolls. */
    public static int rowWidth(int savedSlots) {
        return rowsContent(savedSlots) > ROWS_HEIGHT ? ROWS_WIDTH - SCROLLBAR : ROWS_WIDTH;
    }

    public static int row(int slot) {
        return ROW_BASE + slot * ROW_STRIDE;
    }

    /** The layer of the row's frame, which the server shows on the current flow's row: only layers hide. */
    public static int rowFrameLayer(int slot) {
        return row(slot) + 1;
    }

    public static int rowFrame(int slot) {
        return row(slot) + 2;
    }

    public static int rowName(int slot) {
        return row(slot) + 3;
    }

    public static int rowCount(int slot) {
        return row(slot) + 4;
    }

    /** The layer of the row's Load, which the server hides on an empty slot. */
    public static int rowLoadLayer(int slot) {
        return row(slot) + 5;
    }

    public static int rowLoad(int slot) {
        return row(slot) + 6;
    }

    /** The layer of the row's New, which the server shows on an empty slot only. */
    public static int rowNewLayer(int slot) {
        return row(slot) + 7;
    }

    public static int rowNew(int slot) {
        return row(slot) + 8;
    }

    /** The layer of the row's x, which the server hides on an empty slot. */
    public static int rowDeleteLayer(int slot) {
        return row(slot) + 9;
    }

    public static int rowDelete(int slot) {
        return row(slot) + 10;
    }

    private static WidgetSpec saved(Map<Integer, WidgetSpec> specs, int savedSlots) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.text(SAVED_TITLE, TAB_SAVED, 10, 2, 170, LINE, "Saved flows", ORANGE, FONT_BOLD));
        add(specs, children, rows(specs, savedSlots));
        return WidgetSpec.layer(TAB_SAVED, TAB, 0, SAVED_Y, TAB_WIDTH, TAB_HEIGHT - SAVED_Y, children);
    }

    private static WidgetSpec rows(Map<Integer, WidgetSpec> specs, int savedSlots) {
        List<Integer> children = new ArrayList<>();
        int width = rowWidth(savedSlots);
        for (int slot = 0; slot < savedSlots; slot++) {
            add(specs, children, row(specs, slot, width));
        }
        add(specs, children, locked(specs, savedSlots, width));
        return WidgetSpec.scrollLayer(SAVED_ROWS, TAB_SAVED, ROWS_X, ROWS_Y, ROWS_WIDTH, ROWS_HEIGHT,
                Math.max(rowsContent(savedSlots), ROWS_HEIGHT), children);
    }

    /** Two lines: the name, then the step count with Load and x, or New on an empty slot, at the right. */
    private static WidgetSpec row(Map<Integer, WidgetSpec> specs, int slot, int width) {
        int id = row(slot);
        List<Integer> children = new ArrayList<>();
        add(specs, children, frame(specs, slot, width));
        add(specs, children, WidgetSpec.text(rowName(slot), id, 4, 2, width - 8, LINE, "", WHITE, FONT_SMALL));
        add(specs, children, WidgetSpec.text(rowCount(slot), id, 4, 16, 60, LINE, "", GREY, FONT_SMALL));
        add(specs, children, hideable(specs, rowLoadLayer(slot), id, button(rowLoad(slot), rowLoadLayer(slot), 0, 0, 26, "Load", "Load", FONT_SMALL), width - 50));
        add(specs, children, hideable(specs, rowNewLayer(slot), id, button(rowNew(slot), rowNewLayer(slot), 0, 0, 26, "New", "New flow", FONT_SMALL), width - 50));
        add(specs, children, hideable(specs, rowDeleteLayer(slot), id, button(rowDelete(slot), rowDeleteLayer(slot), 0, 0, 10, "x", "Delete", FONT_SMALL), width - 16));
        return WidgetSpec.layer(id, SAVED_ROWS, 0, slot * (ROW_HEIGHT + ROW_GAP), width, ROW_HEIGHT, children);
    }

    private static WidgetSpec frame(Map<Integer, WidgetSpec> specs, int slot, int width) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.frame(rowFrame(slot), rowFrameLayer(slot), 0, 0, width, ROW_HEIGHT, GREEN));
        return WidgetSpec.layer(rowFrameLayer(slot), row(slot), 0, 0, width, ROW_HEIGHT, children);
    }

    /** {@code button} alone in a layer of its own at ({@code x}, 16) of the row, so the server can hide it. */
    private static WidgetSpec hideable(Map<Integer, WidgetSpec> specs, int id, int row, WidgetSpec button, int x) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, button);
        return WidgetSpec.layer(id, row, x, 16, button.width(), LINE, children);
    }

    /** The padlock after the rows (S01: only the next locked slot shows), the cache's ring of keys as on the builder. */
    private static WidgetSpec locked(Map<Integer, WidgetSpec> specs, int savedSlots, int width) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(SAVED_LOCKED + 1, SAVED_LOCKED, 0, 0, width, LOCKED_HEIGHT, BuilderWidgets.LOCKED));
        add(specs, children, WidgetSpec.frame(SAVED_LOCKED + 2, SAVED_LOCKED, 0, 0, width, LOCKED_HEIGHT, BuilderWidgets.CORNER));
        add(specs, children, new WidgetSpec.Sprite(SAVED_LOCKED_SPRITE, SAVED_LOCKED, (width - LOCKED_SPRITE) / 2,
                (LOCKED_HEIGHT - LOCKED_SPRITE) / 2, LOCKED_SPRITE, LOCKED_SPRITE, "keys", 0));
        return WidgetSpec.layer(SAVED_LOCKED, SAVED_ROWS, 0, savedSlots * (ROW_HEIGHT + ROW_GAP), width, LOCKED_HEIGHT, children);
    }

    private static WidgetSpec button(int id, int parent, int x, int y, int width, String text, String option, int font) {
        return WidgetSpec.button(id, parent, x, y, width, LINE, text, ORANGE, YELLOW, font, option);
    }
}
