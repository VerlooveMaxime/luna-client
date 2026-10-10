package idlers;

import java.util.ArrayList;
import java.util.List;

/**
 * One row of the chatbox search: {@code index} names it to the server, which keeps the value the row sets; a greyed
 * row shows its reason as its note and cannot be picked; a chosen one, in a search that picks several, says so.
 */
public record SearchRow(int index, String label, String note, boolean greyed, WidgetPicture picture, boolean chosen) {

    /** Bits of the flags byte: a greyed row, and a row chosen in a search that picks several (its note drawn green). */
    public static final int GREYED = 1;
    public static final int CHOSEN = 2;

    /** Packet 105's rows: a count, then each row's index, flags, label, note and picture. */
    public static List<SearchRow> readAll(WidgetPicture.Reader in) {
        int count = in.u16();
        List<SearchRow> rows = new ArrayList<>(count);
        for (int row = 0; row < count; row++) {
            rows.add(read(in));
        }
        return List.copyOf(rows);
    }

    static SearchRow read(WidgetPicture.Reader in) {
        int index = in.u16();
        int flags = in.u8();
        String label = in.string();
        String note = in.string();
        return new SearchRow(index, label, note, (flags & GREYED) != 0, WidgetPicture.read(in), (flags & CHOSEN) != 0);
    }
}
