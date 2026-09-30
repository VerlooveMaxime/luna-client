package idlers;

/** Where the fixed-size game screen is drawn inside a window: one uniform scale, centred in the content area. */
public record GameViewport(double scale, int offsetX, int offsetY) {

    public static final String SCALE_VARIABLE = "IDLERS_CLIENT_SCALE";

    private static final double MIN_SCALE = 1.0;

    /** The largest scale at which the game fits the area, never below 1, centred in it. */
    public static GameViewport fit(int gameWidth, int gameHeight, int areaX, int areaY, int areaWidth, int areaHeight) {
        double scale = Math.max(MIN_SCALE,
                Math.min((double) areaWidth / gameWidth, (double) areaHeight / gameHeight));
        return new GameViewport(scale,
                areaX + (int) Math.round((areaWidth - gameWidth * scale) / 2),
                areaY + (int) Math.round((areaHeight - gameHeight * scale) / 2));
    }

    /**
     * The scale the window opens at: {@code configured} when it is a number, otherwise ("auto", blank or absent) the
     * largest whole scale that fits the free screen space, so every game pixel stays the same size.
     */
    public static double initialScale(String configured, int gameWidth, int gameHeight, int maxWidth, int maxHeight) {
        if (configured == null || configured.isBlank() || configured.strip().equalsIgnoreCase("auto")) {
            return Math.max(MIN_SCALE, Math.min(maxWidth / gameWidth, maxHeight / gameHeight));
        }
        double scale = parseScale(configured.strip());
        if (!(scale >= MIN_SCALE) || Double.isInfinite(scale)) {
            throw invalidScale(configured);
        }
        return scale;
    }

    public int toGameX(int windowX) {
        return (int) Math.floor((windowX - offsetX) / scale);
    }

    public int toGameY(int windowY) {
        return (int) Math.floor((windowY - offsetY) / scale);
    }

    private static double parseScale(String configured) {
        try {
            return Double.parseDouble(configured);
        } catch (NumberFormatException e) {
            throw invalidScale(configured);
        }
    }

    private static IllegalArgumentException invalidScale(String configured) {
        return new IllegalArgumentException(
                SCALE_VARIABLE + " must be \"auto\" or a number of at least 1, got \"" + configured + "\"");
    }
}
