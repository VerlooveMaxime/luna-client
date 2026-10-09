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
 * these ids in {@code game.idle.ui.GalleryWidgets} and fills the pictures and the tiles' texts): draggable hover tiles
 * in a scrolling layer, nested layers, a tooltip, greyed and struck text, the step icons at the sizes the builder may
 * use, item icons, npc bodies, and a layer the server hides and shows.
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
    /** Room for every kind of step's icon; the server fills them in its order. */
    public static final int ICON_KINDS = 12;

    public static final int ITEMS_TITLE = 30490;
    private static final int ITEM_BASE = 30491;
    /** The item icons at full size, then shrunk. */
    public static final List<Integer> ITEM_SIZES = List.of(32, 18);
    public static final int ITEM_KINDS = 2;

    private static final int NPC_BASE = 30581;
    private static final int NPC_NAME_BASE = 30585;
    public static final int NPCS = 4;
    private static final int NPC_PITCH_X = 58;
    public static final int NPC_SIZE = 44;
    /** Seen a little from above and turned towards the camera, like the cache's dialogue heads (yaw 1882). */
    static final int NPC_PITCH = 120;
    static final int NPC_YAW = 1882;

    public static final int TILES = 8;
    private static final int TILE_BASE = 30500;
    private static final int TILE_STRIDE = 10;
    private static final int TILE_WIDTH = 116;
    private static final int TILE_HEIGHT = 70;
    private static final int TILE_GAP = 4;

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

    public static int item(int size, int kind) {
        return ITEM_BASE + size * ITEM_KINDS + kind;
    }

    public static int npc(int npc) {
        return NPC_BASE + npc;
    }

    /** Filled by the server: a picture alone does not say which npc it is (Maxime). */
    public static int npcName(int npc) {
        return NPC_NAME_BASE + npc;
    }

    public static int tile(int tile) {
        return TILE_BASE + tile * TILE_STRIDE;
    }

    /** The part of a tile the click and the drag go to. */
    public static int tileFace(int tile) {
        return tile(tile) + 1;
    }

    public static int tilePicture(int tile) {
        return tile(tile) + 3;
    }

    public static int tileLabel(int tile) {
        return tile(tile) + 4;
    }

    public static int tileKind(int tile) {
        return tile(tile) + 5;
    }

    private static Map<Integer, WidgetSpec> build() {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        add(specs, children, WidgetSpec.box(PANEL, GALLERY, 0, 0, WIDTH, HEIGHT, FlowWidgets.PANEL));
        add(specs, children, WidgetSpec.frame(FRAME, GALLERY, 0, 0, WIDTH, HEIGHT, FlowWidgets.EDGE));
        add(specs, children, WidgetSpec.centredText(TITLE, GALLERY, WIDTH / 2, 6, LINE, "Widget gallery", FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, button(CLOSE, 460, 6, 42, "Close", "Close"));
        add(specs, children, tiles(specs));
        items(specs, children);
        npcs(specs, children);
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
                "Click or drag a tile; tiles and buttons answer in the chat box. Close and open: the gallery comes back fresh.",
                FlowWidgets.GREY, FONT_SMALL));
        // Last, so the box the client draws under the hovered area covers the widgets below it.
        add(specs, children, WidgetSpec.text(TOOLTIP_TEXT, GALLERY, RIGHT + 96, 166, 140, LINE, "Hover here for 2 s", FlowWidgets.WHITE, FONT_PLAIN));
        add(specs, children, new WidgetSpec.Tooltip(TOOLTIP, GALLERY, RIGHT + 96, 164, 140, 16,
                "A type 8 tooltip,\\nshown after two seconds."));
        specs.put(GALLERY, WidgetSpec.layer(GALLERY, -1, 0, 0, WIDTH, HEIGHT, children));
        return specs;
    }

    /** Two columns of draggable tiles, each a layer of its own, in a layer that scrolls: three levels below the root. */
    private static WidgetSpec tiles(Map<Integer, WidgetSpec> specs) {
        List<Integer> children = new ArrayList<>();
        for (int tile = 0; tile < TILES; tile++) {
            int x = tile % 2 * (TILE_WIDTH + TILE_GAP);
            int y = tile / 2 * (TILE_HEIGHT + TILE_GAP);
            add(specs, children, buildTile(specs, tile, x, y));
        }
        int rows = (TILES + 1) / 2;
        return WidgetSpec.scrollLayer(SCROLL, GALLERY, 8, 28, 2 * TILE_WIDTH + TILE_GAP, 2 * TILE_HEIGHT + TILE_GAP,
                rows * (TILE_HEIGHT + TILE_GAP) - TILE_GAP, children);
    }

    private static WidgetSpec buildTile(Map<Integer, WidgetSpec> specs, int tile, int x, int y) {
        int id = tile(tile);
        List<Integer> children = new ArrayList<>();
        add(specs, children, new WidgetSpec.Tile(tileFace(tile), id, 0, 0, TILE_WIDTH, TILE_HEIGHT, TILE_COLOUR, TILE_HOVER,
                "Select", true));
        add(specs, children, WidgetSpec.frame(id + 2, id, 0, 0, TILE_WIDTH, TILE_HEIGHT, TILE_EDGE));
        add(specs, children, new WidgetSpec.Picture(tilePicture(tile), id, 5, 5, 25, 25, 0, 0));
        add(specs, children, WidgetSpec.text(tileLabel(tile), id, 36, 6, 76, LINE, "", FlowWidgets.ORANGE, FONT_BOLD));
        add(specs, children, WidgetSpec.text(tileKind(tile), id, 36, 22, 76, LINE, "", FlowWidgets.GREY, FONT_SMALL));
        add(specs, children, WidgetSpec.text(id + 6, id, 5, 36, 106, LINE, "A summary line", FlowWidgets.WHITE, FONT_PLAIN));
        add(specs, children, WidgetSpec.text(id + 7, id, 5, 52, 106, LINE, "@gry@and a greyed one", FlowWidgets.WHITE, FONT_PLAIN));
        return WidgetSpec.layer(id, SCROLL, x, y, TILE_WIDTH, TILE_HEIGHT, children);
    }

    private static void items(Map<Integer, WidgetSpec> specs, List<Integer> children) {
        add(specs, children, WidgetSpec.text(ITEMS_TITLE, GALLERY, 8, 178, 236, LINE, "Item icons at 32 and 18 px",
                FlowWidgets.GREY, FONT_SMALL));
        for (int kind = 0; kind < ITEM_KINDS; kind++) {
            add(specs, children, new WidgetSpec.Picture(item(0, kind), GALLERY, 8 + kind * 36, 192, 32, 32, 0, 0));
            add(specs, children, new WidgetSpec.Picture(item(1, kind), GALLERY, 84 + kind * 22, 199, 18, 18, 0, 0));
        }
    }

    private static void npcs(Map<Integer, WidgetSpec> specs, List<Integer> children) {
        for (int npc = 0; npc < NPCS; npc++) {
            int x = 8 + npc * NPC_PITCH_X;
            add(specs, children, new WidgetSpec.Picture(npc(npc), GALLERY, x, 228, NPC_SIZE, NPC_SIZE, NPC_PITCH, NPC_YAW));
            add(specs, children, WidgetSpec.centredText(npcName(npc), GALLERY, x + NPC_SIZE / 2, 274, LINE, "", FlowWidgets.WHITE,
                    FONT_SMALL));
        }
    }

    private static void icons(Map<Integer, WidgetSpec> specs, List<Integer> children) {
        add(specs, children, WidgetSpec.text(ICONS_TITLE, GALLERY, RIGHT, 28, 236, LINE, "Step icons at 25, 18 and 13 px",
                FlowWidgets.GREY, FONT_SMALL));
        for (int kind = 0; kind < ICON_KINDS; kind++) {
            add(specs, children, new WidgetSpec.Picture(icon(0, kind), GALLERY, RIGHT + kind % 6 * 30, 44 + kind / 6 * 28,
                    25, 25, 0, 0));
            add(specs, children, new WidgetSpec.Picture(icon(1, kind), GALLERY, RIGHT + kind * 19, 102, 18, 18, 0, 0));
            add(specs, children, new WidgetSpec.Picture(icon(2, kind), GALLERY, RIGHT + kind * 19, 124, 13, 13, 0, 0));
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
