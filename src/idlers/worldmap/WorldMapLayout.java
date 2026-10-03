package idlers.worldmap;

import java.util.List;

/**
 * Where the window's panels and buttons sit, after the applet's layout (Key bottom left, zoom buttons bottom centre,
 * Overview bottom right) squeezed into a view of the given size, plus a Close button the applet did not need.
 */
public record WorldMapLayout(Rect close, Rect keyToggle, Rect keyPanel, Rect previousPage, Rect nextPage,
                             Rect overviewToggle, Rect overview, List<Rect> zoomButtons) {

    public record Rect(int x, int y, int width, int height) {

        public boolean contains(int pointX, int pointY) {
            return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
        }
    }

    static final int GAP = 4;
    static final int BAR_HEIGHT = 18;
    static final int KEY_WIDTH = 140;
    static final int KEY_ROW_HEIGHT = 17;
    static final int KEY_LIST_PADDING = 3;
    static final int ZOOM_WIDTH = 50;
    static final int ZOOM_HEIGHT = 30;
    static final int CLOSE_WIDTH = 50;

    public static WorldMapLayout of(int viewWidth, int viewHeight, int overviewWidth, int overviewHeight) {
        int barY = viewHeight - GAP - BAR_HEIGHT;
        Rect keyPanel = new Rect(GAP, GAP, KEY_WIDTH, barY - GAP);
        Rect overviewToggle = new Rect(viewWidth - GAP - overviewWidth, barY, overviewWidth, BAR_HEIGHT);
        Rect[] zoom = new Rect[ZoomLevel.values().length];
        for (int i = 0; i < zoom.length; i++) {
            zoom[i] = new Rect(GAP + KEY_WIDTH + GAP * 3 + i * (ZOOM_WIDTH + GAP), viewHeight - GAP - ZOOM_HEIGHT,
                    ZOOM_WIDTH, ZOOM_HEIGHT);
        }
        return new WorldMapLayout(
                new Rect(viewWidth - GAP - CLOSE_WIDTH, GAP, CLOSE_WIDTH, BAR_HEIGHT),
                new Rect(GAP, barY, KEY_WIDTH, BAR_HEIGHT),
                keyPanel,
                new Rect(keyPanel.x(), keyPanel.y(), KEY_WIDTH, BAR_HEIGHT),
                new Rect(keyPanel.x(), keyPanel.y() + keyPanel.height() - BAR_HEIGHT, KEY_WIDTH, BAR_HEIGHT),
                overviewToggle,
                new Rect(overviewToggle.x(), barY - overviewHeight, overviewWidth, overviewHeight),
                List.of(zoom));
    }

    /** How many key names fit between the page buttons. */
    public int keyRows() {
        return (keyPanel.height() - 2 * BAR_HEIGHT - KEY_LIST_PADDING) / KEY_ROW_HEIGHT;
    }

    /** The top of key row {@code row}. */
    public int keyRowY(int row) {
        return keyPanel.y() + BAR_HEIGHT + KEY_LIST_PADDING + row * KEY_ROW_HEIGHT;
    }

    /** The key row under {@code y}, or -1 outside the list. */
    public int keyRowAt(int x, int y) {
        int offset = y - keyRowY(0);
        if (x < keyPanel.x() || x >= keyPanel.x() + KEY_WIDTH || offset < 0) {
            return -1;
        }
        int row = offset / KEY_ROW_HEIGHT;
        return row < keyRows() ? row : -1;
    }
}
