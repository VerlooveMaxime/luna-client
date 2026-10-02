package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusOverlayTest {

    @Test
    void startsEmpty() {
        assertTrue(new StatusOverlay().isEmpty());
    }

    @Test
    void splitsTheTextIntoLines() {
        StatusOverlay overlay = new StatusOverlay();

        overlay.set("@gre@Autopilot@whi@ step 1/3|chop oak @draynor_oaks until inventory full");

        assertEquals(List.of("@gre@Autopilot@whi@ step 1/3", "chop oak @draynor_oaks until inventory full"), overlay.lines());
    }

    @Test
    void dropsBlankLinesAndSurroundingSpaces() {
        StatusOverlay overlay = new StatusOverlay();

        overlay.set(" first | | second ");

        assertEquals(List.of("first", "second"), overlay.lines());
    }

    @Test
    void anEmptyTextClearsTheOverlay() {
        StatusOverlay overlay = new StatusOverlay();
        overlay.set("running");

        overlay.set("");

        assertTrue(overlay.isEmpty());
    }

    @Test
    void clearForgetsTheLines() {
        StatusOverlay overlay = new StatusOverlay();
        overlay.set("running");

        overlay.clear();

        assertTrue(overlay.isEmpty());
        assertFalse(overlay.lines().contains("running"));
    }

    @Test
    void linesStackDownwardsFromTheTop() {
        assertEquals(StatusOverlay.TOP, StatusOverlay.lineY(0));
        assertEquals(StatusOverlay.TOP + 2 * StatusOverlay.LINE_HEIGHT, StatusOverlay.lineY(2));
    }
}
