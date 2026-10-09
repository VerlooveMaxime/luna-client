package idlers;

/**
 * Frames a model in a widget: the model is moved so the middle of its bounds sits at the origin, the point the
 * client's model widgets look at, and the camera stands back so models keep their real sizes within limits (Maxime,
 * 2026-10-09): one as big as {@link #REFERENCE} or bigger fills the widget, seen from its pitch and yaw; a smaller one
 * shows smaller, down to {@link #SMALLEST} of the widget.
 */
public final class ModelFit {

    /** Share of the widget the model fills, the rest left for its animation to move in. */
    static final double FILL = 0.9;

    /** The client projects a point at depth z at {@code 512 / z} pixels per model unit. */
    private static final double FOCAL = 512;

    /**
     * Half the size, in model units, of a model that just fills the widget: a man (about 103 units from his middle to
     * his head) fills about two-thirds of it.
     */
    static final double REFERENCE = 160;

    /** The smallest share of the widget a model is shown at, so a rat stays readable. */
    static final double SMALLEST = 0.5;

    /** Closest a vertex may come to the camera; the client divides by depth without clipping. */
    private static final double NEAREST = 50;

    private ModelFit() {
    }

    /** The move that centres a model. */
    public record Centre(int dx, int dy, int dz) {
    }

    public static Centre centre(int[] xs, int[] ys, int[] zs, int count) {
        return new Centre(-middle(xs, count), -middle(ys, count), -middle(zs, count));
    }

    /**
     * The camera's distance for a centred model turned by {@code yaw} and seen from {@code pitch} (2048 units a turn), as
     * {@code client.method142} turns a model widget: as far as a model of {@link #REFERENCE} needs to fill the widget,
     * nearer for a bigger model so it fits, and never so far that the model shows below {@link #SMALLEST} of it.
     */
    public static int zoom(int[] xs, int[] ys, int[] zs, int count, int pitch, int yaw, int width, int height) {
        int filling = fillingZoom(xs, ys, zs, count, pitch, yaw, width, height);
        double reference = FOCAL * REFERENCE / (Math.min(width, height) * FILL / 2);
        return (int) Math.ceil(Math.max(filling, Math.min(filling / SMALLEST, reference)));
    }

    /** The camera's distance at which the model fills {@link #FILL} of the widget. */
    static int fillingZoom(int[] xs, int[] ys, int[] zs, int count, int pitch, int yaw, int width, int height) {
        double sinYaw = sin(yaw);
        double cosYaw = cos(yaw);
        double sinPitch = sin(pitch);
        double cosPitch = cos(pitch);
        double halfWidth = width * FILL / 2;
        double halfHeight = height * FILL / 2;
        double zoom = 0;
        for (int i = 0; i < count; i++) {
            double x = zs[i] * sinYaw + xs[i] * cosYaw;
            double z = zs[i] * cosYaw - xs[i] * sinYaw;
            double y = ys[i] * cosPitch - z * sinPitch;
            double depth = ys[i] * sinPitch + z * cosPitch;
            zoom = Math.max(zoom, Math.max(FOCAL * Math.abs(x) / halfWidth, FOCAL * Math.abs(y) / halfHeight) - depth);
            zoom = Math.max(zoom, NEAREST - depth);
        }
        return (int) Math.ceil(zoom);
    }

    private static double sin(int angle) {
        return Math.sin(angle * Math.PI / 1024);
    }

    private static double cos(int angle) {
        return Math.cos(angle * Math.PI / 1024);
    }

    private static int middle(int[] values, int count) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            min = Math.min(min, values[i]);
            max = Math.max(max, values[i]);
        }
        return (int) (((long) min + max) / 2);
    }
}
