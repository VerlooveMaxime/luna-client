package idlers;

import java.util.stream.IntStream;

/**
 * Frames a model in a widget: the model is moved so the middle of its bounds sits at the origin, the point the
 * client's model widgets look at, and the camera stands back so the model fills {@link #FILL} of the widget, seen from
 * its pitch and yaw, whatever its size (Maxime, 2026-10-09: small bodies were hard to read in the search's cells).
 * Only its {@link #framed} vertices count.
 */
public final class ModelFit {

    /** Share of the widget the model fills, the rest left for its animation to move in. */
    static final double FILL = 0.9;

    /** The client projects a point at depth z at {@code 512 / z} pixels per model unit. */
    private static final double FOCAL = 512;

    /** Faces at least this see-through (0 opaque, 255 invisible) do not count when framing. */
    static final int SEE_THROUGH = 128;

    /** Closest a vertex may come to the camera; the client divides by depth without clipping. */
    private static final double NEAREST = 50;

    private ModelFit() {
    }

    /** The coordinates of some of a model's vertices, the first {@code count} of each array. */
    public record Vertices(int[] xs, int[] ys, int[] zs, int count) {
    }

    /**
     * The vertices a model is framed on: those of its faces less than half see-through, or all of them when it has no
     * such face. A duck's water ripples, see-through faces around it, would otherwise shrink the duck to a third of
     * its frame. {@code transparency} is null for a model whose faces are all opaque.
     */
    public static Vertices framed(int[] xs, int[] ys, int[] zs, int vertexCount, int[] facesA, int[] facesB, int[] facesC,
            int[] transparency, int faceCount) {
        boolean[] solid = new boolean[vertexCount];
        for (int face = 0; face < faceCount; face++) {
            if (transparency == null || transparency[face] < SEE_THROUGH) {
                solid[facesA[face]] = true;
                solid[facesB[face]] = true;
                solid[facesC[face]] = true;
            }
        }
        int[] kept = IntStream.range(0, vertexCount).filter(vertex -> solid[vertex]).toArray();
        if (kept.length == 0) {
            return new Vertices(xs, ys, zs, vertexCount);
        }
        return new Vertices(pick(xs, kept), pick(ys, kept), pick(zs, kept), kept.length);
    }

    private static int[] pick(int[] values, int[] indexes) {
        return IntStream.of(indexes).map(index -> values[index]).toArray();
    }

    /** The move that centres a model. */
    public record Centre(int dx, int dy, int dz) {
    }

    public static Centre centre(int[] xs, int[] ys, int[] zs, int count) {
        return new Centre(-middle(xs, count), -middle(ys, count), -middle(zs, count));
    }

    /**
     * The camera's distance at which a centred model turned by {@code yaw} and seen from {@code pitch} (2048 units a
     * turn), as {@code client.method142} turns a model widget, fills {@link #FILL} of the widget.
     */
    public static int zoom(int[] xs, int[] ys, int[] zs, int count, int pitch, int yaw, int width, int height) {
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
