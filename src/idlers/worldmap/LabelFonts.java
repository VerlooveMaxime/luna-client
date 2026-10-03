package idlers.worldmap;

import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntFunction;

/**
 * The label fonts: bold "Helvetica" in the pixel sizes the applet picked per label size and zoom. Linux rarely has
 * Helvetica itself, so the first installed family of {@link #PREFERRED_FAMILIES} stands in, else AWT's sans serif.
 */
public final class LabelFonts {

    public static final int SMALL = 0;
    public static final int TOWN = 1;
    public static final int KINGDOM = 2;

    static final List<String> PREFERRED_FAMILIES = List.of("Helvetica", "Arial", "Liberation Sans");

    /** Pixel size per label size (rows) and zoom level (columns, in {@link ZoomLevel} order). */
    private static final int[][] SIZES = {
            {11, 12, 14, 17},
            {14, 17, 19, 22},
            {19, 22, 26, 30},
    };

    private final Map<Integer, BitmapFont> bySize;

    LabelFonts(IntFunction<BitmapFont> fontOfSize) {
        Map<Integer, BitmapFont> fonts = new TreeMap<>();
        for (int[] row : SIZES) {
            for (int size : row) {
                fonts.computeIfAbsent(size, fontOfSize::apply);
            }
        }
        bySize = Map.copyOf(fonts);
    }

    /** Rasterises every needed size of the first preferred family among {@code installedFamilies}. */
    public static LabelFonts create(Set<String> installedFamilies) {
        String family = family(installedFamilies);
        return new LabelFonts(size -> BitmapFont.fromAwt(new Font(family, Font.BOLD, size)));
    }

    static String family(Set<String> installedFamilies) {
        for (String family : PREFERRED_FAMILIES) {
            if (installedFamilies.contains(family)) {
                return family;
            }
        }
        return Font.SANS_SERIF;
    }

    static int pixelSize(int labelSize, ZoomLevel zoom) {
        int row = Math.max(SMALL, Math.min(KINGDOM, labelSize));
        return SIZES[row][zoom.ordinal()];
    }

    public BitmapFont font(int labelSize, ZoomLevel zoom) {
        return bySize.get(pixelSize(labelSize, zoom));
    }
}
