package idlers;

import java.util.ArrayList;
import java.util.List;

/**
 * One row of the chatbox search: {@code index} names it to the server, which keeps the value the row sets; a greyed
 * row shows its reason as its note and cannot be picked.
 */
public record SearchRow(int index, String label, String note, boolean greyed, WidgetPicture picture) {

    /** Bit of the flags byte set on a greyed row. */
    public static final int GREYED = 1;

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
        boolean greyed = (in.u8() & GREYED) != 0;
        String label = in.string();
        String note = in.string();
        return new SearchRow(index, label, note, greyed, WidgetPicture.read(in));
    }
}
