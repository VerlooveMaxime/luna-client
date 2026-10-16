package idlers;

/**
 * The compass's right-click menu, which the 377 client never had: "Face North" (the left-click default) and "World
 * map". The menu opens in a fourth menu area drawn into the minimap image, whose top-left corner the client's layout
 * places: (550, 4) in the fixed mode, the window's top-right corner in the resizable mode.
 */
public final class CompassMenu {

    /** Menu area id next to the client's 0 (game view), 1 (tab panel) and 2 (chatbox). */
    public static final int MENU_AREA = 3;
    public static final int AREA_WIDTH = 172;
    public static final int AREA_HEIGHT = 156;

    /** Client-only menu action ids, from a range the client leaves unused (below 2000, which it would shift). */
    public static final int FACE_NORTH_ACTION = 1100;
    public static final int WORLD_MAP_ACTION = 1101;
    public static final String FACE_NORTH = "Face North";
    public static final String WORLD_MAP = "World map";

    /** The compass is a 33 x 33 disc at the top-left corner of the minimap image. */
    static final int COMPASS_SIZE = 33;

    private CompassMenu() {
    }

    /** Whether frame point (x, y) is on the compass disc of a minimap image whose top-left is (areaX, areaY). */
    public static boolean contains(int x, int y, int areaX, int areaY) {
        double radius = COMPASS_SIZE / 2.0;
        double dx = x - (areaX + radius);
        double dy = y - (areaY + radius);
        return dx * dx + dy * dy <= radius * radius;
    }

    /** The menu's left edge inside the minimap image at {@code areaX}: centred on the click, kept inside the image. */
    public static int menuX(int clickX, int menuWidth, int areaX) {
        return Math.max(0, Math.min(AREA_WIDTH - menuWidth, clickX - areaX - menuWidth / 2));
    }

    /** The menu's top edge inside the minimap image at {@code areaY}: at the click, kept inside the image. */
    public static int menuY(int clickY, int menuHeight, int areaY) {
        return Math.max(0, Math.min(AREA_HEIGHT - menuHeight, clickY - areaY));
    }
}
