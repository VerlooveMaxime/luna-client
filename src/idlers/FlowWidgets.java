package idlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static idlers.WidgetSpec.FONT_BOLD;
import static idlers.WidgetSpec.FONT_PLAIN;
import static idlers.WidgetSpecs.add;

/**
 * The Idle tab (tab slot 7, unused by the 377 client): status lines, the button that opens the flow builder, Run and
 * Stop; and the colours the builder's screens share. Ids start at {@link #FIRST_ID}, above every cache widget. The
 * server mirrors these ids in {@code game.idle.ui.FlowWidgets}; text the server changes at runtime starts empty here.
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

    static final int ORANGE = 0xff981f;
    static final int WHITE = 0xffffff;
    static final int YELLOW = 0xffff00;
    static final int GREY = 0x9f9f9f;
    /** The client's own interface background colour and a lighter edge for frames and dividers. */
    static final int PANEL = 0x332d25;
    static final int EDGE = 0x8d7f63;

    private static final int TAB_WIDTH = 190;
    private static final int TAB_HEIGHT = 261;
    private static final int LINE = 14;

    private static final Map<Integer, WidgetSpec> SPECS = build();

    private FlowWidgets() {
    }

    public static Map<Integer, WidgetSpec> specs() {
        return Collections.unmodifiableMap(SPECS);
    }

    private static Map<Integer, WidgetSpec> build() {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.text(TAB_TITLE, TAB, 10, 6, 170, LINE, "IdleRS", ORANGE, FONT_BOLD));
        add(specs, children, WidgetSpec.text(TAB_STATUS_1, TAB, 10, 30, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(TAB_STATUS_2, TAB, 10, 46, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(TAB_STATUS_3, TAB, 10, 62, 170, LINE, "", WHITE, FONT_PLAIN));
        add(specs, children, button(TAB_OPEN_BUILDER, 10, 92, 100, "Flow builder", "Open"));
        add(specs, children, button(TAB_RUN, 10, 114, 40, "Run", "Run the flow"));
        add(specs, children, button(TAB_STOP, 60, 114, 40, "Stop", "Stop the flow"));
        specs.put(TAB, WidgetSpec.layer(TAB, -1, 0, 0, TAB_WIDTH, TAB_HEIGHT, children));
        return specs;
    }

    private static WidgetSpec button(int id, int x, int y, int width, String text, String tooltip) {
        return WidgetSpec.button(id, TAB, x, y, width, LINE, text, ORANGE, YELLOW, FONT_PLAIN, tooltip);
    }
}
