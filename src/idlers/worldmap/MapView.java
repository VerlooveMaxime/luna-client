package idlers.worldmap;

/**
 * Which part of the map the window shows, in the applet's terms: a focus tile (column, row from the north edge), a
 * zoom that eases towards the chosen {@link ZoomLevel}, dragging, and clamping that keeps a margin of
 * {@link #MARGIN} tiles inside the map.
 */
public final class MapView {

    static final int MARGIN = 48;
    private static final double ZOOM_DIVISOR = 30.0;

    private final int mapWidth;
    private final int mapHeight;
    private final int viewWidth;
    private final int viewHeight;
    private int focusColumn;
    private int focusRow;
    private double zoom;
    private ZoomLevel target;
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;
    private int dragFocusColumn;
    private int dragFocusRow;

    public MapView(int mapWidth, int mapHeight, int viewWidth, int viewHeight, ZoomLevel level) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.target = level;
        this.zoom = level.factor();
        this.focusColumn = mapWidth / 2;
        this.focusRow = mapHeight / 2;
    }

    public int focusColumn() {
        return focusColumn;
    }

    public int focusRow() {
        return focusRow;
    }

    public double zoom() {
        return zoom;
    }

    public ZoomLevel target() {
        return target;
    }

    /** Labels and the overview box wait until the zoom has settled, as in the applet. */
    public boolean settled() {
        return zoom == target.factor();
    }

    public int left() {
        return focusColumn - halfSpan(viewWidth);
    }

    public int right() {
        return focusColumn + halfSpan(viewWidth);
    }

    public int top() {
        return focusRow - halfSpan(viewHeight);
    }

    public int bottom() {
        return focusRow + halfSpan(viewHeight);
    }

    private int halfSpan(int pixels) {
        return (int) (pixels / zoom);
    }

    public void centreOn(int column, int row) {
        focusColumn = column;
        focusRow = row;
        clamp();
    }

    public void zoomTo(ZoomLevel level) {
        target = level;
    }

    public void startDrag(int x, int y) {
        dragging = true;
        dragStartX = x;
        dragStartY = y;
        dragFocusColumn = focusColumn;
        dragFocusRow = focusRow;
    }

    public void dragTo(int x, int y) {
        if (dragging) {
            focusColumn = dragFocusColumn + (int) ((dragStartX - x) * 2.0 / target.factor());
            focusRow = dragFocusRow + (int) ((dragStartY - y) * 2.0 / target.factor());
            clamp();
        }
    }

    public void endDrag() {
        dragging = false;
    }

    public boolean dragging() {
        return dragging;
    }

    /** One client tick: eases the zoom a thirtieth of the way, as the applet's main loop did. */
    public void tick() {
        double goal = target.factor();
        if (zoom < goal) {
            zoom = Math.min(goal, zoom + zoom / ZOOM_DIVISOR);
        } else if (zoom > goal) {
            zoom = Math.max(goal, zoom - zoom / ZOOM_DIVISOR);
        }
        clamp();
    }

    private void clamp() {
        if (left() < MARGIN) {
            focusColumn = halfSpan(viewWidth) + MARGIN;
        }
        if (top() < MARGIN) {
            focusRow = halfSpan(viewHeight) + MARGIN;
        }
        if (right() > mapWidth - MARGIN) {
            focusColumn = mapWidth - MARGIN - halfSpan(viewWidth);
        }
        if (bottom() > mapHeight - MARGIN) {
            focusRow = mapHeight - MARGIN - halfSpan(viewHeight);
        }
    }

    /** The map column under view pixel {@code x}. */
    public int columnAt(int x) {
        return left() + (int) Math.floor((double) x * (right() - left()) / viewWidth);
    }

    /** The map row under view pixel {@code y}. */
    public int rowAt(int y) {
        return top() + (int) Math.floor((double) y * (bottom() - top()) / viewHeight);
    }

    /** The view pixel of a point given in map columns (fractions allowed, so a tile's centre is column + 0.5). */
    public int pixelX(double column) {
        return (int) (viewWidth * (column - left()) / (right() - left()));
    }

    public int pixelY(double row) {
        return (int) (viewHeight * (row - top()) / (bottom() - top()));
    }
}
