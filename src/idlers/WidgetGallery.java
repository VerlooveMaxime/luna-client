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
 * A developer screen showing every kind of code-defined widget at work (`::widgets` on the server, which mirrors
 * these ids in {@code game.idle.ui.GalleryWidgets}): hover tiles in a scrolling layer, nested layers, a tooltip,
 * greyed and struck text, the step icons at the sizes the builder may use, and a layer the server hides and shows.
 */
public final class WidgetGallery {

    public static final int FIRST_ID = 30400;
    public static final int ID_LIMIT = 30600;

    public static final int GALLERY = 30400;
    public static final int PANEL = 30401;
    public static final int FRAME = 30402;
    public static final int TITLE = 30403;
    public static final int CLOSE = 30404;
    /** Filled by the server each time the gallery opens. */
    public static final int OPENED_LINE = 30405;
    public static final int LEGEND = 30406;

    public static final int SCROLL = 30410;
    public static final int ICONS_TITLE = 30411;
    public static final int TOOLTIP_TEXT = 30412;
    public static final int TOOLTIP = 30413;
    public static final int GREY_LINE = 30414;
    public static final int STRUCK_LINE = 30415;
    public static final int KEYS_TITLE = 30416;
    public static final int KEYS_SMALL = 30417;
    public static final int KEYS_BIG = 30418;

    public static final int HIDEABLE = 30420;
    public static final int HIDEABLE_BOX = 30421;
    public static final int HIDEABLE_TEXT = 30422;
    public static final int HIDE = 30423;
    public static final int SHOW = 30424;

    private static final int ICON_BASE = 30430;
    private static final int ICON_STRIDE = 20;
    /** The step icons at full size, then at two corner sizes for the builder's slots. */
    public static final List<Integer> ICON_SIZES = List.of(25, 18, 13);

    public static final int TILES = 8;
    private static final int TILE_BASE = 30500;
    private static final int TILE_STRIDE = 10;
    private static final int TILE_WIDTH = 116;
    private static final int TILE_HEIGHT = 100;
    private static final int TILE_GAP = 4;

    /** The icon of a kind of step: sprite {@code index} of {@code name} in the media archive. */
    public record StepIcon(String kind, String name, int index) {
    }

    /** The step kinds' icons as S01 picked them; leather gloves (pick up) is an item icon, which comes in S04b. */
    public static final List<StepIcon> STEP_ICONS = List.of(
            new StepIcon("chop", "staticons", 17),
            new StepIcon("mine", "staticons", 12),
            new StepIcon("fish", "staticons", 14),
            new StepIcon("light", "staticons", 16),
            new StepIcon("cook", "staticons", 15),
            new StepIcon("smelt, smith", "staticons", 13),
            new StepIcon("make", "staticons", 10),
            new StepIcon("fight", "staticons", 0),
            new StepIcon("rest", "staticons", 6),
            new StepIcon("walk", "mapmarker", 0),
            new StepIcon("drop", "sideicons", 3),
            new StepIcon("bank", "mapfunction", 5));

    static final int TILE_COLOUR = 0x3b342a;
    static final int TILE_HOVER = 0x4a4134;
    static final int TILE_EDGE = 0x5c5243;

    private static final int WIDTH = 512;
    private static final int HEIGHT = 334;
    private static final int LINE = 14;
    private static final int RIGHT = 268;

    private static final Map<Integer, WidgetSpec> SPECS = build();

    private WidgetGallery() {
    }

    public static Map<Integer, WidgetSpec> specs() {
        return Collections.unmodifiableMap(SPECS);
    }

    public static int icon(int size, int kind) {
        return ICON_BASE + size * ICON_STRIDE + kind;
    }

    public static int tile(int tile) {
        return TILE_BASE + tile * TILE_STRIDE;
    }

    /** The part of a tile the click goes to. */
    public static int tileFace(int tile) {
        return tile(tile) + 1;
    }

