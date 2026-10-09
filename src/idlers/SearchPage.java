package idlers;

import java.util.List;

/**
 * A page of the chatbox search (packet 105): the rows from place {@code offset} of what {@code query} matches ("" for
 * the opening rows), with how many match in all and the columns the server chose for them.
 */
public record SearchPage(int serial, String query, int total, int columns, int offset, List<SearchRow> rows) {

    public static SearchPage read(WidgetPicture.Reader in) {
        int serial = in.u8();
        String query = in.string();
        int total = in.u16();
        int columns = in.u8();
        int offset = in.u16();
        return new SearchPage(serial, query, total, columns, offset, SearchRow.readAll(in));
    }
}
