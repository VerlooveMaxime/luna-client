package idlers;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchOpeningTest {

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
    void aSearchOpensWithItsEmptyLine() {
        assertEquals(new SearchOpening(4, SearchPrompt.Mode.SEARCH, "Which item?", "Nothing to pick", "", 40),
                SearchOpening.read(reader(4, 0, "Which item?", "Nothing to pick", "", 40)));
    }

    @Test
    void aNamePromptOpensWithTheTextOnItsLine() {
        assertEquals(new SearchOpening(5, SearchPrompt.Mode.NAME, "Save over 'Cows' as:", "", "Cows", 20),
                SearchOpening.read(reader(5, 1, "Save over 'Cows' as:", "", "Cows", 20)));
    }
}
