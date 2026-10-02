package idlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lines the server asks the client to draw over the game view: what the autopilot is doing. Empty when it is off.
 * The server sends all lines in one string, separated by {@link #LINE_SEPARATOR} because a newline ends a packet
 * string.
 */
public final class StatusOverlay {

    public static final char LINE_SEPARATOR = '|';

    /**
     * Game-view coordinates (512 x 334): lines are right-aligned to {@link #RIGHT}, away from the hover text the client
     * writes in the top-left corner; the FPS counter (developers only) uses the same edge.
     */
    public static final int RIGHT = 507;
    public static final int TOP = 20;
    public static final int LINE_HEIGHT = 15;

    private List<String> lines = Collections.emptyList();

    /** Replaces the lines with those in {@code text}; blank lines are dropped, an empty text clears the overlay. */
    public void set(String text) {
        List<String> parsed = new ArrayList<>();
        for (String line : text.split("\\" + LINE_SEPARATOR)) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                parsed.add(trimmed);
            }
        }
        lines = Collections.unmodifiableList(parsed);
    }

    public void clear() {
        lines = Collections.emptyList();
    }

    public List<String> lines() {
        return lines;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public static int lineY(int index) {
        return TOP + index * LINE_HEIGHT;
    }
}
