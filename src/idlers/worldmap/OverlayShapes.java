package idlers.worldmap;

/**
 * The applet's {@code DrawOverlayShape} as a table: which pixels of a tile drawn {@code width} by {@code height}
 * take the overlay colour and which the underlay, for shapes 1-11 in four rotations. Each original loop walks x and
 * y forwards or backwards and tests one inequality; the table keeps exactly those.
 */
public final class OverlayShapes {

    private enum Test {
        X_UP_TO_Y, X_FROM_Y,
        X_UP_TO_HALF_Y, X_FROM_DOUBLE_Y, X_FROM_HALF_Y, X_UP_TO_DOUBLE_Y,
        X_UP_TO_HALF_WIDTH, Y_UP_TO_HALF_HEIGHT, X_FROM_HALF_WIDTH, Y_FROM_HALF_HEIGHT,
        X_UP_TO_Y_LESS_HALF_HEIGHT, X_FROM_Y_LESS_HALF_HEIGHT;

        boolean holds(int x, int y, int width, int height) {
            return switch (this) {
                case X_UP_TO_Y -> x <= y;
                case X_FROM_Y -> x >= y;
                case X_UP_TO_HALF_Y -> x <= y >> 1;
                case X_FROM_DOUBLE_Y -> x >= y << 1;
                case X_FROM_HALF_Y -> x >= y >> 1;
                case X_UP_TO_DOUBLE_Y -> x <= y << 1;
                case X_UP_TO_HALF_WIDTH -> x <= width / 2;
                case Y_UP_TO_HALF_HEIGHT -> y <= height / 2;
                case X_FROM_HALF_WIDTH -> x >= width / 2;
                case Y_FROM_HALF_HEIGHT -> y >= height / 2;
                case X_UP_TO_Y_LESS_HALF_HEIGHT -> x <= y - height / 2;
                case X_FROM_Y_LESS_HALF_HEIGHT -> x >= y - height / 2;
            };
        }
    }

    private record Rule(Test test, boolean reverseX, boolean reverseY) {
    }

    private static final Rule[][] RULES = {
            null,
            {rule(Test.X_UP_TO_Y, false, false), rule(Test.X_UP_TO_Y, false, true),
                    rule(Test.X_FROM_Y, false, false), rule(Test.X_FROM_Y, false, true)},
            {rule(Test.X_UP_TO_HALF_Y, false, true), rule(Test.X_FROM_DOUBLE_Y, false, false),
                    rule(Test.X_UP_TO_HALF_Y, true, false), rule(Test.X_FROM_DOUBLE_Y, true, true)},
            {rule(Test.X_UP_TO_HALF_Y, true, true), rule(Test.X_FROM_DOUBLE_Y, false, true),
                    rule(Test.X_UP_TO_HALF_Y, false, false), rule(Test.X_FROM_DOUBLE_Y, true, false)},
            {rule(Test.X_FROM_HALF_Y, false, true), rule(Test.X_UP_TO_DOUBLE_Y, false, false),
                    rule(Test.X_FROM_HALF_Y, true, false), rule(Test.X_UP_TO_DOUBLE_Y, true, true)},
            {rule(Test.X_FROM_HALF_Y, true, true), rule(Test.X_UP_TO_DOUBLE_Y, false, true),
                    rule(Test.X_FROM_HALF_Y, false, false), rule(Test.X_UP_TO_DOUBLE_Y, true, false)},
            {rule(Test.X_UP_TO_HALF_WIDTH, false, false), rule(Test.Y_UP_TO_HALF_HEIGHT, false, false),
                    rule(Test.X_FROM_HALF_WIDTH, false, false), rule(Test.Y_FROM_HALF_HEIGHT, false, false)},
            {rule(Test.X_UP_TO_Y_LESS_HALF_HEIGHT, false, false), rule(Test.X_UP_TO_Y_LESS_HALF_HEIGHT, false, true),
                    rule(Test.X_UP_TO_Y_LESS_HALF_HEIGHT, true, true), rule(Test.X_UP_TO_Y_LESS_HALF_HEIGHT, true, false)},
            {rule(Test.X_FROM_Y_LESS_HALF_HEIGHT, false, false), rule(Test.X_FROM_Y_LESS_HALF_HEIGHT, false, true),
                    rule(Test.X_FROM_Y_LESS_HALF_HEIGHT, true, true), rule(Test.X_FROM_Y_LESS_HALF_HEIGHT, true, false)},
    };

    private OverlayShapes() {
    }

    private static Rule rule(Test test, boolean reverseX, boolean reverseY) {
        return new Rule(test, reverseX, reverseY);
    }

    /** Whether pixel (x, y) of the tile, counted from its top-left corner, shows the overlay. */
    public static boolean isOverlay(int shape, int rotation, int x, int y, int width, int height) {
        int baseShape = shape;
        int baseRotation = rotation;
        if (shape == 9) {
            baseShape = 1;
            baseRotation = (rotation + 1) & 3;
        } else if (shape == 10) {
            baseShape = 1;
            baseRotation = (rotation + 3) & 3;
        } else if (shape == 11) {
            baseShape = 8;
            baseRotation = (rotation + 3) & 3;
        }
        if (baseShape < 1 || baseShape >= RULES.length) {
            return true;
        }
        Rule rule = RULES[baseShape][baseRotation];
        int testX = rule.reverseX() ? width - 1 - x : x;
        int testY = rule.reverseY() ? height - 1 - y : y;
        return rule.test().holds(testX, testY, width, height);
    }

    public static void fill(Raster raster, int left, int top, int width, int height, int underlay, int overlay,
                            int shape, int rotation) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                raster.plot(left + x, top + y, isOverlay(shape, rotation, x, y, width, height) ? overlay : underlay);
            }
        }
    }
}
