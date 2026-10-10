package idlers;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchRowTest {

    /** Hands out the values a packet holds, in order, whatever their size. */
    private static WidgetPicture.Reader reader(Object... values) {
        Deque<Object> left = new ArrayDeque<>(List.of(values));
        return new WidgetPicture.Reader() {
            public int u8() {
                return (Integer) left.pop();
            }

            public int u16() {
                return (Integer) left.pop();
            }

            public String string() {
                return (String) left.pop();
            }
        };
    }

    @Test
    void aRowIsItsIndexItsFlagsItsTextsAndItsPicture() {
        List<SearchRow> rows = SearchRow.readAll(reader(1, 7, 0, "Oak", "Woodcutting 15", WidgetPicture.ITEM, 1521));

        assertEquals(List.of(new SearchRow(7, "Oak", "Woodcutting 15", false, new WidgetPicture.Item(1521), false)), rows);
    }

    @Test
    void theGreyedBitMarksARowThatCannotBePicked() {
        List<SearchRow> rows = SearchRow.readAll(reader(1, 0, SearchRow.GREYED, "Yew", "needs Woodcutting 60", WidgetPicture.NONE));

        assertTrue(rows.get(0).greyed());
    }

    @Test
    void theChosenBitMarksARowChosenThatCanStillBePicked() {
        SearchRow row = SearchRow.readAll(reader(1, 0, SearchRow.CHOSEN, "Oak logs", "chosen", WidgetPicture.NONE)).get(0);

        assertEquals(List.of(true, false), List.of(row.chosen(), row.greyed()));
    }

    @Test
    void otherFlagBitsLeaveARowUsableAndNotChosen() {
        SearchRow row = SearchRow.readAll(reader(1, 0, 4, "Tree", "Woodcutting 1", WidgetPicture.NONE)).get(0);

        assertEquals(List.of(false, false), List.of(row.greyed(), row.chosen()));
    }

    @Test
    void theCountComesFirstThenEachRowInOrder() {
        List<SearchRow> rows = SearchRow.readAll(reader(2,
                0, 0, "Banks", "", WidgetPicture.MEDIA, "mapfunction", 5,
                1, 0, "Rat", "level 1", WidgetPicture.NPC_BODY, 86));

        assertEquals(List.of(new SearchRow(0, "Banks", "", false, new WidgetPicture.Media("mapfunction", 5), false),
                new SearchRow(1, "Rat", "level 1", false, new WidgetPicture.NpcBody(86), false)), rows);
    }

    @Test
    void noRowsIsAnEmptyList() {
        assertEquals(List.of(), SearchRow.readAll(reader(0)));
    }
}
