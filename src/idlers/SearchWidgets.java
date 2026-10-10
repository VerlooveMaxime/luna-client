package idlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static idlers.WidgetSpecs.add;

/**
 * The search prompt's look in the chatbox (the mockup's) and the widgets that draw its icons: a layer over the rows'
 * area holding {@link SearchGrid#MOST_ICONS} pictures, which the client places on the visible cells, so items, npc
 * bodies and sprites draw as any picture widget does. The server never names these ids.
 */
public final class SearchWidgets {

    public static final int FIRST_ID = 30600;
    public static final int ID_LIMIT = 30610;

    public static final int ICONS = 30600;

    /** Npc bodies seen as in the gallery and the builder's slots. */
    static final int PITCH = WidgetGallery.NPC_PITCH;
    static final int YAW = WidgetGallery.NPC_YAW;

    /** Baselines in the chatbox: the title, a cell's label and note below its top, the typed line. */
    public static final int TITLE_Y = 13;
    /** How far the title's bold font reaches below its baseline (g, j, q, y), from the cache's b12_full. */
    public static final int TITLE_DESCENT = 4;
    public static final int LABEL_Y = 12;
    public static final int NOTE_Y = 25;
    public static final int MESSAGE_Y = 47;
    public static final int TYPED_Y = 90;
    public static final int TYPED_X = 4;
    public static final int HINT_RIGHT = 475;
    public static final String HINT = "Escape: cancel";

    /** The name mode, drawn as the 377's "Enter amount:" (Maxime): title and typed line centred, a hint at the bottom. */
    public static final int NAME_TITLE_Y = 40;
    public static final int NAME_TYPED_Y = 60;
    public static final int NAME_HINT_Y = 90;
    public static final String NAME_HINT = "Enter to save, Escape to cancel";

    public static final int TITLE = 0x000000;
    public static final int LABEL = 0x000000;
    public static final int NOTE = 0x3a3a3a;
    public static final int GREYED_LABEL = 0x7a6c52;
    public static final int GREYED_REASON = 0x8b0000;
    public static final int MESSAGE = 0x000080;
    public static final int TYPED = 0x0000ff;
    public static final int HINT_COLOUR = 0x555555;
    public static final int NAME_TYPED = 0x000080;
    /** A white veil over the hovered cell, at this opacity out of 256. */
    public static final int HOVER = 0xffffff;
    public static final int HOVER_OPACITY = 90;

    private static final Map<Integer, WidgetSpec> SPECS = build();

    private SearchWidgets() {
    }

    public static Map<Integer, WidgetSpec> specs() {
        return Collections.unmodifiableMap(SPECS);
    }

    public static int icon(int place) {
        return ICONS + 1 + place;
    }

    private static Map<Integer, WidgetSpec> build() {
        Map<Integer, WidgetSpec> specs = new LinkedHashMap<>();
        List<Integer> children = new ArrayList<>();
        for (int place = 0; place < SearchGrid.MOST_ICONS; place++) {
            add(specs, children, new WidgetSpec.Picture(icon(place), ICONS, 0, 0, SearchGrid.ICON, SearchGrid.ICON, PITCH, YAW));
        }
        specs.put(ICONS, WidgetSpec.layer(ICONS, -1, 0, SearchGrid.TOP, SearchGrid.WIDTH, SearchGrid.HEIGHT, children));
        return specs;
    }
}
