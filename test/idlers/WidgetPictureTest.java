package idlers;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WidgetPictureTest {

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
    void noneClearsThePicture() {
        assertEquals(new WidgetPicture.None(), WidgetPicture.read(reader(WidgetPicture.NONE)));
    }

    @Test
    void aMediaSpriteIsANameAndAnIndex() {
        assertEquals(new WidgetPicture.Media("staticons", 17), WidgetPicture.read(reader(WidgetPicture.MEDIA, "staticons", 17)));
    }

    @Test
    void anItemIsItsId() {
        assertEquals(new WidgetPicture.Item(1059), WidgetPicture.read(reader(WidgetPicture.ITEM, 1059)));
    }

    @Test
    void anNpcBodyIsTheNpcsId() {
        assertEquals(new WidgetPicture.NpcBody(81), WidgetPicture.read(reader(WidgetPicture.NPC_BODY, 81)));
    }

    @Test
    void anUnknownSourceFailsLoudly() {
        assertThrows(IllegalArgumentException.class, () -> WidgetPicture.read(reader(9)));
    }
}