    private static Map<Integer, WidgetSpec> build() {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(PANEL, GALLERY, 0, 0, WIDTH, HEIGHT, FlowWidgets.PANEL));
        add(specs, children, WidgetSpec.frame(FRAME, GALLERY, 0, 0, WIDTH, HEIGHT, FlowWidgets.EDGE));
        add(specs, children, WidgetSpec.centredText(TITLE, GALLERY, WIDTH / 2, 6, LINE, "Widget gallery", FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, button(CLOSE, 460, 6, 42, "Close", "Close"));
        add(specs, children, tiles(specs));
        icons(specs, children);
        add(specs, children, WidgetSpec.text(KEYS_TITLE, GALLERY, RIGHT, 144, 236, LINE, "Locked slot: keys at 32 and 48 px", FlowWidgets.GREY, FONT_SMALL));
        add(specs, children, new WidgetSpec.Sprite(KEYS_SMALL, GALLERY, RIGHT, 158, 32, 32, "keys", 0));
        add(specs, children, new WidgetSpec.Sprite(KEYS_BIG, GALLERY, RIGHT + 38, 158, 48, 48, "keys", 0));
        add(specs, children, WidgetSpec.text(GREY_LINE, GALLERY, RIGHT, 212, 236, LINE,
                "@gry@Willow logs @whi@need @gry@Woodcutting 30", FlowWidgets.WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(STRUCK_LINE, GALLERY, RIGHT, 228, 236, LINE,
                "@gry@@str@Greyed and struck through", FlowWidgets.WHITE, FONT_PLAIN));
        add(specs, children, hideable(specs));
        add(specs, children, button(HIDE, RIGHT, 282, 36, "Hide", "Hide the nested layer"));
        add(specs, children, button(SHOW, RIGHT + 44, 282, 40, "Show", "Show the nested layer"));
        add(specs, children, WidgetSpec.text(OPENED_LINE, GALLERY, 8, 296, 496, LINE, "", FlowWidgets.YELLOW, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(LEGEND, GALLERY, 8, 314, 496, LINE,
                "Tiles and buttons answer in the chat box. Close and open again: the gallery comes back fresh.",
                FlowWidgets.GREY, FONT_SMALL));
        // Last, so the box the client draws under the hovered area covers the widgets below it.
        add(specs, children, WidgetSpec.text(TOOLTIP_TEXT, GALLERY, RIGHT + 96, 166, 140, LINE, "Hover here for 2 s", FlowWidgets.WHITE, FONT_PLAIN));
        add(specs, children, new WidgetSpec.Tooltip(TOOLTIP, GALLERY, RIGHT + 96, 164, 140, 16,
                "A type 8 tooltip,\\nshown after two seconds."));
        specs.put(GALLERY, WidgetSpec.layer(GALLERY, -1, 0, 0, WIDTH, HEIGHT, children));
        return specs;
    }

    /** Two columns of tiles, each a layer of its own, in a layer that scrolls: three levels below the root. */
    private static WidgetSpec tiles(Map<Integer, WidgetSpec> specs) {
        List<Integer> children = new ArrayList<>();
        for (int tile = 0; tile < TILES; tile++) {
            int x = tile % 2 * (TILE_WIDTH + TILE_GAP);
            int y = tile / 2 * (TILE_HEIGHT + TILE_GAP);
            add(specs, children, buildTile(specs, tile, x, y));
        }
        int rows = (TILES + 1) / 2;
        return WidgetSpec.scrollLayer(SCROLL, GALLERY, 8, 28, 2 * TILE_WIDTH + TILE_GAP, 260,
                rows * (TILE_HEIGHT + TILE_GAP) - TILE_GAP, children);
    }

    private static WidgetSpec buildTile(Map<Integer, WidgetSpec> specs, int tile, int x, int y) {
        int id = tile(tile);
        StepIcon icon = STEP_ICONS.get(tile);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(id + 1, id, 0, 0, TILE_WIDTH, TILE_HEIGHT, TILE_COLOUR, TILE_HOVER, "Select"));
        add(specs, children, WidgetSpec.frame(id + 2, id, 0, 0, TILE_WIDTH, TILE_HEIGHT, TILE_EDGE));
        add(specs, children, new WidgetSpec.Sprite(id + 3, id, 5, 5, 25, 25, icon.name(), icon.index()));
        add(specs, children, WidgetSpec.text(id + 4, id, 36, 6, 76, LINE, "Tile " + (tile + 1), FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, WidgetSpec.text(id + 5, id, 36, 22, 76, LINE, icon.kind(), FlowWidgets.GREY, FONT_SMALL));
        add(specs, children, WidgetSpec.text(id + 6, id, 5, 44, 106, LINE, "A summary line", FlowWidgets.WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(id + 7, id, 5, 60, 106, LINE, "@gry@and a greyed one", FlowWidgets.WHITE, FONT_PLAIN));
        return WidgetSpec.layer(id, SCROLL, x, y, TILE_WIDTH, TILE_HEIGHT, children);
    }

    private static void icons(Map<Integer, WidgetSpec> specs, List<Integer> children) {
        add(specs, children, WidgetSpec.text(ICONS_TITLE, GALLERY, RIGHT, 28, 236, LINE, "Step icons at 25, 18 and 13 px",
                FlowWidgets.GREY, FONT_SMALL));
        for (int kind = 0; kind < STEP_ICONS.size(); kind++) {
            StepIcon icon = STEP_ICONS.get(kind);
            add(specs, children, new WidgetSpec.Sprite(icon(0, kind), GALLERY, RIGHT + kind % 6 * 30, 44 + kind / 6 * 28,
                    25, 25, icon.name(), icon.index()));
            add(specs, children, new WidgetSpec.Sprite(icon(1, kind), GALLERY, RIGHT + kind * 19, 102, 18, 18, icon.name(),
                    icon.index()));
            add(specs, children, new WidgetSpec.Sprite(icon(2, kind), GALLERY, RIGHT + kind * 19, 124, 13, 13, icon.name(),
                    icon.index()));
        }
    }

    private static WidgetSpec hideable(Map<Integer, WidgetSpec> specs) {
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(HIDEABLE_BOX, HIDEABLE, 0, 0, 236, 30, TILE_COLOUR));
        add(specs, children, WidgetSpec.text(HIDEABLE_TEXT, HIDEABLE, 6, 8, 224, LINE, "A nested layer, hidden by packet 82",
                FlowWidgets.WHITE, FONT_PLAIN));
        return WidgetSpec.layer(HIDEABLE, GALLERY, RIGHT, 246, 236, 30, children);
    }

    private static WidgetSpec button(int id, int x, int y, int width, String text, String option) {
        return WidgetSpec.button(id, GALLERY, x, y, width, LINE, text, FlowWidgets.ORANGE, FlowWidgets.YELLOW, FONT_PLAIN, option);
    }
}
