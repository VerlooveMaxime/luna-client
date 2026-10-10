package idlers;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchPageTest {

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
    void aPageIsItsHeaderThenItsRows() {
        SearchPage page = SearchPage.read(reader(4, "oak", 120, 2, 30, 1, 31, 0, "Oak logs", "312 in bank", WidgetPicture.ITEM, 1521));

        assertEquals(new SearchPage(4, "oak", 120, 2, 30,
                List.of(new SearchRow(31, "Oak logs", "312 in bank", false, new WidgetPicture.Item(1521), false))), page);
    }
}
