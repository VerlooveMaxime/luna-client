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
 * The IdleRS widgets: the sidebar tab (tab slot 7, unused by the 377 client) and the flow builder screen. Ids start
 * at {@link #FIRST_ID}, above every cache widget. The server mirrors these ids in {@code game.idle.ui.FlowWidgets};
 * text the server changes at runtime starts empty here.
 */
public final class FlowWidgets {

    public static final int FIRST_ID = 30000;
    public static final int ID_LIMIT = 30300;

    public static final int TAB = 30000;
    public static final int TAB_TITLE = 30001;
    public static final int TAB_STATUS_1 = 30002;
    public static final int TAB_STATUS_2 = 30003;
    public static final int TAB_STATUS_3 = 30004;
    public static final int TAB_OPEN_BUILDER = 30010;
    public static final int TAB_RUN = 30011;
    public static final int TAB_STOP = 30012;

    public static final int BUILDER = 30100;
    public static final int BUILDER_TITLE = 30101;
    public static final int BUILDER_CLOSE = 30102;

    public static final int ROWS = 8;
    public static final int ROW_BASE = 30110;
    public static final int ROW_STRIDE = 10;

    public static final int DRAFT_LABEL = 30200;
    public static final int DRAFT_KIND = 30201;
    public static final int DRAFT_FIELD_1 = 30202;
    public static final int DRAFT_FIELD_2 = 30203;
    public static final int DRAFT_FIELD_3 = 30204;
    public static final int DRAFT_FIELD_4 = 30205;
    public static final int DRAFT_ADD = 30207;
    public static final int DRAFT_NEW = 30208;

    public static final int STATUS = 30210;
    public static final int RUN = 30211;
    public static final int STOP = 30212;
    public static final int CLEAR = 30213;
    public static final int MESSAGE = 30214;
    public static final int DRAFT_KIND_LABEL = 30220;
    public static final int DRAFT_FIELD_1_LABEL = 30221;
    public static final int DRAFT_FIELD_2_LABEL = 30222;
    public static final int DRAFT_FIELD_3_LABEL = 30223;
    public static final int DRAFT_FIELD_4_LABEL = 30224;

    static final int ORANGE = 0xff981f;
    static final int WHITE = 0xffffff;
    static final int YELLOW = 0xffff00;
    static final int GREY = 0x9f9f9f;
    /** The client's own interface background colour and a lighter edge for frames and dividers. */
    static final int PANEL = 0x332d25;
    static final int EDGE = 0x8d7f63;

    public static final int BUILDER_PANEL = 30103;
    public static final int BUILDER_FRAME = 30104;
    public static final int BUILDER_DIVIDER_1 = 30105;
    public static final int BUILDER_DIVIDER_2 = 30106;

    private static final int TAB_WIDTH = 190;
    private static final int TAB_HEIGHT = 261;
    private static final int SCREEN_WIDTH = 512;
    private static final int SCREEN_HEIGHT = 334;
    private static final int LINE = 14;
    private static final int ROW_TOP = 28;
    private static final int ROW_HEIGHT = 18;

    private static final Map<Integer, WidgetSpec> SPECS = build();

    private FlowWidgets() {
    }

    public static int rowText(int row) {
        return ROW_BASE + row * ROW_STRIDE;
    }

    public static int rowUp(int row) {
        return rowText(row) + 1;
    }

    public static int rowDown(int row) {
        return rowText(row) + 2;
    }

    public static int rowDelete(int row) {
        return rowText(row) + 3;
    }

    public static Map<Integer, WidgetSpec> specs() {
        return Collections.unmodifiableMap(SPECS);
    }

    private static Map<Integer, WidgetSpec> build() {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        tab(specs);
        builder(specs);
        return specs;
    }

    private static void tab(Map<Integer, WidgetSpec> specs) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.text(TAB_TITLE, TAB, 10, 6, 170, LINE, "IdleRS", ORANGE, FONT_BOLD));
        add(specs, children, WidgetSpec.text(TAB_STATUS_1, TAB, 10, 30, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(TAB_STATUS_2, TAB, 10, 46, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(TAB_STATUS_3, TAB, 10, 62, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, button(TAB_OPEN_BUILDER, TAB, 10, 92, 100, LINE, "Flow builder", "Open"));
        add(specs, children, button(TAB_RUN, TAB, 10, 114, 40, LINE, "Run", "Run the flow"));
        add(specs, children, button(TAB_STOP, TAB, 60, 114, 40, LINE, "Stop", "Stop the flow"));
        specs.put(TAB, WidgetSpec.layer(TAB, -1, 0, 0, TAB_WIDTH, TAB_HEIGHT, children));
    }

    private static void builder(Map<Integer, WidgetSpec> specs) {
        List<Integer> children = new ArrayList<>();
        // The panel comes first so everything else draws over it; the title is centred, away from the hover text
        // the client writes in the top-left corner of the game view.
        add(specs, children, WidgetSpec.box(BUILDER_PANEL, BUILDER, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, PANEL));
        add(specs, children, WidgetSpec.frame(BUILDER_FRAME, BUILDER, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, EDGE));
        add(specs, children, WidgetSpec.box(BUILDER_DIVIDER_1, BUILDER, 8, 164, SCREEN_WIDTH - 16, 1, EDGE));
        add(specs, children, WidgetSpec.box(BUILDER_DIVIDER_2, BUILDER, 8, 254, SCREEN_WIDTH - 16, 1, EDGE));
        add(specs, children, WidgetSpec.centredText(BUILDER_TITLE, BUILDER, SCREEN_WIDTH / 2, 6, LINE, "Flow builder", ORANGE, FONT_BOLD));
        add(specs, children, button(BUILDER_CLOSE, BUILDER, 460, 6, 42, LINE, "Close", "Close"));
        for (int row = 0; row < ROWS; row++) {
            int y = ROW_TOP + row * ROW_HEIGHT;
            add(specs, children, WidgetSpec.button(rowText(row), BUILDER, 10, y, 370, LINE, "", WHITE, YELLOW, FONT_PLAIN, "Edit"));
            add(specs, children, button(rowUp(row), BUILDER, 390, y, 22, LINE, "", "Move up"));
            add(specs, children, button(rowDown(row), BUILDER, 418, y, 38, LINE, "", "Move down"));
            add(specs, children, button(rowDelete(row), BUILDER, 466, y, 30, LINE, "", "Delete"));
        }
        int labels = 182;
        int fields = 196;
        add(specs, children, WidgetSpec.text(DRAFT_LABEL, BUILDER, 10, 168, 300, LINE, "", YELLOW, FONT_PLAIN));
        // The field labels come from the server with the fields: each kind of step names its own.
        add(specs, children, WidgetSpec.text(DRAFT_KIND_LABEL, BUILDER, 10, labels, 92, LINE, "step", GREY, FONT_SMALL));
        add(specs, children, WidgetSpec.text(DRAFT_FIELD_1_LABEL, BUILDER, 106, labels, 98, LINE, "", GREY, FONT_SMALL));
        add(specs, children, WidgetSpec.text(DRAFT_FIELD_2_LABEL, BUILDER, 208, labels, 98, LINE, "", GREY, FONT_SMALL));
        add(specs, children, WidgetSpec.text(DRAFT_FIELD_3_LABEL, BUILDER, 310, labels, 98, LINE, "", GREY, FONT_SMALL));
        add(specs, children, WidgetSpec.text(DRAFT_FIELD_4_LABEL, BUILDER, 412, labels, 92, LINE, "", GREY, FONT_SMALL));
        add(specs, children, button(DRAFT_KIND, BUILDER, 10, fields, 92, LINE, "", "Change"));
        add(specs, children, button(DRAFT_FIELD_1, BUILDER, 106, fields, 98, LINE, "", "Change"));
        add(specs, children, button(DRAFT_FIELD_2, BUILDER, 208, fields, 98, LINE, "", "Change"));
        add(specs, children, button(DRAFT_FIELD_3, BUILDER, 310, fields, 98, LINE, "", "Change"));
        add(specs, children, button(DRAFT_FIELD_4, BUILDER, 412, fields, 92, LINE, "", "Change"));
        add(specs, children, button(DRAFT_ADD, BUILDER, 10, 218, 90, LINE, "", "Add or save"));
        add(specs, children, button(DRAFT_NEW, BUILDER, 110, 218, 70, LINE, "New step", "Start a new step"));
        add(specs, children, WidgetSpec.text(STATUS, BUILDER, 10, 262, 370, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, button(RUN, BUILDER, 390, 262, 30, LINE, "Run", "Run the flow"));
        add(specs, children, button(STOP, BUILDER, 428, 262, 34, LINE, "Stop", "Stop the flow"));
        add(specs, children, button(CLEAR, BUILDER, 468, 262, 38, LINE, "Clear", "Clear the flow"));
        add(specs, children, WidgetSpec.text(MESSAGE, BUILDER, 10, 290, 490, LINE, "", YELLOW, FONT_PLAIN));
        specs.put(BUILDER, WidgetSpec.layer(BUILDER, -1, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, children));
    }

    private static WidgetSpec button(int id, int parent, int x, int y, int width, int height, String text, String tooltip) {
        return WidgetSpec.button(id, parent, x, y, width, height, text, ORANGE, YELLOW, FONT_PLAIN, tooltip);
    }
}
