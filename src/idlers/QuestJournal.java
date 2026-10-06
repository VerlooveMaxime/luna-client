package idlers;

import java.util.Arrays;

import static idlers.WidgetSpec.FONT_BOLD;
import static idlers.WidgetSpec.FONT_PLAIN;

/**
 * The stages IdleRS lists at the top of the 377 quest journal: a "STAGES:" header and one line per stage above the
 * 2006 quests, which move down to make room. Only Tutorial Island so far; the server sends its name, coloured like a
 * quest (red, yellow, green). Positions and styles copy the cache's quest list (layer 639).
 */
public final class QuestJournal {

    /** The quest journal's scrolling list. */
    public static final int LIST = 639;

    public static final int STAGES_HEADER = 30300;
    public static final int TUTORIAL_LINE = 30301;
    public static final int ID_LIMIT = 30310;

    static final String HEADER_TEXT = "STAGES:";
    static final int HEADER_X = 5;
    static final int HEADER_Y = 8;
    static final int LINE_X = 10;
    static final int LINE_Y = 23;
    /** One quest line (15) and the gap above the members' header (19), so "FREE QUESTS:" sits like a second header. */
    static final int SHIFT = 34;

    private static final int ORANGE = 0xf99b15;
    private static final int RED = 0xff0000;
    private static final int LINE_HEIGHT = 14;

    private QuestJournal() {
    }

    /** The children and scroll height of a quest list layer once the stages are in. */
    public record Layout(int[] ids, int[] xs, int[] ys, int scrollHeight) {
    }

    /** The widgets the stages add, or null for any other id. */
    public static WidgetSpec spec(int id) {
        if (id == STAGES_HEADER)
            return WidgetSpec.text(STAGES_HEADER, 638, HEADER_X, HEADER_Y, 84, LINE_HEIGHT, HEADER_TEXT, ORANGE, FONT_BOLD);
        if (id == TUTORIAL_LINE)
            return WidgetSpec.text(TUTORIAL_LINE, 638, LINE_X, LINE_Y, 131, LINE_HEIGHT, "", RED, FONT_PLAIN);
        return null;
    }

    /**
     * The quest list with the stages on top and every quest moved down; a list that already has them is returned as
     * it is, since the client parses a dropped list again.
     */
    public static Layout withStages(int[] ids, int[] xs, int[] ys, int scrollHeight) {
        if (Arrays.stream(ids).anyMatch(id -> id == STAGES_HEADER))
            return new Layout(ids, xs, ys, scrollHeight);
        int count = ids.length + 2;
        int[] newIds = new int[count];
        int[] newXs = new int[count];
        int[] newYs = new int[count];
        newIds[0] = STAGES_HEADER;
        newXs[0] = HEADER_X;
        newYs[0] = HEADER_Y;
        newIds[1] = TUTORIAL_LINE;
        newXs[1] = LINE_X;
        newYs[1] = LINE_Y;
        for (int i = 0; i < ids.length; i++) {
            newIds[i + 2] = ids[i];
            newXs[i + 2] = xs[i];
            newYs[i + 2] = ys[i] + SHIFT;
        }
        return new Layout(newIds, newXs, newYs, scrollHeight + SHIFT);
    }
}
