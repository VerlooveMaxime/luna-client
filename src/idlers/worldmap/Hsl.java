package idlers.worldmap;

/** The applet's HSL to RGB conversion (hue, saturation, lightness in 0..1), kept bit for bit. */
public final class Hsl {

    private static final double THIRD = 1.0 / 3.0;
    private static final double TWO_THIRDS = 2.0 / 3.0;

    private Hsl() {
    }

    public static int toRgb(double hue, double saturation, double lightness) {
        double red = lightness;
        double green = lightness;
        double blue = lightness;
        if (saturation != 0.0) {
            double q = lightness < 0.5 ? lightness * (saturation + 1.0) : lightness + saturation - lightness * saturation;
            double p = lightness * 2.0 - q;
            double redHue = hue + THIRD;
            if (redHue > 1.0) {
                redHue--;
            }
            double blueHue = hue - THIRD;
            if (blueHue < 0.0) {
                blueHue++;
            }
            red = channel(p, q, redHue);
            green = channel(p, q, hue);
            blue = channel(p, q, blueHue);
        }
        return ((int) (red * 256.0) << 16) + ((int) (green * 256.0) << 8) + (int) (blue * 256.0);
    }

    private static double channel(double p, double q, double hue) {
        if (hue * 6.0 < 1.0) {
            return p + (q - p) * 6.0 * hue;
        }
        if (hue * 2.0 < 1.0) {
            return q;
        }
        if (hue * 3.0 < 2.0) {
            return p + (q - p) * (TWO_THIRDS - hue) * 6.0;
        }
        return p;
    }
}
