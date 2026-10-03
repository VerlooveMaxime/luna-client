package idlers;

/**
 * The compass's right-click menu, which the 377 client never had: "Face North" (the left-click default) and "World
 * map". The menu opens in a fourth menu area drawn into the minimap image, whose top-left corner is at
 * ({@link #AREA_X}, {@link #AREA_Y}) in the 765 x 503 frame.
 */
public final class CompassMenu {

    /** Menu area id next to the client's 0 (game view), 1 (tab panel) and 2 (chatbox). */
    public static final int MENU_AREA = 3;
    public static final int AREA_X = 550;
    public static final int AREA_Y = 4;
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

    /** Whether frame point (x, y) is on the compass disc. */
    public static boolean contains(int x, int y) {
        double radius = COMPASS_SIZE / 2.0;
        double dx = x - (AREA_X + radius);
        double dy = y - (AREA_Y + radius);
        return dx * dx + dy * dy <= radius * radius;
    }

    /** The menu's left edge inside the minimap image: centred on the click, kept inside the image. */
    public static int menuX(int clickX, int menuWidth) {
        return Math.max(0, Math.min(AREA_WIDTH - menuWidth, clickX - AREA_X - menuWidth / 2));
    }

    /** The menu's top edge inside the minimap image: at the click, kept inside the image. */
    public static int menuY(int clickY, int menuHeight) {
        return Math.max(0, Math.min(AREA_HEIGHT - menuHeight, clickY - AREA_Y));
    }
}
