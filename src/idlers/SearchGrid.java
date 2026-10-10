package idlers;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Where the search rows sit in the chatbox, under the prompt's title: {@code count} cells in the {@code columns} the
 * server chose (Maxime, 2026-10-09), each a 24 px icon beside a label and a note. Coordinates are the chatbox's,
 * scrolled by the rows' scroll position; a cell's place is its row's place in the results.
 */
public final class SearchGrid {

    /** The chatbox's width left of the scrollbar. */
    public static final int WIDTH = 463;
    /** The rows' area under the title, which the scrollbar spans. */
    public static final int TOP = 17;
    public static final int HEIGHT = 60;
    public static final int CELL_HEIGHT = 28;
    public static final int ICON = 24;
    /** Room around a cell's content and above the first row. */
    public static final int PAD = 2;
    /**
     * The band painted again over the rows' icons, title included: one row into the padding, since the client's font
     * drops a glyph's last row when it ends on a clip's bottom, and the title's descenders end on the rows' top.
     */
    public static final int TITLE_BAND = TOP + 1;
    /** Where a cell's text starts, right of its icon. */
    public static final int TEXT_X = PAD + ICON + 4;
    /** The most icons the area shows at once: 3 rows of 3, partly hidden ones included. */
    public static final int MOST_ICONS = 9;
    /** One screen of the area: the grid rows it shows whole. Rows are kept one screen above and below the view. */
    public static final int SCREEN_ROWS = 2;
    /** The most columns a grid takes. */
    public static final int MOST_COLUMNS = 3;

    private final int columns;
    private final int count;

    public SearchGrid(int columns, int count) {
        this.columns = columns;
        this.count = count;
    }

    public int columns() {
        return columns;
    }

    public int cellWidth() {
        return WIDTH / columns;
    }

    private int gridRows() {
        return (count + columns - 1) / columns;
    }

    public int contentHeight() {
        return gridRows() * CELL_HEIGHT + 2 * PAD;
    }

    /** True when the rows overflow their area, which then shows a scrollbar. */
    public boolean scrolls() {
        return contentHeight() > HEIGHT;
    }

    public int clampScroll(int scroll) {
        return Math.max(0, Math.min(scroll, contentHeight() - HEIGHT));
    }

    /** The top left corner of the cell at {@code place}. */
    public Cell cell(int place, int scroll) {
        return new Cell(place % columns * cellWidth(), TOP + PAD + place / columns * CELL_HEIGHT - scroll);
    }

    private static int firstRowInView(int scroll) {
        return Math.max(0, Math.floorDiv(scroll - PAD, CELL_HEIGHT));
    }

    private int lastRowInView(int scroll) {
        return Math.min(gridRows() - 1, Math.floorDiv(HEIGHT + scroll - PAD - 1, CELL_HEIGHT));
    }

    /** The cells drawn: those at least partly inside the rows' area, first to last. */
    public List<Integer> visible(int scroll) {
        return IntStream.range(firstRowInView(scroll) * columns, Math.min(count, (lastRowInView(scroll) + 1) * columns))
                .boxed().toList();
    }

    /** The cells whose icon shows, at most {@link #MOST_ICONS}. */
    public List<Integer> withIcons(int scroll) {
        return visible(scroll).stream().filter(place -> {
            int iconY = cell(place, scroll).iconY();
            return iconY + ICON > TOP && iconY < TOP + HEIGHT;
        }).toList();
    }

    /** The places kept loaded: the rows in view and one screen above and below (Maxime, 2026-10-09). */
    public Window window(int scroll) {
        return new Window(Math.max(0, (firstRowInView(scroll) - SCREEN_ROWS) * columns),
                Math.min(count, (lastRowInView(scroll) + SCREEN_ROWS + 1) * columns));
    }

    /** How many rows the window at the top holds in the widest grid: what a new search asks for first. */
    public static int firstPage() {
        return (Math.floorDiv(HEIGHT - PAD - 1, CELL_HEIGHT) + SCREEN_ROWS + 1) * MOST_COLUMNS;
    }

    /** The cell under point ({@code x}, {@code y}), if any. */
    public OptionalInt at(int x, int y, int scroll) {
        if (x < 0 || x >= columns * cellWidth() || y < TOP || y >= TOP + HEIGHT) {
            return OptionalInt.empty();
        }
        int below = y - TOP - PAD + scroll;
        if (below < 0) {
            return OptionalInt.empty();
        }
        int place = below / CELL_HEIGHT * columns + x / cellWidth();
        return place < count ? OptionalInt.of(place) : OptionalInt.empty();
    }

    /** A cell's top left corner. */
    public record Cell(int x, int y) {

        public int iconX() {
            return x + PAD;
        }

        public int iconY() {
            return y + PAD;
        }

        public int textX() {
            return x + TEXT_X;
        }
    }

    /** Places {@code from} (included) to {@code to} (excluded). */
    public record Window(int from, int to) {

        public boolean contains(int place) {
            return place >= from && place < to;
        }
    }
}
