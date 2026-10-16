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
     * Game-view coordinates: lines are right-aligned {@link #MARGIN} pixels in from the right edge of the visible view,
     * away from the hover text the client writes in the top-left corner. In the fixed mode that edge is the view's own
     * (512 wide, so 507, where the FPS counter for developers also ends); in the resizable mode it is the minimap's left.
     */
    public static final int MARGIN = 5;
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

    /** Where lines end in a view whose visible part is {@code visibleWidth} wide. */
    public static int right(int visibleWidth) {
        return visibleWidth - MARGIN;
    }

    public static int lineY(int index) {
        return TOP + index * LINE_HEIGHT;
    }
}
