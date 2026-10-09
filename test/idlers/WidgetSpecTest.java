package idlers;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WidgetSpecTest {

    @Test
    void aPlainLayerDoesNotScroll() {
        assertFalse(WidgetSpec.layer(1, -1, 0, 0, 50, 40, List.of()).scrolls());
    }

    @Test
    void aLayerTallerInsideThanOutsideScrolls() {
        assertTrue(WidgetSpec.scrollLayer(1, -1, 0, 0, 50, 40, 41, List.of()).scrolls());
    }

    @Test
    void aLayerKeepsItsOwnCopyOfTheChildren() {
        List<Integer> children = new ArrayList<>(List.of(2));
        WidgetSpec.Layer layer = WidgetSpec.layer(1, -1, 0, 0, 50, 40, children);

        children.add(3);

        assertEquals(List.of(2), layer.children());
        assertThrows(UnsupportedOperationException.class, () -> layer.children().add(4));
    }

    @Test
    void aBoxIsFilledAndAFrameIsOnlyItsOutline() {
        assertTrue(WidgetSpec.box(1, -1, 0, 0, 5, 5, 0).filled());
        assertFalse(WidgetSpec.frame(1, -1, 0, 0, 5, 5, 0).filled());
    }

    @Test
    void aCentredTextIsCentredOnItsXWithNoWidth() {
        WidgetSpec.Text text = WidgetSpec.centredText(1, -1, 256, 6, 14, "Title", 0, WidgetSpec.FONT_BOLD);

        assertTrue(text.centred());
        assertEquals(0, text.width());
    }

    @Test
    void aPlainTextIsNotCentred() {
        assertFalse(WidgetSpec.text(1, -1, 0, 0, 10, 14, "", 0, WidgetSpec.FONT_PLAIN).centred());
    }
}
