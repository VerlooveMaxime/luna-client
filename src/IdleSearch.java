import idlers.PageRequest;
import idlers.SearchGrid;
import idlers.SearchPage;
import idlers.SearchPrompt;
import idlers.SearchRow;
import idlers.SearchWidgets;
import idlers.WidgetPicture;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * IdleRS: the chatbox search ({@link SearchPrompt}) in the 377's dead item search slot ({@code chatboxInterfaceType}
 * 3): drawn with the client's fonts, paged and picked over our packets.
 */
final class IdleSearch {

    /** Incoming: the server opens a prompt, then sends pages of its rows. */
    static final int OPEN = 103;
    static final int ROWS = 105;
    /** Outgoing: a page asked for, the row picked, the prompt closed without a pick. */
    static final int PAGE = 102;
    static final int PICK = 103;
    static final int CLOSED = 105;

    /** The client-only menu action of "Pick" (the compass menu has 1100 and 1101). */
    static final int PICK_ACTION = 1102;

    private static final SearchPrompt PROMPT = new SearchPrompt();

    /** What each icon widget shows, so a picture is set again only when it changes. */
    private static final WidgetPicture[] ICONS_SHOWN = new WidgetPicture[SearchGrid.MOST_ICONS];

    private static JagFont small;
    private static JagFont plain;
    private static JagFont bold;

    /** The place of the row under the mouse that can be picked, -1 for none. */
    private static int hovered = -1;

    private IdleSearch() {
    }

    static void fonts(JagFont small, JagFont plain, JagFont bold) {
        IdleSearch.small = small;
        IdleSearch.plain = plain;
        IdleSearch.bold = bold;
    }

    static void open(JagBuffer buffer) {
        WidgetPicture.Reader in = IdleWidgets.reader(buffer);
        PROMPT.open(in.u8(), in.string(), in.string());
        Arrays.fill(ICONS_SHOWN, null);
        hovered = -1;
    }

    /** True when the page starts new results, which show from the top. */
    static boolean fill(JagBuffer buffer) {
        return PROMPT.receive(SearchPage.read(IdleWidgets.reader(buffer)));
    }

    /** Called every frame while the prompt is open: sends the page the prompt wants, if any. */
    static void update(JagBuffer out, String typed, int scroll, long now) {
        PROMPT.type(typed, now);
        PROMPT.update(now, scroll).ifPresent(request -> sendPage(out, request));
    }

    static int contentHeight() {
        return PROMPT.grid().contentHeight();
    }

    static boolean scrolls() {
        return PROMPT.grid().scrolls();
    }

    static int clampScroll(int scroll) {
        return PROMPT.grid().clampScroll(scroll);
    }

    static int hovered() {
        return hovered;
    }

    /** The row at chatbox point ({@code x}, {@code y}) if it can be picked; it is drawn hovered from now on. */
    static Optional<SearchRow> hover(int x, int y, int scroll) {
        OptionalInt place = PROMPT.grid().at(x, y, scroll);
        Optional<SearchRow> row = place.isPresent() ? PROMPT.row(place.getAsInt()).filter(found -> !found.greyed()) : Optional.empty();
        hovered = row.isPresent() ? place.getAsInt() : -1;
        return row;
    }

    /**
     * Draws the prompt on the chatbox but its icons, which are placed on their widgets: the caller draws the returned
     * layer, then the scrollbar. A row not loaded yet is an empty cell.
     */
    static JagInterface draw(String typed, int scroll) {
        drawTitle();
        SearchGrid grid = PROMPT.grid();
        Drawable.method446(SearchGrid.TOP, 0, SearchGrid.TOP + SearchGrid.HEIGHT, SearchGrid.WIDTH, true);
        for (int place : grid.visible(scroll)) {
            SearchGrid.Cell cell = grid.cell(place, scroll);
            PROMPT.row(place).ifPresent(row -> drawCell(row, cell, grid.cellWidth(), place == hovered));
        }
        Drawable.method445();
        PROMPT.message().ifPresent(line -> bold.method470(239, 452, SearchWidgets.MESSAGE_Y, SearchWidgets.MESSAGE, line));
        plain.method474(2245, SearchWidgets.TYPED_X, SearchWidgets.TYPED, SearchWidgets.TYPED_Y, typed + "*");
        small.method474(2245, SearchWidgets.HINT_RIGHT - small.method473(SearchWidgets.HINT, (byte) -53),
                SearchWidgets.HINT_COLOUR, SearchWidgets.TYPED_Y, SearchWidgets.HINT);
        return placeIcons(grid, scroll);
    }

    /** The title over the rows; drawn again after the icons, which the client's models let rise into it. */
    static void drawTitle() {
        bold.method470(239, 452, SearchWidgets.TITLE_Y, SearchWidgets.TITLE, PROMPT.title());
    }

    private static void drawCell(SearchRow row, SearchGrid.Cell cell, int width, boolean hovered) {
        if (hovered)
            Drawable.method448(false, SearchWidgets.HOVER, cell.y(), width, SearchGrid.CELL_HEIGHT, SearchWidgets.HOVER_OPACITY,
                    cell.x());
        small.method474(2245, cell.textX(), row.greyed() ? SearchWidgets.GREYED_LABEL : SearchWidgets.LABEL,
                cell.y() + SearchWidgets.LABEL_Y, row.label());
        small.method474(2245, cell.textX(), row.greyed() ? SearchWidgets.GREYED_REASON : SearchWidgets.NOTE,
                cell.y() + SearchWidgets.NOTE_Y, row.note());
    }

    private static JagInterface placeIcons(SearchGrid grid, int scroll) {
        JagInterface layer = JagInterface.forId(SearchWidgets.ICONS);
        List<Integer> withIcons = grid.withIcons(scroll);
        for (int place = 0; place < SearchGrid.MOST_ICONS; place++) {
            WidgetPicture picture = new WidgetPicture.None();
            if (place < withIcons.size()) {
                SearchGrid.Cell cell = grid.cell(withIcons.get(place), scroll);
                layer.childX[place] = cell.iconX();
                layer.childY[place] = cell.iconY() - SearchGrid.TOP;
                picture = PROMPT.row(withIcons.get(place)).map(SearchRow::picture).orElse(picture);
            }
            if (!picture.equals(ICONS_SHOWN[place])) {
                IdleWidgets.show(JagInterface.forId(SearchWidgets.icon(place)), picture);
                ICONS_SHOWN[place] = picture;
            }
        }
        return layer;
    }

    private static void sendPage(JagBuffer out, PageRequest request) {
        out.putOpcode(PAGE);
        out.putByte(0);
        int start = out.position;
        out.putByte(request.serial());
        out.putShort(request.offset());
        out.putByte(request.count());
        out.putString(request.query());
        out.putLength(out.position - start);
    }

    /** The pick of row {@code index} (the server's own index) as the prompt's serial names it. */
    static void sendPick(JagBuffer out, int index) {
        out.putOpcode(PICK);
        out.putByte(PROMPT.serial());
        out.putShort(index);
    }

    static void sendClosed(JagBuffer out) {
        out.putOpcode(CLOSED);
        out.putByte(PROMPT.serial());
    }
}
